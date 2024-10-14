package org.example.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.example.entity.NotifyDto;
import org.example.repository.DemoRepository;
import org.example.service.DemoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;

import javax.annotation.Resource;
import java.sql.Array;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DemoController {
    @Resource
    private DemoRepository demoRepository;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${stream.key:purchase-stream-events}")
    private String streamKey;


    // 消息队列
    final String queue = "names";
    final String pendingQueue = "pengindNames";

    @RequestMapping("/test/{name}")
    public List<DemoEntity> test(@PathVariable String name) throws InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        List<DemoEntity> list = demoRepository.selectByMap(map);

        if (list.isEmpty()) {
            return list;
        }

        Cache names = cacheManager.getCache("names");
        if (names != null) {
            LinkedBlockingQueue nameCollect = names.get("nameCollect", LinkedBlockingQueue.class);
            if (nameCollect != null) {
                if (nameCollect.remainingCapacity() == 0) {
                    throw new RuntimeException("队列已满");
                }
                nameCollect.put(name);
            } else {
                LinkedBlockingQueue<String> newCollect = new LinkedBlockingQueue<>(2);
                newCollect.put(name);
                names.put("nameCollect", newCollect);
            }
        }
        return list;
    }

    @RequestMapping("/test/redis/{name}")
    public List<DemoEntity> testRedis(@PathVariable String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        List<DemoEntity> list = demoRepository.selectByMap(map);

        if (list.isEmpty()) {
            log.info("找不到用户");
            return list;
        }

        // 放入redis缓存
        stringRedisTemplate.opsForList().leftPush(queue, name);

        return list;
    }

    @RequestMapping("/test/redis/stream/v1/{name}")
    public List<DemoEntity> testRedisStreamV1(@PathVariable String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        List<DemoEntity> list = demoRepository.selectByMap(map);

        if (list.isEmpty()) {
            log.info("找不到用户");
            return list;
        }

        // 放入redis缓存
        ObjectRecord<String, String> record =
                StreamRecords.newRecord().ofObject(name).withStreamKey(streamKey);

        RecordId recordId = redisTemplate.opsForStream().add(record);

        if (Objects.isNull(recordId)) {
            log.error("缓存失败");
        } else {
            log.info("缓存成功");
        }

        return list;
    }

    @RequestMapping("/test/redis/stream/v2/{name}")
    public List<DemoEntity> testRedisStreamV2(@PathVariable String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        List<DemoEntity> list = demoRepository.selectByMap(map);

        if (list.isEmpty()) {
            log.info("找不到用户");
            return list;
        }

        // 放入redis缓存
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        ObjectRecord<String, String> record =
                StreamRecords.newRecord().ofObject(name).withStreamKey(streamKey);
        reactiveRedisTemplate.opsForStream().add(record).subscribe();

        log.info("缓存成功");

        return list;
    }

    @Scheduled(fixedDelay = 5000)
    public void test() throws InterruptedException {
//        Cache names = cacheManager.getCache("names");
//        assert names != null;
//        LinkedBlockingQueue nameCollect = names.get("nameCollect", LinkedBlockingQueue.class);
//        if (nameCollect != null && !nameCollect.isEmpty()) {
//            Object obj = nameCollect.peek();
//            log.info(obj.toString());
//            nameCollect.take();
//        }
        // 从redis中读取
        String name = stringRedisTemplate.opsForList().rightPopAndLeftPush(queue, pendingQueue, 5, TimeUnit.SECONDS);
        // redis stream

        log.info(name);
    }

    @Resource
    private DemoService demoService;

    @RequestMapping("/test-file")
    public void testFile() {
        demoService.readFile();
    }

    @RequestMapping("/test-concurrent")
    public void testConcurrent(@RequestParam String id) {
        redisTemplate.opsForValue().increment("test-concurrent-" + id, 1);
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/test-for-update")
    public String testForUpdate(@RequestParam Long id) {
        DemoEntity demoEntity = demoRepository.lockById(id);
        try {
            if (demoEntity != null) {
                Thread.sleep(15000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/test-redis-trans")
    public String testRedisTrans(@RequestParam String key) {
        try {
            Long i = stringRedisTemplate.opsForValue().increment(key, 1);
            if (i > 1) {
                throw new RuntimeException("duplicate");
            }
            if (key.equals("test-key-fail")) {
                throw new RuntimeException("fail key");
            }
            Instant instant = Instant.now().plus(5, ChronoUnit.MINUTES);
            stringRedisTemplate.opsForValue().getOperations().expireAt(key, instant);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/test-for-update-01")
    public String testForUpdate01(@RequestParam Long id) {
        long start = System.currentTimeMillis();
        DemoEntity demoEntity = demoRepository.lockById(id);
        if (demoEntity != null) {
            demoEntity.setName("Patsz");
            demoRepository.updateById(demoEntity);
        }
        long end = System.currentTimeMillis();
        log.info("time: {}", (end - start));
        return "success";
    }

    @RequestMapping("/redis-size-by-key")
    public Long redisSizeByKey(@RequestParam String key) {
        Long size = stringRedisTemplate.opsForList().size(key);
        return size;
    }

    @RequestMapping("/test-redis-sub")
    public void testRedisSub(@RequestParam String key) {
        redisTemplate.opsForValue().increment("test:" + key, 1);
        redisTemplate.opsForValue().getOperations().expireAt("test:" + key, Instant.now().plus(10, ChronoUnit.SECONDS));
    }

    /**
     * 占座超时
     */
    @Scheduled(fixedRate = 1000)
    public void schduleSeat() {
        long expireTime = Instant.now().toEpochMilli();
        String holdExpireKey = "seat:zset";
        String holdMapKeyPrefix = "seat:map:";
        Set<String> strings = redisTemplate.opsForZSet().rangeByScore(holdExpireKey, 0, expireTime);
        if (strings == null) {
            return;
        }

        String luaScript = "local holdKey = KEYS[1] \n" +
                "local holdMapKey = KEYS[2] \n" +
                "local holdExpireKey = KEYS[3] \n" +
                "local seatKeys = redis.call('HKEYS', holdMapKey) \n" +
                "for i=1,#seatKeys do \n" +
                "\tlocal offset = tonumber(seatKeys[i]) \n" +
                "\tredis.call('SETBIT', holdKey, offset, 0)\n" +
                "end \n" +
                "local seatValues = redis.call('HVALS', holdMapKey) \n" +
                "redis.call('DEL', holdMapKey) \n" +
                "redis.call('ZREM', holdExpireKey, holdMapKey) \n" +
                "return seatValues";

        for (String key : strings) {
            String[] setKeyArr = key.split(":");
            if (setKeyArr.length < holdMapKeyPrefix.split(":").length + 2) {
                log.error("错误的rediskey");
                continue;
            }
            String holdKey = "seat:bitmap:" + setKeyArr[2];
            List<String> keys = new ArrayList<>();
            keys.add(holdKey);
            keys.add(key);
            keys.add(holdExpireKey);

            Object[] values = new Object[10];

            RedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);
            try {
                List result = redisTemplate.execute(script, keys, values);
                log.error("占座超时释放成功: {}", result);
            } catch (Exception e) {
                log.error("占座超时释放失败：{}", e.getMessage(), e);
            }
        }
    }


    /**
     * 占座
     *
     * @param seatNum
     * @param userId
     */
    @RequestMapping("/test/seat/hold/{scheduleId}/{userId}/{seatNum}")
    public String holdSeat(@PathVariable long scheduleId, @PathVariable long seatNum, @PathVariable long userId) {
        String holdKey = "seat:bitmap:" + scheduleId;
        String holdMapKey = "seat:map:" + scheduleId + ":" + userId;
        String holdExpireKey = "seat:zset";

        String seatNumStr = String.valueOf(seatNum);

        List<String> keys = new ArrayList<>();
        keys.add(holdKey);
        keys.add(holdMapKey);
        keys.add(holdExpireKey);
        keys.add(seatNumStr);

        long epochMilli = Instant.now().plus(15, ChronoUnit.SECONDS).toEpochMilli();

        Object[] values = new Object[10];
        values[0] = seatNumStr;
        values[1] = String.valueOf(scheduleId);
        values[2] = String.valueOf(epochMilli);
        String luaScript = "local holdKey = KEYS[1] \n" +
                "local offset = tonumber(ARGV[1]) \n" +
                "local holdMapKey = KEYS[2] \n" +
                "local scheduleId = ARGV[2] \n" +
                "local holdExpireKey = KEYS[3] \n" +
                "local expireTime = tonumber(ARGV[3]) \n" +
                "local holdSeatKey = KEYS[4] \n" +
                "if redis.call('SETBIT', holdKey, offset, 1) == 0 then \n" +
                "   redis.call('HSET', holdMapKey, holdSeatKey, scheduleId) \n" +
                "   redis.call('ZADD', holdExpireKey, 'GT', expireTime, holdMapKey) \n" +
                "   return true \n" +
                "else \n" +
                "   return false \n" +
                "end";

        RedisScript<Boolean> script = new DefaultRedisScript<>(luaScript, Boolean.class);
        Boolean success = redisTemplate.execute(script, keys, values);
        if (Boolean.TRUE.equals(success)) {
            log.info("占座成功");
            return "success";
        } else {
            log.error("已被占座");
            return "failed";
        }
    }

    /**
     * 取消占座
     *
     * @param seatNum
     * @param userId
     * @return
     */
    @RequestMapping("/test/seat/cancel/{scheduleId}/{userId}/{seatNum}")
    public String cancelSeat(@PathVariable long scheduleId, @PathVariable long seatNum, @PathVariable long userId) {
        String holdKey = "seat:bitmap:" + scheduleId;
        String holdExpireKey = "seat:zset";

        String holdMapKey = "seat:map:" + scheduleId + ":" + userId;
        String seatNumStr = String.valueOf(seatNum);

        String luaScript = "local holdKey = KEYS[1] \n" +
                "local holdMapKey = KEYS[2] \n" +
                "local holdExpireKey = KEYS[3] \n" +
                "local offset = tonumber(ARGV[1]) \n" +
                "local seatNum = ARGV[1] \n" +
                "redis.call('HDEL', holdMapKey, seatNum) \n" +
                "redis.call('SETBIT', holdKey, offset, 0) \n" +
                "if redis.call('HLEN', holdMapKey) == 0 then \n" +
                "   redis.call('ZREM', holdExpireKey, holdMapKey) \n" +
                "end \n" +
                "return true";

        List<String> keys = new ArrayList<>();
        keys.add(holdKey);
        keys.add(holdMapKey);
        keys.add(holdExpireKey);

        Object[] values = new Object[10];
        values[0] = seatNumStr;

        RedisScript<Boolean> script = new DefaultRedisScript<>(luaScript, Boolean.class);
        try {
            Boolean result = redisTemplate.execute(script, keys, values);
            if (Boolean.TRUE.equals(result)) {
                log.info("取消占座成功");
                return "success";
            } else {
                log.info("取消占座失败");
                return "failed";
            }
        } catch (Exception e) {
            log.error("取消占座失败：{}", e.getMessage(), e);
            return "failed";
        }
    }

    /**
     * 取消用户占座
     *
     * @param userId
     * @return
     */
    @RequestMapping("/test/seat/cancel/{scheduleId}/{userId}")
    public String cancelUserSeat(@PathVariable long scheduleId, @PathVariable long userId) {
        String holdKey = "seat:bitmap:" + scheduleId;
        String holdExpireKey = "seat:zset";

        String holdMapKey = "seat:map:" + scheduleId + ":" + userId;

        String luaScript = "local holdKey = KEYS[1] \n" +
                "local holdMapKey = KEYS[2] \n" +
                "local holdExpireKey = KEYS[3] \n" +
                "local seatKeys = redis.call('HKEYS', holdMapKey) \n" +
                "for i=1,#seatKeys do \n" +
                "\tlocal offset = tonumber(seatKeys[i]) \n" +
                "\tredis.call('SETBIT', holdKey, offset, 0)\n" +
                "end \n" +
                "local seatValues = redis.call('HVALS', holdMapKey) \n" +
                "redis.call('DEL', holdMapKey) \n" +
                "redis.call('ZREM', holdExpireKey, holdMapKey) \n" +
                "return seatValues";

        List<String> keys = new ArrayList<>();
        keys.add(holdKey);
        keys.add(holdMapKey);
        keys.add(holdExpireKey);

        Object[] values = new Object[10];

        RedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);
        try {
            List result = redisTemplate.execute(script, keys, values);
            log.info("占座超时释放成功: {}", result);
            return "success";
        } catch (Exception e) {
            log.error("占座超时释放失败：{}", e.getMessage(), e);
            return "failed";
        }
    }

    @RequestMapping("/test-redis-str")
    public void testStr() {
        List<String> keys = new ArrayList<>();
        keys.add("test-set-quotes");
        Object[] values = new Object[10];
        values[0] = "296";
        RedisScript<Boolean> script = new DefaultRedisScript<>("local key = KEYS[1];local value = ARGV[1];redis.call('SET', key, value);return true", Boolean.class);
        Boolean execute = redisTemplate.execute(script, keys, values);
        if (Boolean.TRUE.equals(execute)) {
            log.info("success");
        }
    }

    @RequestMapping("/test-redis-hash")
    public void testHash() {
        List<String> keys = new ArrayList<>();
        keys.add("test-hash-quotes");
        Object[] values = new Object[10];
        values[0] = "296";
        RedisScript<Boolean> script = new DefaultRedisScript<>("local key = KEYS[1];local value = ARGV[1];redis.call('HSET', key, '1', value);return true", Boolean.class);
        Boolean execute = redisTemplate.execute(script, keys, values);
        if (Boolean.TRUE.equals(execute)) {
            log.info("success");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/test/lock/batch")
    public void testLock(@RequestBody List<Long> ids) {
        QueryWrapper<DemoEntity> wrapper = new QueryWrapper<>();
        for (Long id : ids) {
            wrapper = wrapper.eq("id", id).or();
        }
        wrapper.last("for update");
        try {
            log.info("waiting for get lock...");
            List<DemoEntity> list = demoRepository.selectList(wrapper);
            log.info("list: {}", list);
            Thread.sleep(10000);
            log.info("lock release...");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @RequestMapping("/test/redis/setnx")
    public void testLock(@RequestParam String key) {
        try {
            log.info("lock");
            Boolean absent = redisTemplate.opsForValue().setIfAbsent(key, "test", 30, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(absent)) {
                log.info("get lock");
            } else {
                throw new RuntimeException("locked");
            }
            log.info("do something");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
//            redisTemplate.opsForValue().getAndDelete(key);
            log.info("release");
        }
    }


    @RequestMapping("/test/redis/lua/queue/push")
    public void testLuaQueuePush(@RequestParam String key) {
        NotifyDto notifyDto = new NotifyDto();
        notifyDto.setOrderNo(key);
        notifyDto.setNotifyTime(Instant.now().toEpochMilli());
        notifyDto.setStep(0);
        stringRedisTemplate.opsForList().leftPush("test-lua-queue", JSON.toJSONString(notifyDto));
    }

    @RequestMapping("/test/redis/lua/queue")
    public void testLuaQueue() {
        List<String> list = stringRedisTemplate.opsForList().range("test-lua-queue", 0, -1);
        if (list == null || list.isEmpty()) {
            return;
        }
        List<NotifyDto> notifyDtos = list.stream().map(s -> {
            NotifyDto notifyDto = JSON.parseObject(s, NotifyDto.class);
            notifyDto.setOriginNotify(s);
            return notifyDto;
        }).collect(Collectors.toList());

        for (NotifyDto notifyDto : notifyDtos) {
            String originNotify = notifyDto.getOriginNotify();
            notifyDto.setStep(notifyDto.getStep() + 1);
            notifyDto.setOriginNotify(null);

            String luaScript = "local pendingKey = KEYS[1] \n" +
                    "local oldValue = ARGV[1] \n" +
                    "local newValue = ARGV[2] \n" +
                    "redis.call('LREM', pendingKey, 0, oldValue) \n" +
                    "redis.call('LPUSH', pendingKey, newValue) \n";

            List<String> keys = new ArrayList<>();
            keys.add("test-lua-queue");
            Object[] values = new Object[2];
            values[0] = originNotify;
            values[1] = JSON.toJSONString(notifyDto);
            DefaultRedisScript<Object> script = new DefaultRedisScript<>(luaScript);
            stringRedisTemplate.execute(script, keys, values);
        }
    }

    @Resource
    private TaskExecutor taskExecutor;

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/test/tx/rollback")
    public void testRollback(@RequestParam int testNum) {
        DemoEntity entity = new DemoEntity();
        entity.setId(2L);
        entity.setName("outer rollback");
        demoService.updateById(entity);
        try {
            taskExecutor.execute(() -> {
                try {
                    demoService.testException(testNum);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (testNum > 20) {
            throw new RuntimeException("outer rollback");
        }
    }
}
