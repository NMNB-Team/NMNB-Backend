dependencies {
    implementation(project(":nmnb-common"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Webflux
    implementation ("org.springframework.boot:spring-boot-starter-webflux")

    // r2dbc-mysql
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.asyncer:r2dbc-mysql:1.4.0")

    // Nanoid
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}