plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.allopen") version "1.9.25"
    kotlin("plugin.noarg") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless").version("6.19.0")
}

group = "cv"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.github.consoleau:kassava:2.1.0")
    //Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    //jakarta
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
    //nanoid
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
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

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}


tasks.register<Copy>("updateGitHooks") {
    from(File(rootProject.rootDir, ".script/pre-commit"))
    into(File(rootProject.rootDir, ".git/hooks"))

    doLast {
        val osName = System.getProperty("os.name").lowercase()
        val command = if (osName.contains("windows")) {
            arrayOf("cmd", "/c", "chmod -R +x .git/hooks/")
        } else {
            arrayOf("chmod", "-R", "+x", ".git/hooks/")
        }
        Runtime.getRuntime().exec(command)
    }
}

tasks.named("compileKotlin") {
    dependsOn("spotlessApply")
    dependsOn("updateGitHooks")
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}