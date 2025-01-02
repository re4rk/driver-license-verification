package com.ark.driverlicense.verification.presentation;

import com.ark.driverlicense.verification.presentation.dtos.VerifyLicenseRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ark.driverlicense.verification.application.DriverLicenseService;
import com.ark.driverlicense.verification.application.dtos.DriverLicenseDto;
import com.ark.driverlicense.verification.domain.LicenseStatus;
import com.ark.driverlicense.verification.domain.LicenseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerDriverLicenseController.class)
class CustomerDriverLicenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverLicenseService driverLicenseService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("유효한 요청으로 면허 검증을 수행할 수 있다")
    void verifyLicenseWithValidRequest() throws Exception {
        // given
        VerifyLicenseRequest request = VerifyLicenseRequest.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build();

        DriverLicenseDto response = DriverLicenseDto.builder()
            .id(UUID.randomUUID())
            .licenseNumber(request.licenseNumber())
            .serialNumber(request.serialNumber())
            .type(request.type())
            .name(request.name())
            .dateOfBirth(request.dateOfBirth())
            .issueDate(request.issueDate())
            .expiryDate(request.expiryDate())
            .status(LicenseStatus.VERIFIED)
            .build();

        when(driverLicenseService.verifyLicense(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/driver-licenses/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.licenseNumber").value(request.licenseNumber()))
            .andExpect(jsonPath("$.serialNumber").value(request.serialNumber()))
            .andExpect(jsonPath("$.type").value(request.type().name()))
            .andExpect(jsonPath("$.name").value(request.name()))
            .andExpect(jsonPath("$.status").value(LicenseStatus.VERIFIED.name()));
    }

    @Test
    @DisplayName("잘못된 형식의 면허번호로 요청하면 400 에러가 발생한다")
    void returnBadRequestWhenLicenseNumberFormatIsInvalid() throws Exception {
        // given
        VerifyLicenseRequest request = VerifyLicenseRequest.builder()
            .licenseNumber("invalid-format")
            .serialNumber("ABC123")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build();

        // when & then
        mockMvc.perform(post("/api/v1/driver-licenses/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value(
                "licenseNumber: must match \"\\d{2}-\\d{2}-\\d{6}-\\d{2}\""));
    }

    @Test
    @DisplayName("잘못된 형식의 일련번호로 요청하면 400 에러가 발생한다")
    void returnBadRequestWhenSerialNumberFormatIsInvalid() throws Exception {
        // given
        VerifyLicenseRequest request = VerifyLicenseRequest.builder()
            .licenseNumber("11-22-333333-44")
            .serialNumber("invalid")
            .type(LicenseType.TYPE_1)
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .issueDate(LocalDate.of(2020, 1, 1))
            .expiryDate(LocalDate.of(2025, 1, 1))
            .build();

        // when & then
        mockMvc.perform(post("/api/v1/driver-licenses/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("serialNumber: must match \"[A-Z0-9]{6}$\""));
    }
}
