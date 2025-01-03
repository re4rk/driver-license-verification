package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RepositoryProperties.class)
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(
        name = "ark.driver-license.type",
        havingValue = "INMEMORY",
        matchIfMissing = true
    )
    public DriverLicenseRepository inMemoryDriverLicenseRepository() {
        log.info("Using in-memory driver license repository");
        return new InMemoryDriverLicenseRepository(new ArrayList<>());
    }

    @Bean
    @ConditionalOnProperty(
        name = "ark.driver-license.type",
        havingValue = "REDIS"
    )
    public DriverLicenseRepository redisDriverLicenseRepository(
        RedisTemplate<String, String> redisTemplate) {
        log.info("Using Redis driver license repository");
        return new RedisDriverLicenseRepository(redisTemplate);
    }
}
