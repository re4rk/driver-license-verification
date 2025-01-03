package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.exception.DriverLicensePersistenceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Repository
@ConditionalOnProperty(
    name = "ark.driver-license.type",
    havingValue = "JPA"
)
@RequiredArgsConstructor
public class JpaDriverLicenseRepository implements DriverLicenseRepository {

    private final SpringDataDriverLicenseRepository repository;

    @Override
    @Transactional
    public void save(DriverLicense driverLicense) {
        try {
            JpaDriverLicense entity = JpaDriverLicense.from(driverLicense);
            repository.save(entity);
        } catch (Exception e) {
            log.error("Failed to persist driver license", e);
            throw new DriverLicensePersistenceException("Failed to persist driver license");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverLicense> findAll() {
        try {
            return repository.findAll()
                .stream()
                .map(JpaDriverLicense::toDomain)
                .toList();
        } catch (Exception e) {
            log.error("Failed to fetch driver licenses", e);
            throw new DriverLicensePersistenceException("Failed to retrieve driver licenses");
        }
    }
}
