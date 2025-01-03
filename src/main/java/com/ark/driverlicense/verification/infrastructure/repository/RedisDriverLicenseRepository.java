package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.exception.DriverLicensePersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
class RedisDriverLicenseRepository implements DriverLicenseRepository {

    private static final String KEY_PREFIX = "driver-license:";
    private final RedisTemplate<String, RedisDriverLicense> redisTemplate;

    public RedisDriverLicenseRepository(
        RedisTemplate<String, RedisDriverLicense> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(DriverLicense driverLicense) {
        try {
            RedisDriverLicense redisEntity = RedisDriverLicense.from(driverLicense);
            String key = KEY_PREFIX + redisEntity.id();

            redisTemplate.opsForValue().set(key, redisEntity);
        } catch (Exception e) {
            log.error("Failed to persist driver license", e);
            throw new DriverLicensePersistenceException("Failed to persist driver license");
        }
    }

    @Override
    public List<DriverLicense> findAll() {
        try {
            Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
            if (keys.isEmpty()) {
                return Collections.emptyList();
            }

            List<RedisDriverLicense> values = redisTemplate.opsForValue().multiGet(keys);
            if (values == null) {
                return Collections.emptyList();
            }

            return values.stream()
                .filter(Objects::nonNull)
                .map(RedisDriverLicense::toDomain)
                .toList();
        } catch (Exception e) {
            log.error("Failed to fetch driver licenses", e);
            throw new DriverLicensePersistenceException("Failed to retrieve driver licenses");
        }
    }
}
