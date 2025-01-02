package com.ark.driverlicense.verification.infrastructure;

import com.ark.driverlicense.verification.domain.DriverLicense;

public interface LicenseVerificationClient {
    VerificationResult verify(DriverLicense license);
}
