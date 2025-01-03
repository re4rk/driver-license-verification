package com.ark.driverlicense.verification.application;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.DriverLicenseRepository;
import com.ark.driverlicense.verification.infrastructure.client.VerificationResult;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLicenseService {

    private final DriverLicenseRepository driverLicenseRepository;

    @Transactional
    public DriverLicenseDto processVerification(DriverLicense license, VerificationResult result) {
        if (result instanceof VerificationResult.Success) {
            license.verify();
        } else if (result instanceof VerificationResult.Failure failure) {
            license.reject(failure.getReason());
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

