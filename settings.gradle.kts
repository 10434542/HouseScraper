rootProject.name = "housewebscraper"
include(":backend")


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("testLibs") {

            library("spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("reactor-test", "io.projectreactor", "reactor-test").withoutVersion()
            library("spring-rabbit-test", "org.springframework.amqp", "spring-rabbit-test").withoutVersion()
            library("spring-security-test", "org.springframework.security", "spring-security-test").withoutVersion()

            library("mockk", "io.mockk", "mockk").version("1.13.4")
            library("mockk-jvm", "io.mockk", "mockk-jvm").version("1.13.4")
            library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").version("1.6.4")
            library("springmockk", "com.ninja-squad", "springmockk").version("4.0.2")
            library(
                "spring-cloud-contract-wiremock",
                "org.springframework.cloud",
                "spring-cloud-contract-wiremock"
            ).version("4.0.2")
            library("testcontainers-junit-jupiter", "org.testcontainers", "junit-jupiter").version("1.17.6")
            library("testcontainers-mongodb", "org.testcontainers", "mongodb").version("1.18.1")
        }

        create("libs") {
            library("starter-amqp", "org.springframework.boot", "spring-boot-starter-amqp").withoutVersion()
            library(
                "starter-mongo-reactive",
                "org.springframework.boot",
                "spring-boot-starter-data-mongodb-reactive"
            ).withoutVersion()
            library(
                "starter-oauth2-client",
                "org.springframework.boot",
                "spring-boot-starter-oauth2-client"
            ).withoutVersion()
            library("starter-security", "org.springframework.boot", "spring-boot-starter-security").withoutVersion()
            library("starter-webflux", "org.springframework.boot", "spring-boot-starter-webflux").withoutVersion()
            library("jaxb-api", "javax.xml.bind", "jaxb-api").version("2.3.1")
            library("springdoc-openapi-webflux-ui", "org.springdoc", "springdoc-openapi-starter-webflux-ui").version("2.1.0")

            library("jackson-module-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").withoutVersion()
            library(
                "reactor-kotlin-extensions",
                "io.projectreactor.kotlin",
                "reactor-kotlin-extensions"
            ).withoutVersion()
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").withoutVersion()
            library(
                "kotlinx-coroutines-reactor",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-reactor"
            ).withoutVersion()
            library("jsoup", "org.jsoup", "jsoup").version("1.15.4")
            library(
                "spring-cloud-starter-gateway",
                "org.springframework.cloud",
                "spring-cloud-starter-gateway"
            ).withoutVersion()
            library(
                "spring-cloud-starter-openfeign",
                "org.springframework.cloud",
                "spring-cloud-starter-openfeign"
            ).withoutVersion()
            library("devtools", "org.springframework.boot", "spring-boot-devtools").withoutVersion()
            library(
                "spring-boot-configuration-processor",
                "org.springframework.boot",
                "spring-boot-configuration-processor"
            ).withoutVersion()
            library("mapstruct", "org.mapstruct", "mapstruct").version("1.5.3.Final")
            library("mapstruct-processor", "org.mapstruct", "mapstruct-processor").version("1.5.3.Final")

            library("springdoc-openapi-webflux", "org.springdoc", "springdoc-openapi-webflux-ui").version("1.7.0")
            library("springdoc-openapi-kotlin", "org.springdoc", "springdoc-openapi-kotlin").version("1.7.0")
            library("arrow-core", "io.arrow-kt", "arrow-core").version("1.1.5")
            library("slf4j" ,"org.slf4j", "slf4j-api").version("2.0.7")
            library("lombok", "org.projectlombok", "lombok").version("1.18.20")
            library("logback", "ch.qos.logback","logback-core").version("1.4.7")
            library("kotlin-logging", "io.github.microutils", "kotlin-logging-jvm").version("3.0.5")
        }
    }

}