# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy the maven scripts and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execution permission to the maven wrapper
RUN chmod +x mvnw

# Download dependencies (this caches the downloads)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/cctvserver-0.0.1-SNAPSHOT.jar app.jar

# Render will provide the PORT environment variable
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
