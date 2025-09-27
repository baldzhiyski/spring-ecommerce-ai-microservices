# ---- Dev runtime (runs bootRun) ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install curl for healthchecks (optional)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Copy only what's needed for dependency download first (layer caching)
# Gradle wrapper + build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Prime Gradle dependency cache (first-time speedup)
# --no-daemon is good inside containers
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Copy source once for initial build; in compose we'll bind-mount /app later
COPY src src

# Expose your dev port (adjust if this service uses another)
ENV SERVER_PORT=9999
EXPOSE 9999

# Enable dev profile and devtools restart
# (devtools dependency must be in build.gradle)
CMD ["./gradlew", "--no-daemon", "bootRun", "-Pspring.profiles.active=dev"]
