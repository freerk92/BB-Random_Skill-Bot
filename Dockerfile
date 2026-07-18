# Build stage: Gradle image includes Gradle, so no wrapper is required
FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle shadowJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/bb-random-skill-bot.jar app.jar
CMD ["java", "-jar", "app.jar"]
