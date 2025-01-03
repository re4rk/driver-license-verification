package com.ark.driverlicense.verification.infrastructure;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.LicenseStatus;
import com.ark.driverlicense.verification.domain.LicenseType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;


public record RedisDriverLicense(
    UUID id,
    String licenseNumber,
    String serialNumber,
    LicenseType type,
    String name,
    LocalDate dateOfBirth,
    LocalDate issueDate,
    LocalDate expiryDate,
    LicenseStatus status,
    Map<String, Object> metadata
) {

    @JsonCreator
    @Builder
    public RedisDriverLicense(
        @JsonProperty("id") UUID id,
        @JsonProperty("licenseNumber") String licenseNumber,
        @JsonProperty("serialNumber") String serialNumber,
        @JsonProperty("type") LicenseType type,
        @JsonProperty("name") String name,
        @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
        @JsonProperty("issueDate") LocalDate issueDate,
        @JsonProperty("expiryDate") LocalDate expiryDate,
        @JsonProperty("status") LicenseStatus status,
        @JsonProperty("metadata") Map<String, Object> metadata
    ) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.serialNumber = serialNumber;
        this.type = type;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.metadata = metadata;
    }

    public static RedisDriverLicense from(DriverLicense driverLicense) {
        return RedisDriverLicense.builder()
            .id(driverLicense.getId())
            .licenseNumber(driverLicense.getLicenseNumber())
            .serialNumber(driverLicense.getSerialNumber())
            .type(driverLicense.getType())
            .name(driverLicense.getName())
            .dateOfBirth(driverLicense.getDateOfBirth())
            .issueDate(driverLicense.getIssueDate())
            .expiryDate(driverLicense.getExpiryDate())
            .status(driverLicense.getStatus())
            .metadata(driverLicense.getMetadata())
            .build();
    }

    public DriverLicense toDomain() {
        return DriverLicense.builder()
            .licenseNumber(this.licenseNumber)
            .serialNumber(this.serialNumber)
            .type(this.type)
            .name(this.name)
            .dateOfBirth(this.dateOfBirth)
            .issueDate(this.issueDate)
            .expiryDate(this.expiryDate)
            .metadata(this.metadata)
            .build();
    }
}
