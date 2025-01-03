package com.ark.driverlicense.verification.infrastructure;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryDriverLicenseRepository implements DriverLicenseRepository {

    private final List<DriverLicense> driverLicenses;

    public InMemoryDriverLicenseRepository(List<DriverLicense> driverLicenses) {
        this.driverLicenses = driverLicenses;
    }

    @Override
    public void save(DriverLicense driverLicense) {
        driverLicenses.add(driverLicense);
    }

    @Override
    public List<DriverLicense> findAll() {
        return driverLicenses;
    }
}
