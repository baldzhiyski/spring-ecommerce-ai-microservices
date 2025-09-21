# ---- Build stage ----
FROM gradle:8.8-jdk17 AS build
WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle
COPY . .
# cache Gradle deps (requires BuildKit)
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# install curl for healthchecks
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV SERVER_PORT=9999
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
