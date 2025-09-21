# ---- Build ----
FROM gradle:8.8-jdk17 AS build
WORKDIR /app
# (Enable Gradle cache with BuildKit)
ENV GRADLE_USER_HOME=/home/gradle/.gradle
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV SERVER_PORT=9999
EXPOSE 9999
ENTRYPOINT ["java","-jar","/app/app.jar"]
