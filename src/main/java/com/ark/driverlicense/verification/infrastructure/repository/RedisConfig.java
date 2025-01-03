package com.ark.driverlicense.verification.infrastructure.repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(name = "ark.driver-license.type", havingValue = "REDIS")
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean(name = "driverLicenseRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisProperties.getHost());
        redisConfig.setPort(redisProperties.getPort());
        redisConfig.setPassword(redisProperties.getPassword());
        redisConfig.setDatabase(redisProperties.getDatabase());

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, RedisDriverLicense> redisTemplate(
        RedisConnectionFactory driverLicenseRedisConnectionFactory
    ) {
        // RedisTemplate
        RedisTemplate<String, RedisDriverLicense> template = new RedisTemplate<>();
        template.setConnectionFactory(driverLicenseRedisConnectionFactory);

        // ObjectMapper
        var objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // Jackson2JsonRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(
            new Jackson2JsonRedisSerializer<>(objectMapper, RedisDriverLicense.class)
        );
        template.setHashValueSerializer(
            new Jackson2JsonRedisSerializer<>(objectMapper, RedisDriverLicense.class)
        );

        return template;
    }
}
