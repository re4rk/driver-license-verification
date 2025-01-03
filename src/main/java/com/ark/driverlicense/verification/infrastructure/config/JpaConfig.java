package com.ark.driverlicense.verification.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ark.driverlicense.verification.infrastructure.repository")
@ConditionalOnProperty(name = "ark.driver-license.type", havingValue = "JPA")
public class JpaConfig {

}
