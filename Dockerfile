# Use lightweight Java 17 runtime
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/hdalepark-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
