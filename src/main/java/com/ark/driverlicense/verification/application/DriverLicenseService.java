package com.ark.driverlicense.verification.application;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseData;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.infrastructure.LicenseVerificationClient;
import com.ark.driverlicense.verification.infrastructure.VerificationResult;
import org.springframework.stereotype.Service;

@Service
public class DriverLicenseService {

    private final LicenseVerificationClient verificationClient;

    public DriverLicenseService(LicenseVerificationClient verificationClient) {
        this.verificationClient = verificationClient;
    }

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

        return DriverLicenseDto.fromDomain(license);
    }
}
