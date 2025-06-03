dependencies {
    implementation("org.springframework:spring-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //jakarta
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-common:2.3.0")

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