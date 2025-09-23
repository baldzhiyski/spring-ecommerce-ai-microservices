# ---- Build stage ----
FROM gradle:8.8-jdk21 AS build
WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# install curl for healthchecks
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV SERVER_PORT=8050
EXPOSE 8050
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
