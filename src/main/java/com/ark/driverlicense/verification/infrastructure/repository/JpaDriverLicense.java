package com.ark.driverlicense.verification.infrastructure.repository;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.LicenseStatus;
import com.ark.driverlicense.verification.domain.LicenseType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver_licenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JpaDriverLicense {

    @Id
    private UUID id;

    @Column(nullable = false, length = 20)
    private String licenseNumber;

    @Column(nullable = false, length = 6)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseType type;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "driver_license_metadata",
        joinColumns = @JoinColumn(name = "driver_license_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();

    public static JpaDriverLicense from(DriverLicense driverLicense) {
        JpaDriverLicense entity = new JpaDriverLicense();
        entity.id = driverLicense.getId();
        entity.licenseNumber = driverLicense.getLicenseNumber();
        entity.serialNumber = driverLicense.getSerialNumber();
        entity.type = driverLicense.getType();
        entity.name = driverLicense.getName();
        entity.dateOfBirth = driverLicense.getDateOfBirth();
        entity.issueDate = driverLicense.getIssueDate();
        entity.expiryDate = driverLicense.getExpiryDate();
        entity.status = driverLicense.getStatus();

        // Object 타입의 metadata를 String으로 변환
        driverLicense.getMetadata().forEach((key, value) ->
            entity.metadata.put(key, value != null ? value.toString() : null)
        );

        return entity;
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
            .metadata(new HashMap<>(this.metadata))
            .build();
    }
}
