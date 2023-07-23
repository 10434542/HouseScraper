import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.21"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("kapt") version "1.8.21"
}

group = "org.ray"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.0"

dependencies {
    implementation(libs.starter.amqp)
    implementation(libs.starter.mongo.reactive)
    implementation(libs.starter.oauth2.client)
    implementation(libs.starter.security)
    implementation(libs.starter.webflux)
    implementation(libs.jaxb.api)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.jsoup)
    implementation(libs.slf4j)
    implementation(libs.arrow.core)
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.logback)
    developmentOnly(libs.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.mapstruct)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
    kapt(libs.mapstruct.processor)

    testImplementation(testLibs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
    }
    testImplementation(testLibs.reactor.test)
    testImplementation(testLibs.spring.rabbit.test)
    testImplementation(testLibs.spring.security.test)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.jvm)
    testImplementation(testLibs.kotlinx.coroutines.test)
    testImplementation(testLibs.springmockk)
    testImplementation(testLibs.spring.cloud.contract.wiremock)
    testImplementation(testLibs.testcontainers.junit.jupiter)
    testImplementation(testLibs.testcontainers.mongodb)


}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

kapt {
    arguments {
        arg("mapstruct.unmappedTargetPolicy", "ignore")
    }
    keepJavacAnnotationProcessors = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    exclude("**/*ContainerTest*") // this is it
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    useJUnitPlatform()
    include("*ContainerTest")
}
