package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.handler.RedisMessageConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Slf4j
@Configuration
public class RedisStreamConfig {

    @Value("${stream.key:purchase-stream-events}")
    private String streamKey;

    /**
     * 创建group
     * 创建消息容器
     *
     * @param connectionFactory
     * @return
     * @throws UnknownHostException
     */
    @Bean
    public Subscription subscription(RedisConnectionFactory connectionFactory) throws UnknownHostException {

        createConsumerGroupIfNotExists(connectionFactory, streamKey, streamKey);

        StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());

        StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer
                        .StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofMillis(100))
                        .targetType(String.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        Subscription subscription =
                container.receive(
                        Consumer.from(
                                streamKey,
                                InetAddress.getLocalHost().getHostName()
                        ),
                        streamOffset,
                        purchaseStreamListener()
                );

        container.start();
        return subscription;
    }

    /**
     * 注入自定义消费者
     *
     * @return
     */
    @Bean
    public StreamListener<String, ObjectRecord<String, String>> purchaseStreamListener() {
        // handle message from stream
        return new RedisMessageConsumer();
    }

    /**
     * 修改序列化方式，防止乱码
     *
     * @param factory
     * @return
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<String> valueSerializer = new Jackson2JsonRedisSerializer<>(String.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, String> context = builder.value(valueSerializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    /**
     * 修改序列化方式，防止乱码
     *
     * @param factory
     * @return
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        template.setConnectionFactory(factory);
        // key序列化方式
        template.setKeySerializer(redisSerializer);
        // value序列化
        template.setValueSerializer(redisSerializer);
        // value hashmap序列化
        template.setHashKeySerializer(redisSerializer);
        // 这个地方不可使用 json 序列化，如果使用的是ObjectRecord传输对象时，可能会有问题
        // 会出现一个 java.lang.IllegalArgumentException: Value must not be null! 错误
        template.setHashValueSerializer(RedisSerializer.string());

        template.setEnableTransactionSupport(true);

        return template;
    }

    /**
     * 初始化group
     *
     * @param redisConnectionFactory
     * @param streamKey
     * @param groupName
     */
    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory,
                                                String streamKey, String groupName) {
        try {
            redisConnectionFactory
                    .getConnection()
                    .streamCommands()
                    .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.lastConsumed(), true);
        } catch (RedisSystemException exception) {
            log.warn(exception.getCause().getMessage());
        }
    }

}