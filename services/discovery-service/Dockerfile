# ---- Dev runtime (hot reload enabled) ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Install curl for healthchecks (optional)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Copy wrapper + build files first (better caching)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Pre-fetch dependencies to speed up first run
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Copy source (initial build only – will be bind-mounted later)
COPY src src

ENV SERVER_PORT=8761
EXPOSE 8761

# Run the app in dev profile with hot reload
CMD ["./gradlew","--no-daemon","bootRun","-Pspring.profiles.active=dev"]
