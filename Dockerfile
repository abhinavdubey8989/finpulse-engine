# Multi-stage Dockerfile for Spring Boot application

# ===================================
# Stage 1: Build the application
# ===================================
FROM gradle:8.14-jdk21 AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN gradle bootJar --no-daemon


# ===================================
# Stage 2: Runtime image
# ===================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8055

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8055/actuator/health || exit 1

# Run the application with JAVA_OPTS (must be provided via environment variable)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
