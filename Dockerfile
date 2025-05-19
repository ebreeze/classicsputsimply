FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy build files and gradle wrapper
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ ./gradle/

# Copy only necessary parts of the source code for dependency resolution and caching
COPY src/main/java ./src/main/java
COPY src/main/resources ./src/main/resources

# Resolve and cache dependencies
RUN chmod +x gradlew
RUN ./gradlew dependencies --write-locks

# Copy the entire source code now that dependencies are cached
COPY src/ ./src/

# Build the application skipping tests and linting to improve build performance
RUN ./gradlew bootJar -x test -x check

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN ls -l /app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]