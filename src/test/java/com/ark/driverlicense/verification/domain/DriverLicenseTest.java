package com.ark.driverlicense.verification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ark.driverlicense.verification.exception.InvalidLicenseException;
import com.ark.driverlicense.verification.exception.InvalidLicenseStatusException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DriverLicenseTest {

    @Test
    @DisplayName("유효한 데이터로 운전면허 객체를 생성할 수 있다")
    void createDriverLicenseWithValidData() {
        // given
        String licenseNumber = "11-22-333333-44";
        String serialNumber = "ABC123";
        LicenseType type = LicenseType.TYPE_1;
        String name = "홍길동";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        LocalDate issueDate = LocalDate.of(2020, 1, 1);
        LocalDate expiryDate = LocalDate.of(2025, 1, 1);

        // when
        DriverLicense license = DriverLicense.builder()
            .licenseNumber(licenseNumber)
            .serialNumber(serialNumber)
            .type(type)
            .name(name)
            .dateOfBirth(dateOfBirth)
            .issueDate(issueDate)
            .expiryDate(expiryDate)
            .build();

        // then
        assertThat(license.getLicenseNumber()).isEqualTo(licenseNumber);
        assertThat(license.getSerialNumber()).isEqualTo(serialNumber);
        assertThat(license.getType()).isEqualTo(type);
        assertThat(license.getName()).isEqualTo(name);
        assertThat(license.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(license.getIssueDate()).isEqualTo(issueDate);
        assertThat(license.getExpiryDate()).isEqualTo(expiryDate);
        assertThat(license.getStatus()).isEqualTo(LicenseStatus.PENDING);
    }

    @Test
    @DisplayName("잘못된 형식의 면허번호로 객체를 생성하면 예외가 발생한다")
    void throwExceptionWhenLicenseNumberFormatIsInvalid() {
        // given
        String invalidLicenseNumber = "123456789";

        // when & then
        assertThatThrownBy(() -> DriverLicense
            .builder()
            .licenseNumber(invalidLicenseNumber)
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build()
        )
            .isInstanceOf(InvalidLicenseException.class)
            .hasMessage("Invalid license number format");
    }

    @Test
    @DisplayName("잘못된 형식의 일련번호로 객체를 생성하면 예외가 발생한다")
    void throwExceptionWhenSerialNumberFormatIsInvalid() {
        // given
        String invalidSerialNumber = "123";

        // when & then
        assertThatThrownBy(() -> DriverLicense.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber(invalidSerialNumber)
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build()
        )
            .isInstanceOf(InvalidLicenseException.class)
            .hasMessage("Invalid serial number format");
    }

    @Test
    @DisplayName("발급일이 만료일보다 늦으면 예외가 발생한다")
    void throwExceptionWhenIssueDateIsAfterExpiryDate() {
        // given
        LocalDate issueDate = LocalDate.of(2025, 1, 1);
        LocalDate expiryDate = LocalDate.of(2020, 1, 1);

        // when & then
        assertThatThrownBy(() -> DriverLicense.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(issueDate)
            .expiryDate(expiryDate)
            .build()
        )
            .isInstanceOf(InvalidLicenseException.class)
            .hasMessage("Issue date must be before expiry date");
    }

    @Test
    @DisplayName("생년월일이 발급일보다 늦으면 예외가 발생한다")
    void throwExceptionWhenDateOfBirthIsAfterIssueDate() {
        // given
        LocalDate dateOfBirth = LocalDate.of(2025, 1, 1);
        LocalDate issueDate = LocalDate.of(2020, 1, 1);

        // when & then
        assertThatThrownBy(() -> DriverLicense.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(dateOfBirth)
            .issueDate(issueDate)
            .expiryDate(LocalDate.of(2030, 1, 1))
            .build()
        )
            .isInstanceOf(InvalidLicenseException.class)
            .hasMessage("Date of birth must be before issue date");
    }

    @Test
    @DisplayName("면허를 검증할 수 있다")
    void verifyLicense() {
        // given
        DriverLicense license = createValidLicense();

        // when
        license.verify();

        // then
        assertThat(license.getStatus()).isEqualTo(LicenseStatus.VERIFIED);
    }

    @Test
    @DisplayName("이미 검증된 면허는 다시 검증할 수 없다")
    void cannotVerifyAlreadyVerifiedLicense() {
        // given
        DriverLicense license = createValidLicense();
        license.verify();

        // when & then
        assertThatThrownBy(license::verify)
            .isInstanceOf(InvalidLicenseStatusException.class)
            .hasMessage("License must be in PENDING status to be verified");
    }

    @Test
    @DisplayName("면허를 거절할 수 있다")
    void rejectLicense() {
        // given
        DriverLicense license = createValidLicense();
        String rejectionReason = "Invalid information";

        // when
        license.reject(rejectionReason);

        // then
        assertThat(license.getStatus()).isEqualTo(LicenseStatus.REJECTED);
        assertThat(license.getMetadata()).containsEntry("rejectionReason", rejectionReason);
    }

    @Test
    @DisplayName("이미 거절된 면허는 다시 거절할 수 없다")
    void cannotRejectAlreadyRejectedLicense() {
        // given
        DriverLicense license = createValidLicense();
        license.reject("Some reason");

        // when & then
        assertThatThrownBy(() -> license.reject("Another reason"))
            .isInstanceOf(InvalidLicenseStatusException.class)
            .hasMessage("License must be in PENDING status to be rejected");
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
