FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ ./gradle/

RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]