package com.hb.concert.support.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key를 위한 Serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Value를 위한 Serializer
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        // HashKey를 위한 Serializer
        template.setHashKeySerializer(new StringRedisSerializer());

        // HashValue를 위한 Serializer
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        template.afterPropertiesSet();
        return template;
    }
}
