package org.example.controller;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/redis")
@RestController
public class RedisTestController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public static final String ZSET_REDIS_KEY = "test:zset:demo";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/zset/add")
    public String zsetAdd(@RequestBody DemoEntity entity) throws JsonProcessingException {
        long epochMilli = Instant.now().toEpochMilli();
        String entityStr = objectMapper.writeValueAsString(entity);
        redisTemplate.opsForZSet().add(ZSET_REDIS_KEY, entityStr, epochMilli);
        return "success";
    }

    @RequestMapping("/zset/range-by-score")
    public String zsetRangeByScore() throws InterruptedException {
        long epochMilli = Instant.now().toEpochMilli();
        Set<String> rangeByScore = redisTemplate.opsForZSet().rangeByScore(ZSET_REDIS_KEY, 0, epochMilli);
        if (rangeByScore == null || rangeByScore.isEmpty()) {
            return "empty";
        }

        List<DemoEntity> demoEntities = rangeByScore.stream().map(s -> {
            try {
                return objectMapper.readValue(s, DemoEntity.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        // 模拟耗时操作
        Thread.sleep(15000);
        demoEntities.forEach(entity -> log.info(JSON.toJSONString(entity)));
        redisTemplate.opsForZSet().removeRangeByScore(ZSET_REDIS_KEY, 0, epochMilli);
        return "success";
    }
}
