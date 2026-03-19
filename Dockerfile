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

# Expose Spring Boot default port
EXPOSE 8080

# Environment variables for database connection
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/smart_traffic_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root123
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
