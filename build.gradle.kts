plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    kotlin("plugin.allopen") version "1.9.25" apply false
    kotlin("plugin.noarg") version "1.9.25" apply false
    kotlin("kapt") version "1.9.25" apply false
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "6.19.0" apply false
}

allprojects {
    group = "cv"
    version = "0.0.1-SNAPSHOT"

    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven("https://jitpack.io")
        mavenCentral()
    }

    project.tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint()
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        dependsOn("spotlessApply")
        dependsOn("updateGitHooks")
    }
}
