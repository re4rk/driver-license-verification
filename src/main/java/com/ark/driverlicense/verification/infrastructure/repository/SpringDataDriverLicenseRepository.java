package com.ark.driverlicense.verification.infrastructure.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDriverLicenseRepository extends JpaRepository<JpaDriverLicense, UUID> {

}
