package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class RedisExpireListener extends KeyExpirationEventMessageListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public RedisExpireListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void doHandleMessage(Message message) {

        // 过期的 key
        byte[] body = message.getBody();

        // 消息通道
        byte[] channel = message.getChannel();

        String key = new String(body);
        String orderNo = key.replaceFirst("test:", "");
        if (key.startsWith("test:")) {
            stringRedisTemplate.opsForList().leftPush("test:expire:list:", orderNo);

            log.info("message = {}, channel = {}", orderNo, new String(channel));

//            stringRedisTemplate.opsForList().remove("test:expire:list:", 0, orderNo);
        }
    }
}
