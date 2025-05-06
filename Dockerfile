FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy build files and gradle wrapper
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ ./gradle/

# Copy the application source code
COPY src/ ./src/

RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN ls -l /app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
