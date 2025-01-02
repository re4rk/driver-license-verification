# 운전면허 진위여부  검증 서비스

운전면허 진위여부를 검증하는 Spring Boot 기반의 서비스입니다. 도로교통공단의 운전면허 진위여부 확인 API를 활용하여 면허 정보를 검증합니다.

## 기능

- 운전면허 정보 검증
    - 면허번호, 일련번호, 면허종류, 이름, 생년월일 등의 정보를 검증
    - 입력된 정보의 형식 유효성 검사
    - 도로교통공단 API를 통한 진위여부 확인
- 검증 결과 관리
    - 검증 성공/실패/보류 상태 관리
    - 검증 실패 시 실패 사유 관리
    - 메타데이터를 통한 추가 정보 관리

## 기술 스택

- Language: Java 17
- Framework: Spring Boot 3.x
- Build Tool: Gradle
- Dependencies:
    - spring-boot-starter-web
    - spring-boot-starter-validation
    - lombok
    - junit5, assertj, mockito

## API 명세

### 운전면허 검증 API

```http
POST /api/v1/driver-licenses/verify
Content-Type: application/json

{
  "licenseNumber": "11-22-333333-44",   // 면허번호 (형식: XX-XX-XXXXXX-XX)
  "serialNumber": "ABC123",             // 일련번호 (형식: [A-Z0-9]{6})
  "type": "TYPE_1",                     // 면허종류
  "name": "홍길동",                      // 이름 (2-4자 한글)
  "dateOfBirth": "1990-01-01",         // 생년월일
  "issueDate": "2020-01-01",           // 발급일자
  "expiryDate": "2025-01-01"           // 만료일자
}
```

#### 응답 예시

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "licenseNumber": "11-22-333333-44",
  "serialNumber": "ABC123",
  "type": "TYPE_1",
  "name": "홍길동",
  "dateOfBirth": "1990-01-01",
  "issueDate": "2020-01-01",
  "expiryDate": "2025-01-01",
  "status": "VERIFIED"
}
```

## 도메인 모델

### DriverLicense (운전면허)

- 운전면허 정보를 관리하는 도메인 엔티티
- 면허상태(PENDING, VERIFIED, REJECTED) 관리
- 메타데이터를 통한 추가 정보 관리

### 면허 상태

- PENDING: 검증 대기
- VERIFIED: 검증 완료
- REJECTED: 거절됨
- EXPIRED: 만료됨

### 면허 종류

- TYPE_1: 1종 보통
- TYPE_2: 2종 보통
- TYPE_1_LARGE: 1종 대형
- TYPE_1_SPECIAL: 1종 특수
- TYPE_2_SMALL: 2종 소형

## 프로젝트 구조

```
com.ark.driverlicense
├── common
│   ├── config
│   ├── error
│   └── exception
├── verification
│   ├── application
│   │   └── dtos
│   ├── domain
│   ├── infrastructure
│   └── presentation
│       └── dtos
└── DriverLicenseApplication.java
```

## 유효성 검사

- 면허번호 형식: `XX-XX-XXXXXX-XX`
- 일련번호 형식: 6자리 영문 대문자 또는 숫자
- 이름: 2-4자 한글
- 날짜 유효성:
    - 생년월일은 과거일자
    - 발급일은 생년월일보다 이후
    - 만료일은 발급일보다 이후

## 예외 처리

| 예외 클래스                        | 에러 코드                  | HTTP 상태 코드 |
|-------------------------------|------------------------|------------|
| InvalidLicenseException       | INVALID_LICENSE        | 400        |
| InvalidLicenseStatusException | INVALID_LICENSE_STATUS | 409        |
| ExternalServiceException      | EXTERNAL_SERVICE_ERROR | 503        |

## 로컬 개발 환경 설정

1. 프로젝트 클론

```bash
git clone https://github.com/re4rk/driver-license-verification.git
```

2. 프로젝트 빌드

```bash
./gradlew clean build
```

3. 애플리케이션 실행

```bash
./gradlew bootRun
```

## 테스트 실행

```bash
./gradlew test
```

## 라이선스

MIT License
