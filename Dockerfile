# Multi-stage build for optimized image size
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Final stage - lightweight runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Fly.io provides PORT at runtime; default to 8080 locally
ENV PORT=8080
EXPOSE ${PORT}

# Database config comes from Fly.io environment variables at runtime
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Run the application with Fly.io's dynamic port
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
