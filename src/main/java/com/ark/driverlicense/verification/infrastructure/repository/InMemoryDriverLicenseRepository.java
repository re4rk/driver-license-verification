package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Repository
@ConditionalOnProperty(
    name = "ark.driver-license.type",
    havingValue = "INMEMORY",
    matchIfMissing = true
)
public class InMemoryDriverLicenseRepository implements DriverLicenseRepository {

    private final ConcurrentHashMap<UUID, DriverLicense> driverLicenses;
    private final ThreadLocal<Map<UUID, DriverLicense>> transactionCache = new ThreadLocal<>();

    public InMemoryDriverLicenseRepository(List<DriverLicense> initialLicenses) {
        this.driverLicenses = new ConcurrentHashMap<>();
        initialLicenses.forEach(license -> driverLicenses.put(license.getId(), license));
    }


    @Override
    public void save(DriverLicense driverLicense) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            getTransactionCache().put(driverLicense.getId(), driverLicense);
            var original = driverLicenses.get(driverLicense.getId());

            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        driverLicenses.put(driverLicense.getId(), driverLicense);
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK && original != null) {
                            driverLicenses.put(driverLicense.getId(), original);
                        }
                        transactionCache.remove();
                    }
                });
        } else {
            driverLicenses.put(driverLicense.getId(), driverLicense);
        }
    }

    private Map<UUID, DriverLicense> getTransactionCache() {
        Map<UUID, DriverLicense> cache = transactionCache.get();
        if (cache == null) {
            cache = new HashMap<>();
            transactionCache.set(cache);
        }
        return cache;
    }

    @Override
    public List<DriverLicense> findAll() {
        return new ArrayList<>(driverLicenses.values());
    }
}
