package com.ark.driverlicense.verification.presentation;

import com.ark.driverlicense.verification.application.DriverLicenseService;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.presentation.dtos.VerifyLicenseRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/list")
    public ResponseEntity<List<DriverLicenseDto>> listDriverLicenses() {
        return ResponseEntity.ok(driverLicenseFacade.listDriverLicenses());
    }
}
