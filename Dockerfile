# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copy the pom.xml and download dependencies
COPY pom.xml .
# Copy the source code
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar
# The port will be dynamically assigned by Railway, but exposing 8080 as a default
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
