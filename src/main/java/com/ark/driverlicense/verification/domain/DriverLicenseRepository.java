package com.ark.driverlicense.verification.domain;

import java.util.List;

public interface DriverLicenseRepository {

    void save(DriverLicense driverLicense);

    List<DriverLicense> findAll();
}

