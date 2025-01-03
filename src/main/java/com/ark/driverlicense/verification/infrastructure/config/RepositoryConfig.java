package com.ark.driverlicense.verification.infrastructure.config;

import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.infrastructure.repository.JpaDriverLicenseRepository;
import com.ark.driverlicense.verification.infrastructure.repository.SpringDataDriverLicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RepositoryProperties.class)
public class RepositoryConfig {

    @Bean
    @ConditionalOnProperty(
        name = "ark.driver-license.type",
        havingValue = "JPA"
    )
    public DriverLicenseRepository jpaDriverLicenseRepository(
        SpringDataDriverLicenseRepository driverLicenseRepository
    ) {
        log.info("Using JPA driver license repository");
        return new JpaDriverLicenseRepository(driverLicenseRepository);
    }
}
