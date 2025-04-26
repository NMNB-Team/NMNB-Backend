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