# Build
FROM gradle:8.8-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENV SERVER_PORT=8761
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
