package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.exception.DriverLicensePersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Repository
@ConditionalOnProperty(
    name = "ark.driver-license.type",
    havingValue = "REDIS"
)
public class RedisDriverLicenseRepository implements DriverLicenseRepository {

    private static final String KEY_PREFIX = "driver-license:";
    private final RedisTemplate<String, RedisDriverLicense> redisTemplate;
    private final ThreadLocal<RedisDriverLicense> transactionCache = new ThreadLocal<>();

    public RedisDriverLicenseRepository(RedisTemplate<String, RedisDriverLicense> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void save(DriverLicense driverLicense) {
        try {
            RedisDriverLicense redisEntity = RedisDriverLicense.from(driverLicense);
            String key = KEY_PREFIX + redisEntity.id();

            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                transactionCache.set(redisEntity);
                // 기존 데이터 백업
                RedisDriverLicense originalData = redisTemplate.opsForValue().get(key);

                TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisTemplate.opsForValue().set(key, transactionCache.get());
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK && originalData != null) {
                                redisTemplate.opsForValue().set(key, originalData);
                            }
                            transactionCache.remove();
                        }
                    });
            } else {
                redisTemplate.opsForValue().set(key, redisEntity);
            }
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
