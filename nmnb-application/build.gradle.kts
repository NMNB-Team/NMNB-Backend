dependencies {
    implementation(project(":nmnb-common"))
    implementation(project(":nmnb-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Mockito
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")

    // Security
    implementation ("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // Jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Redis
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")
    implementation ("org.springframework.session:spring-session-data-redis")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation ("com.github.ben-manes.caffeine:caffeine")

    // S3
    implementation("software.amazon.awssdk:s3:2.19.1")

    // Nanoid
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}