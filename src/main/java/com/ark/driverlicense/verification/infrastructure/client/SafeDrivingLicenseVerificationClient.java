package com.ark.driverlicense.verification.infrastructure.client;

import com.ark.driverlicense.verification.domain.DriverLicense;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class SafeDrivingLicenseVerificationClient implements LicenseVerificationClient {

    private final RestTemplate restTemplate;

    public SafeDrivingLicenseVerificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public VerificationResult verify(DriverLicense license) {
        if (license.getSerialNumber() == null || license.getSerialNumber().length() != 6) {
            throw new IllegalArgumentException("Serial number must be exactly 6 characters.");
        }

        // HTTP 요청에 필요한 헤더 설정
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 전송할 폼 데이터 작성
        String requestData =
            "menuCode=MN-PO-1241" + "&licenLocal=" + license.getType().name() + "&sName="
                + license.getName() + "&sJumin1=" + license.getDateOfBirth().toString().replace(
                "-",
                ""
            ).substring(2) +
                "&licence01=" + license.getLicenseNumber().split("-")[0] +
                "&licence02=" + license.getLicenseNumber().split("-")[1] +
                "&licence03=" + license.getLicenseNumber().split("-")[2] +
                "&licence04=" + license.getLicenseNumber().split("-")[3] +
                "&serialNum=" + license.getSerialNumber() +
                "&answer=any_value";

        HttpEntity<String> httpEntity = new HttpEntity<>(
            requestData,
            requestHeaders
        );

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                VERIFY_URL,
                HttpMethod.POST,
                httpEntity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody() == null ? "" : response.getBody();

                if (responseBody.contains("암호일련번호가 일치합니다.") &&
                    responseBody.contains("한국도로교통공단 전산 자료와 일치합니다.")
                ) {

                    return new VerificationResult.Success(UUID.randomUUID().toString());

                } else if (responseBody.contains("한국도로교통공단 전산 자료와 일치합니다.")) {
                    return new VerificationResult.Failure("암호일련번호가 일치하지 않습니다.");

                } else {
                    return new VerificationResult.Failure(
                        "Verification failed: Conditions not met");
                }
            } else {
                return VerificationResult.PendingManualVerification.INSTANCE;
            }
        } catch (Exception ex) {
            return VerificationResult.PendingManualVerification.INSTANCE;
        }
    }

    private static final String VERIFY_URL =
        "https://www.safedriving.or.kr/LnrForRtnLicns/LnrForRtnLicnsTruthYnComplete.do";
}
