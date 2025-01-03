package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import java.util.List;

public class InMemoryDriverLicenseRepository implements DriverLicenseRepository {

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
