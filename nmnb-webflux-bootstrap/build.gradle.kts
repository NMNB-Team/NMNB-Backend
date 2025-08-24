dependencies {
    implementation(project(":nmnb-webflux"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Webflux
    implementation ("org.springframework.boot:spring-boot-starter-webflux")

    // R2dbc Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

tasks.bootJar {
    enabled = true
    mainClass.set("nmnb.webfluxBootstrap.NmnbWebfluxApplicationKt")
}

tasks.jar {
    enabled = false
}