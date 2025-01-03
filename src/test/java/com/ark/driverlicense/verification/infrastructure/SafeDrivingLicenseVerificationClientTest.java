package com.ark.driverlicense.verification.infrastructure;

import com.ark.driverlicense.verification.infrastructure.client.SafeDrivingLicenseVerificationClient;
import com.ark.driverlicense.verification.infrastructure.client.VerificationResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ark.driverlicense.verification.domain.DriverLicense;
import com.ark.driverlicense.verification.domain.LicenseType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SafeDrivingLicenseVerificationClientTest {

    private RestTemplate restTemplate;
    private SafeDrivingLicenseVerificationClient verificationClient;

    @BeforeEach
    void setUp() {
        restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        verificationClient = new SafeDrivingLicenseVerificationClient(restTemplate);
    }

    @Test
    @DisplayName("운전면허 정보가 일치하면 Success를 반환한다")
    void returnSuccessWhenLicenseInfoMatches() {
        // given
        DriverLicense license = createValidLicense();
        String successResponse = "암호일련번호가 일치합니다. 한국도로교통공단 전산 자료와 일치합니다.";

        when(restTemplate.exchange(
            eq("https://www.safedriving.or.kr/LnrForRtnLicns/LnrForRtnLicnsTruthYnComplete.do"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(successResponse, HttpStatus.OK));

        // when
        VerificationResult result = verificationClient.verify(license);

        // then
        assertThat(result).isInstanceOf(VerificationResult.Success.class);
    }

    @Test
    @DisplayName("일련번호가 불일치하면 Failure를 반환한다")
    void returnFailureWhenSerialNumberMismatches() {
        // given
        DriverLicense license = createValidLicense();
        String failureResponse = "한국도로교통공단 전산 자료와 일치합니다.";

        when(restTemplate.exchange(
            eq("https://www.safedriving.or.kr/LnrForRtnLicns/LnrForRtnLicnsTruthYnComplete.do"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(failureResponse, HttpStatus.OK));

        // when
        VerificationResult result = verificationClient.verify(license);

        // then
        assertThat(result).isInstanceOf(VerificationResult.Failure.class);
        VerificationResult.Failure failure = (VerificationResult.Failure) result;
        assertThat(failure.getReason()).isEqualTo("암호일련번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("외부 서비스 오류 발생 시 수동 검증이 필요한 상태를 반환한다")
    void returnPendingManualVerificationWhenExternalServiceFails() {
        // given
        DriverLicense license = createValidLicense();
        when(restTemplate.exchange(
            eq("https://www.safedriving.or.kr/LnrForRtnLicns/LnrForRtnLicnsTruthYnComplete.do"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenThrow(new RuntimeException("External service error"));

        // when
        VerificationResult result = verificationClient.verify(license);

        // then
        assertThat(result).isInstanceOf(VerificationResult.PendingManualVerification.class);
    }

    private DriverLicense createValidLicense() {
        return DriverLicense.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build();
    }
}
