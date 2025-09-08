package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RedisMessageConsumer implements StreamListener<String, ObjectRecord<String, String>> {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${stream.key:purchase-stream-events}")
    private String streamKey;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String stream = message.getStream();
        String messageId = message.getId().toString();
        String messageBody = message.getValue();

        log.info("Received message from Stream '{}' with messageId: {}", stream, messageId);
        log.info("Message body: {}", messageBody);

        redisTemplate.opsForStream().acknowledge(streamKey, message);

//        reactiveRedisTemplate.opsForStream().acknowledge(streamKey, streamKey, messageId).subscribe();

        log.info("消费成功");
    }
}
