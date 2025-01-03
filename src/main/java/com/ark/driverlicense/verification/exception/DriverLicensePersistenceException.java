package com.ark.driverlicense.verification.exception;

import com.ark.driverlicense.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DriverLicensePersistenceException extends BaseException {

    public DriverLicensePersistenceException(String message) {
        super(message, "DRIVER_LICENSE_PERSISTENCE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
