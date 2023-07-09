
plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.8.21"
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