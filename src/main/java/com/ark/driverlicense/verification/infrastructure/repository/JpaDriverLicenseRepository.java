package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.exception.DriverLicensePersistenceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@ConditionalOnProperty(
    name = "ark.driver-license.type",
    havingValue = "JPA"
)
public class JpaDriverLicenseRepository implements DriverLicenseRepository {

    private final SpringDataDriverLicenseRepository repository;

    public JpaDriverLicenseRepository(SpringDataDriverLicenseRepository repository) {
        this.repository = repository;
    }

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
