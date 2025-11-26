package com.home.trip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}") private String host;
    @Value("${spring.data.redis.port}") private int port;

    // redis와 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Connected to Redis at {}:{}", host, port);
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
