package com.ark.driverlicense.verification.domain;

public enum LicenseStatus {
    PENDING,    // 검증 대기
    VERIFIED,   // 검증 완료
    REJECTED,   // 거절됨
    EXPIRED     // 만료됨
}
