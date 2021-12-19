plugins {
    id("org.springframework.boot") version "2.4.5"

    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.31"

    application

//    kotlin("jvm")
//    kotlin("plugin.spring") version "1.5.31"


    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.20"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.4.5"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.0-jre")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation(project(":library"))
    implementation(project(":app"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

allOpen {
//    annotation("javax.persistence.Entity")
//    annotation("javax.persistence.Embeddable")
//    annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
