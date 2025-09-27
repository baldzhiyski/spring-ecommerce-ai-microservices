# ---- Dev runtime (hot reload) ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# curl for healthchecks (optional)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Copy wrapper + build files first for better layer caching
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Pre-fetch dependencies for faster first run
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Initial source copy; during dev you'll bind-mount the whole folder
COPY src src

ENV SERVER_PORT=8222
EXPOSE 8222

# Run in dev profile; Spring DevTools must be on the classpath
CMD ["./gradlew","--no-daemon","bootRun","-Pspring.profiles.active=dev"]
