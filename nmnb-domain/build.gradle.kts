dependencies {
    implementation(project(":nmnb-common"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Jakarta
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")

    // Nanoid
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")

    // Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

val querydslDir = file("build/generated/querydsl")

kapt {
    arguments {
        arg("querydsl.generated", querydslDir.absolutePath)
    }
    correctErrorTypes = true
}

sourceSets {
    named("main") {
        kotlin {
            srcDirs(files(querydslDir))
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