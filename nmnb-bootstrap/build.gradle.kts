dependencies {
    implementation(project(":nmnb-application"))
    implementation(project(":nmnb-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}

tasks.bootJar {
    enabled = true
    mainClass.set("nmnb.bootstrap.NmnbBackApplicationKt")
}

tasks.jar {
    enabled = false
}