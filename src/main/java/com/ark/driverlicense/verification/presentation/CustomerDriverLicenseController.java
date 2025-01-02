package com.ark.driverlicense.verification.presentation;

import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.application.DriverLicenseService;
import com.ark.driverlicense.verification.presentation.dtos.VerifyLicenseRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver-licenses")
public class CustomerDriverLicenseController {

    private final DriverLicenseService driverLicenseFacade;

    public CustomerDriverLicenseController(DriverLicenseService driverLicenseFacade) {
        this.driverLicenseFacade = driverLicenseFacade;
    }

    @PostMapping("/verify")
    public ResponseEntity<DriverLicenseDto> verifyLicense(
        @Valid @RequestBody VerifyLicenseRequest request
    ) {
        return ResponseEntity.ok(driverLicenseFacade.verifyLicense(request.toDriverLicenseData()));
    }
}
