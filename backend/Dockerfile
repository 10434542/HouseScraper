FROM gradle:7.5.1-jdk17 AS build
WORKDIR /home/gradle/src
COPY settings.gradle.kts .
COPY backend/build.gradle.kts .
COPY backend/src ./src
RUN gradle clean build --no-daemon --stacktrace

FROM openjdk:17-oracle
EXPOSE 9000 5005
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT exec java $JAVA_OPTS -jar "/app/spring-boot-application.jar"