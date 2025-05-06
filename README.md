# Classics-Put-Simply: Making Classics Accessible for Language Development

## About

Classics-Put-Simply is an educational project designed to make classic legends, stories, and fairy tales highly accessible. 
It aims to provide valuable language development stimuli for children with language deficits, hearing impairments, as well as those growing up in multilingual environments. 
The project provides a REST API to deliver story content.

## Getting Started

This guide will help you get the backend REST API up and running.

### Tech Stack

* **Backend REST API:** Java Spring Boot with Gradle

### Prerequisites

* **Java Development Kit (JDK):** Ensure you have a compatible JDK installed on your system. JDK version 21 is currently recommended.
* **Gradle:** The project uses Gradle as its build tool.

### Steps to Run the REST API (Port 8080)

1.  **Build the Application:**
    Navigate to the root directory of the project in your terminal and run the Gradle build command:

    ```bash
    ./gradlew build
    ```
    This command will download dependencies and build the Spring Boot application JAR file.
    Clean build & refresh:
    ```bash
    ./gradlew --refresh-dependencies clean build
    ```

2.  **Build the JAR File:**
    After a successful build, create the bootable JAR file using the following Gradle task:

    ```bash
    ./gradlew bootJar
    ```

    The generated JAR file will be located in the `build/libs/` directory. Its name will be similar to `classicsputsimply-0.0.1-SNAPSHOT.jar`.

3.  **Run the REST API:**
    Execute the JAR file using the Java command:

    ```bash
    java -jar ./build/libs/classicsputsimply-0.0.1-SNAPSHOT.jar
    ```

    This will start the Spring Boot application, and it will be accessible on port `8080` by default. 
You should see logs in your console indicating that the application has started.

## Testing the API

Once the application is running, you can test the following endpoints:

### Available Endpoints

* **List All Classics:** Retrieve a list of all available classic stories (including their IDs, titles, and slugs).
    ```
    GET http://localhost:8080/api/v1/classics
    ```

* **Get Story Content by Slug:** Retrieve the content of a specific classic story by its unique slug and an optional language parameter.
    ```
    GET http://localhost:8080/api/v1/classics/story/little-red-riding-hood?lang=en
    ```
  (Replace `little-red-riding-hood` with the desired story slug and `en` with the desired language code.)

### Swagger UI

For a more interactive way to explore and test the API endpoints, you can access the Swagger UI:
http://localhost:8080/swagger-ui/index.html

### API Docs

http://localhost:8080/api-docs

### Actuator
Endpoints
http://localhost:8080/actuator/health: Shows the health status of your application (e.g., UP, DOWN).
http://localhost:8080/actuator/info: Displays general information about your application (you can customize this).
http://localhost:8080/actuator/metrics: Provides various metrics about your application's performance (e.g., memory usage, CPU usage, HTTP request counts). You can often drill down to specific metrics by appending their name (e.g., /actuator/metrics/jvm.memory.used).
http://localhost:8080/actuator/loggers: Allows you to view and modify the logging levels of your application at runtime.
http://localhost:8080/actuator/threaddump: Provides a snapshot of the threads in your application.
http://localhost:8080/actuator/heapdump: Generates a heap dump of your application (can be large).
http://localhost:8080/actuator/env: Shows the application's environment properties.
http://localhost:8080/actuator/configprops: Displays the application's configuration properties.

Configutation in application.properties e.g. for specific endpoints:
management.endpoints.web.exposure.include=health,info,metrics
or for all:
management.endpoints.web.exposure.include=*

### Run Tests

    ```bash
    ./gradlew test
    ```
