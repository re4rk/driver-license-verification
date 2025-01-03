package com.ark.driverlicense.verification.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ark.driver-license")
public class RepositoryProperties {

    private RepositoryType type = RepositoryType.INMEMORY;

    public enum RepositoryType {
        INMEMORY, REDIS
    }
}
