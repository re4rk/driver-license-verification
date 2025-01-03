package com.ark.driverlicense.verification.infrastructure;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.exception.DriverLicensePersistenceException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
class RedisDriverLicenseRepository implements DriverLicenseRepository {

    private static final String KEY_PREFIX = "driver-license:";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisDriverLicenseRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public void save(DriverLicense driverLicense) {
        try {
            RedisDriverLicense redisEntity = RedisDriverLicense.from(driverLicense);
            String key = KEY_PREFIX + redisEntity.id();
            String value = objectMapper.writeValueAsString(redisEntity);

            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize driver license", e);
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

            List<String> values = redisTemplate.opsForValue().multiGet(keys);
            if (values == null) {
                return Collections.emptyList();
            }

            return values.stream()
                .filter(Objects::nonNull)
                .map(this::deserializeRedisEntity)
                .map(RedisDriverLicense::toDomain)
                .toList();
        } catch (Exception e) {
            log.error("Failed to fetch driver licenses", e);
            throw new DriverLicensePersistenceException("Failed to retrieve driver licenses");
        }
    }

    private RedisDriverLicense deserializeRedisEntity(String json) {
        try {
            return objectMapper.readValue(json, RedisDriverLicense.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize driver license: {}", json, e);
            throw new DriverLicensePersistenceException(
                "Failed to reconstruct driver license from persistence");
        }
    }
}
