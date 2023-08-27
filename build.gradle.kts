
plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("kapt") version "1.9.10"
}

group = "org.ray"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

allprojects {
}