# ---- Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# ---- Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar /app/app.jar

# Optional: cut noisy DNS lookups
ENV JAVA_OPTS=""

EXPOSE 8080

# If GOOGLE_APPLICATION_CREDENTIALS_JSON is set, write it to a file and point the
# GOOGLE_APPLICATION_CREDENTIALS path at it. Then start the app.
ENTRYPOINT ["/bin/sh","-c","\
  if [ -n \"$GOOGLE_APPLICATION_CREDENTIALS_JSON\" ]; then \
    mkdir -p /etc/creds && printf \"%s\" \"$GOOGLE_APPLICATION_CREDENTIALS_JSON\" > /etc/creds/key.json && \
    export GOOGLE_APPLICATION_CREDENTIALS=/etc/creds/key.json; \
  fi; \
  exec java $JAVA_OPTS -jar /app/app.jar \
"]
