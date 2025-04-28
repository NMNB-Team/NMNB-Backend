dependencies {
    implementation(project(":nmnb-common"))
    implementation(project(":nmnb-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    //mockito
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")

    //security
    implementation ("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
}

val querydslDir = file("build/generated/querydsl")

kapt {
    arguments {
        arg("querydsl.generated", querydslDir.absolutePath)
    }
}

sourceSets {
    named("main") {
        kotlin {
            srcDirs(querydslDir)
        }
    }
}

tasks.named("clean") {
    doLast {
        file(querydslDir).deleteRecursively()
    }
}
tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}