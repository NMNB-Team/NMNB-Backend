dependencies {
    implementation("org.springframework:spring-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //jakarta
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}