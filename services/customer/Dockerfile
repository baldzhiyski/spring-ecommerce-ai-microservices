# ---- Dev runtime (runs bootRun with hot reload) ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# curl is handy for healthchecks
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Copy wrapper + build files first (better layer caching)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Prime Gradle deps for faster first run
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Initial source copy; during dev we'll bind-mount /app
COPY src src

ENV SERVER_PORT=8090
EXPOSE 8090

# Run with dev profile; DevTools must be added in build.gradle
CMD ["./gradlew","--no-daemon","bootRun","-Pspring.profiles.active=dev"]
