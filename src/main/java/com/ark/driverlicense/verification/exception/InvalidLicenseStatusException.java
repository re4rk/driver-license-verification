package com.ark.driverlicense.verification.exception;

import com.ark.driverlicense.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidLicenseStatusException extends BaseException {

    public InvalidLicenseStatusException(String message) {
        super(message, "INVALID_LICENSE_STATUS", HttpStatus.CONFLICT);
    }
}
