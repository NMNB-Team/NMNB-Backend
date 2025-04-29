dependencies {
    implementation(project(":nmnb-application"))
    implementation(project(":nmnb-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}