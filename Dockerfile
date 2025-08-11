# Use an official Maven image to build the app
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use a lightweight Java runtime image for deployment
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Decode the Firebase key from the environment variable
CMD echo "$FIREBASE_KEY_BASE64" | base64 -d > /app/firebase-service-account-key.json && \
    java -jar app.jar
