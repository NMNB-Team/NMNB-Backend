dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //jakarta
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")

    //security
    implementation ("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}