dependencies {
    implementation(project(":nmnb-application"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}