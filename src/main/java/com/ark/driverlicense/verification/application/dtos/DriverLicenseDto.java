package com.ark.driverlicense.verification.application.dtos;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.LicenseStatus;
import com.ark.driverlicense.verification.domain.LicenseType;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DriverLicenseDto(
    UUID id,
    String licenseNumber,
    String serialNumber,
    LicenseType type,
    String name,
    LocalDate dateOfBirth,
    LocalDate issueDate,
    LocalDate expiryDate,
    LicenseStatus status
) {

    public static DriverLicenseDto fromDomain(DriverLicense driverLicense) {
        return DriverLicenseDto.builder()
            .id(driverLicense.getId())
            .licenseNumber(driverLicense.getLicenseNumber())
            .serialNumber(driverLicense.getSerialNumber())
            .type(driverLicense.getType())
            .name(driverLicense.getName())
            .dateOfBirth(driverLicense.getDateOfBirth())
            .issueDate(driverLicense.getIssueDate())
            .expiryDate(driverLicense.getExpiryDate())
            .status(driverLicense.getStatus())
            .build();
    }
}
