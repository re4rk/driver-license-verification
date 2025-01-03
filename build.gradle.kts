plugins {
    java
    id("org.springframework.boot") version "3.3.7"
    id("io.spring.dependency-management") version "1.1.7"

    id("checkstyle")
}

checkstyle {
    maxWarnings = 0
    configFile = file("${rootDir}/config/ArkCheckStyle.xml")
    toolVersion = "8.39"
}

group = "com.ark"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    runtimeOnly("org.postgresql:postgresql")

    // RestTemplate
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.1")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
