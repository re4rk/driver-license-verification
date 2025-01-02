package com.ark.driverlicense.verification.exception;

import com.ark.driverlicense.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BaseException {

    public ExternalServiceException(String message) {
        super(message, "EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
