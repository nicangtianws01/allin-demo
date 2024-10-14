package org.example.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;


public class RedisDistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    private final long expireTime;

    public RedisDistributedLock(RedisTemplate<String, String> redisTemplate, long expireTime) {
        this.redisTemplate = redisTemplate;
        this.expireTime = expireTime;
    }

    // 获取锁的 Lua 脚本
    private static final String LOCK_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('pexpire', KEYS[1], ARGV[2]); " +
                    "return true; " +
                    "else return false; " +
                    "end";

    // 释放锁的 Lua 脚本
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "redis.call('del', KEYS[1]); " +
                    "return true; " +
                    "else return false; " +
                    "end";

    // 获取分布式锁
    public boolean lock(String key, String value) {
        String[] keys = {key};
        String[] args = {value, String.valueOf(expireTime)};
        RedisScript<Boolean> script = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
        Boolean result = redisTemplate.execute(script, Arrays.asList(keys), args);
        return result != null && result;
    }

    // 释放分布式锁
    public boolean unlock(String key, String value) {
        String[] keys = {key};
        String[] args = {value};
        RedisScript<Boolean> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Boolean.class);
        Boolean result = redisTemplate.execute(script, Arrays.asList(keys), args);
        return result != null && result;
    }
}

