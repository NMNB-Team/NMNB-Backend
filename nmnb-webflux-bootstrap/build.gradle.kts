dependencies {
    implementation(project(":nmnb-webflux"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Webflux
    implementation ("org.springframework.boot:spring-boot-starter-webflux")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}