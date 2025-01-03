package com.ark.driverlicense.verification.application;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseData;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.infrastructure.client.LicenseVerificationClient;
import com.ark.driverlicense.verification.infrastructure.client.VerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLicenseVerifyService {

    private final DriverLicenseService driverLicenseService;
    private final LicenseVerificationClient verificationClient;

    public DriverLicenseDto verifyLicense(DriverLicenseData driverLicenseData) {
        DriverLicense license = driverLicenseData.toDomain();
        VerificationResult result = verificationClient.verify(license);

        return driverLicenseService.processVerification(license, result);
    }
}
