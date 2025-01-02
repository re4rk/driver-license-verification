package com.ark.driverlicense.verification.infrastructure;

public sealed interface VerificationResult
        permits VerificationResult.Success, VerificationResult.Failure, VerificationResult.PendingManualVerification {

    record Success(String verificationCode) implements VerificationResult {
        public String getVerificationCode() {
            return verificationCode;
        }
    }

    record Failure(String reason) implements VerificationResult {
        public String getReason() {
            return reason;
        }
    }

    final class PendingManualVerification implements VerificationResult {
        public static final PendingManualVerification INSTANCE = new PendingManualVerification();

        private PendingManualVerification() {
        }

        public static PendingManualVerification getInstance() {
            return INSTANCE;
        }
    }
}
