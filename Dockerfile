# Stage 1: Build the application using the Gradle container
# Use a specific version for reproducible builds
FROM gradle:8.5-jdk17-alpine AS build

LABEL authors="CrusterCrew"

WORKDIR /home/gradle/src

# Copy build files first to leverage Docker's layer cache
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle src ./src

# Build the application, creating the executable JAR. We skip tests for a faster build.
RUN gradle build --no-daemon -x test

# Stage 2: Create the final, lightweight runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*-plain.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]