# Multi-stage build for production-ready Docker image
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy only POM files first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -X

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Rickarya Das <rickaryadas@gmail.com>"
LABEL description="Journal App - Secure End-to-End Encrypted Journal Application"
LABEL version="1.0.0"

WORKDIR /app

# Add non-root user for security
RUN addgroup -g 1000 -S appuser && \
    adduser -u 1000 -S appuser -G appuser

# Copy the built application from builder stage
COPY --from=builder /app/target/journalApp-*.jar app.jar

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/journal/health/status || exit 1

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "app.jar"]
