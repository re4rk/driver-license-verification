package com.ark.driverlicense.verification.application.dtos;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.LicenseType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DriverLicenseData(
    String licenseNumber,
    String serialNumber,
    LicenseType type,
    String name,
    LocalDate dateOfBirth,
    LocalDate issueDate,
    LocalDate expiryDate
) {

    public DriverLicense toDomain() {
        return DriverLicense.builder()
            .licenseNumber(licenseNumber)
            .serialNumber(serialNumber)
            .type(type)
            .name(name)
            .dateOfBirth(dateOfBirth)
            .issueDate(issueDate)
            .expiryDate(expiryDate)
            .build();
    }
}
