package com.ark.driverlicense.verification.presentation.dtos;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseData;
import com.ark.driverlicense.verification.domain.LicenseType;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VerifyLicenseRequest(
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{6}-\\d{2}")
    String licenseNumber,
    @Pattern(regexp = "[A-Z0-9]{6}$")
    String serialNumber,
    LicenseType type,
    @Pattern(regexp = "[가-힣]{2,4}")
    String name,
    LocalDate dateOfBirth,
    LocalDate issueDate,
    LocalDate expiryDate
) {

    public DriverLicenseData toDriverLicenseData() {
        return DriverLicenseData.builder()
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

