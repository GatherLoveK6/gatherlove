plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
}

group = "k6"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // ─── Spring Boot Starters ───────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // ─── Observability & Monitoring ────────────────────────────────────────────
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ─── Jakarta Annotations (e.g. @PostConstruct) ────────────────────────────
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // ─── Devtools ──────────────────────────────────────────────────────────────
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ─── Testing ───────────────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // JUnit 4 support is not needed
        exclude(module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit-vintage-engine")
    }
    // Mockito support for @ExtendWith(MockitoExtension.class)
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    // Bean Validation implementation for jakarta.validation.*
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    // Spring Security test helpers (csrf(), etc)
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    testImplementation("org.springframework.security:spring-security-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // ─── JWT (JJWT) ─────────────────────────────────────────────────────────────
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // ─── Database Driver ────────────────────────────────────────────────────────
    implementation("org.postgresql:postgresql")
    testImplementation("com.h2database:h2")

    // ─── Annotation Processing / Lombok ────────────────────────────────────────
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ─── Jakarta Annotations (e.g. @PostConstruct) ────────────────────────────
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // ─── Devtools ──────────────────────────────────────────────────────────────
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ─── Testing ───────────────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // JUnit 4 support is not needed
        exclude(module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit-vintage-engine")
    }
    // Mockito support for @ExtendWith(MockitoExtension.class)
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    // Bean Validation implementation for jakarta.validation.*
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    // Spring Security test helpers (csrf(), etc)
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    testImplementation("org.springframework.security:spring-security-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.postgresql:postgresql:42.5.0")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly   ("org.postgresql:postgresql")
    testImplementation ("com.h2database:h2")
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest") // Exclude functional tests
    }
    finalizedBy(tasks.jacocoTestReport) // Ensure jacocoTestReport runs after test
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }// Make sure jacocoTestReport runs after test
}

sonar {
    properties {
        property("sonar.projectKey", "GatherLoveK6_gatherlove")
        property("sonar.organization", "gatherlovek6")
        property("sonar.host.url", "https://sonarcloud.io")

        // 👇 Add these lines:
        property("sonar.sources", "src/main/java,src/main/resources")
        property("sonar.inclusions", "**/*.java,**/*.html")
        property("sonar.language", "java")
        property("sonar.sourceEncoding", "UTF-8")
    }
}
