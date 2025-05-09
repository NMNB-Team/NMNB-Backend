dependencies {
    implementation(project(":nmnb-r2dbc"))
    implementation(project(":nmnb-common"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Webflux
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // r2dbc-mysql
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.asyncer:r2dbc-mysql:1.4.0")
    // r2dbc-coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    // ConfigurationProperties
    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")

    // S3
    implementation("software.amazon.awssdk:s3:2.19.1")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}