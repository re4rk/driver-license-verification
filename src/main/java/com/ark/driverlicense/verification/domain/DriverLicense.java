package com.ark.driverlicense.verification.domain;

import com.ark.driverlicense.verification.exception.InvalidLicenseException;
import com.ark.driverlicense.verification.exception.InvalidLicenseStatusException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DriverLicense {

    private final UUID id;
    private final String licenseNumber;
    private final String serialNumber;
    private final LicenseType type;
    private final String name;
    private final LocalDate dateOfBirth;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;
    private LicenseStatus status;
    private Map<String, Object> metadata = Collections.emptyMap();

    private static final Pattern LICENSE_NUMBER_PATTERN = Pattern.compile(
        "^\\d{2}-\\d{2}-\\d{6}-\\d{2}$");
    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

    @Builder
    public DriverLicense(String licenseNumber, String serialNumber, String name,
        LocalDate dateOfBirth, LocalDate issueDate, LocalDate expiryDate,
        UUID id, LicenseType type, Map<String, Object> metadata) {
        validateLicenseFormat(licenseNumber);
        validateSerialNumber(serialNumber);
        validateDates(dateOfBirth, issueDate, expiryDate);

        this.id = UUID.randomUUID();
        this.licenseNumber = licenseNumber;
        this.serialNumber = serialNumber;
        this.type = type;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = LicenseStatus.PENDING;
        this.metadata = metadata == null ? Collections.emptyMap() : metadata;
    }

    private void validateLicenseFormat(String licenseNumber) {
        if (!LICENSE_NUMBER_PATTERN.matcher(licenseNumber).matches()) {
            throw new InvalidLicenseException("Invalid license number format");
        }
    }

    private void validateSerialNumber(String serialNumber) {
        if (!SERIAL_NUMBER_PATTERN.matcher(serialNumber).matches()) {
            throw new InvalidLicenseException("Invalid serial number format");
        }
    }

    private void validateDates(LocalDate dateOfBirth, LocalDate issueDate, LocalDate expiryDate) {
        if (!issueDate.isBefore(expiryDate)) {
            throw new InvalidLicenseException("Issue date must be before expiry date");
        }
        if (!dateOfBirth.isBefore(issueDate)) {
            throw new InvalidLicenseException("Date of birth must be before issue date");
        }
    }

    public void verify() {
        validatePendingStatus("verified");
        this.status = LicenseStatus.VERIFIED;
    }

    public void reject(String reason) {
        validatePendingStatus("rejected");
        this.status = LicenseStatus.REJECTED;
        updateMetadataWithRejection(reason);
    }

    private void validatePendingStatus(String action) {
        if (status != LicenseStatus.PENDING) {
            throw new InvalidLicenseStatusException(
                String.format("License must be in PENDING status to be %s", action)
            );
        }
    }

    private void updateMetadataWithRejection(String reason) {
        Map<String, Object> newMetadata = new HashMap<>(this.metadata);
        newMetadata.put("rejectionReason", reason);
        this.metadata = Collections.unmodifiableMap(newMetadata);
    }
}
