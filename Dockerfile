# build
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# run
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV KEY_PATH=/app/firebase-service-account-key.json
EXPOSE 8080

# If FIREBASE_KEY_BASE64 is set, use it; otherwise use FIREBASE_KEY_JSON (raw).
CMD if [ -n "$FIREBASE_KEY_BASE64" ]; then \
       echo "$FIREBASE_KEY_BASE64" | base64 -d > "$KEY_PATH"; \
    elif [ -n "$FIREBASE_KEY_JSON" ]; then \
       printf "%s" "$FIREBASE_KEY_JSON" > "$KEY_PATH"; \
    else \
       echo "No FIREBASE key provided (set FIREBASE_KEY_JSON or FIREBASE_KEY_BASE64)"; exit 1; \
    fi && \
    java -jar app.jar
