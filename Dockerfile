# ---- Build stage ----
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /home/gradle/src

# Copy everything into the image
COPY --chown=gradle:gradle . .

# Build the Spring Boot jar (skip tests to speed up)
RUN gradle clean build -x test

# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /home/gradle/src/build/libs/mylists-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
