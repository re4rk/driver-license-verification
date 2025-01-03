package com.ark.driverlicense.verification.application;

import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseData;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.LicenseStatus;
import com.ark.driverlicense.verification.domain.LicenseType;
import com.ark.driverlicense.verification.infrastructure.client.LicenseVerificationClient;
import com.ark.driverlicense.verification.infrastructure.client.VerificationResult;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DriverLicenseServiceTest {

    private LicenseVerificationClient verificationClient;
    private DriverLicenseService driverLicenseService;

    @BeforeEach
    void setUp() {
        DriverLicenseRepository driverLicenseRepository = mock(DriverLicenseRepository.class);
        verificationClient = mock(LicenseVerificationClient.class);
        driverLicenseService = new DriverLicenseService(
            driverLicenseRepository,
            verificationClient
        );
    }

    @Test
    @DisplayName("면허 검증에 성공하면 VERIFIED 상태가 된다")
    void verifyLicenseSuccess() {
        // given
        DriverLicenseData licenseData = createValidLicenseData();
        when(verificationClient.verify(any()))
            .thenReturn(new VerificationResult.Success("verification-code"));

        // when
        DriverLicenseDto result = driverLicenseService.verifyLicense(licenseData);

        // then
        assertThat(result.status()).isEqualTo(LicenseStatus.VERIFIED);
    }

    @Test
    @DisplayName("면허 검증에 실패하면 REJECTED 상태가 된다")
    void verifyLicenseFailure() {
        // given
        DriverLicenseData licenseData = createValidLicenseData();
        String failureReason = "Invalid information";
        when(verificationClient.verify(any()))
            .thenReturn(new VerificationResult.Failure(failureReason));

        // when
        DriverLicenseDto result = driverLicenseService.verifyLicense(licenseData);

        // then
        assertThat(result.status()).isEqualTo(LicenseStatus.REJECTED);
    }

    @Test
    @DisplayName("수동 검증이 필요한 경우 PENDING 상태를 유지한다")
    void verifyLicensePending() {
        // given
        DriverLicenseData licenseData = createValidLicenseData();
        when(verificationClient.verify(any()))
            .thenReturn(VerificationResult.PendingManualVerification.INSTANCE);

        // when
        DriverLicenseDto result = driverLicenseService.verifyLicense(licenseData);

        // then
        assertThat(result.status()).isEqualTo(LicenseStatus.PENDING);
    }

    private DriverLicenseData createValidLicenseData() {
        return DriverLicenseData.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build();
    }
}
