package com.ark.driverlicense.verification.exception;

import com.ark.driverlicense.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidLicenseException extends BaseException {

    public InvalidLicenseException(String message) {
        super(message, "INVALID_LICENSE", HttpStatus.BAD_REQUEST);
    }
}

