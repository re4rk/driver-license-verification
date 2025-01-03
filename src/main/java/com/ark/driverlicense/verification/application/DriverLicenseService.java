package com.ark.driverlicense.verification.application;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseData;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.infrastructure.client.LicenseVerificationClient;
import com.ark.driverlicense.verification.infrastructure.client.VerificationResult;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLicenseService {

    private final DriverLicenseRepository driverLicenseRepository;
    private final LicenseVerificationClient verificationClient;

    @Transactional
    public DriverLicenseDto verifyLicense(DriverLicenseData driverLicenseData) {
        DriverLicense license = driverLicenseData.toDomain();
        VerificationResult verificationResult = verificationClient.verify(license);

        if (verificationResult instanceof VerificationResult.Success) {
            license.verify();
        } else if (verificationResult instanceof VerificationResult.Failure failure) {
            license.reject(failure.getReason());
        } else if (verificationResult instanceof VerificationResult.PendingManualVerification) {
            // Do nothing
        }

        driverLicenseRepository.save(license);

        return DriverLicenseDto.fromDomain(license);
    }

    public List<DriverLicenseDto> listDriverLicenses() {
        return driverLicenseRepository.findAll().stream()
            .map(DriverLicenseDto::fromDomain)
            .toList();
    }
}
