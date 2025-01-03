package com.ark.driverlicense.verification.infrastructure.client;

import com.ark.driverlicense.verification.domain.DriverLicense;

public interface LicenseVerificationClient {

    VerificationResult verify(DriverLicense license);
}
