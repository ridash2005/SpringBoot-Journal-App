# Journal App - Build & Deployment Guide

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized setup)
- Git

## Local Build & Testing

### Important: Tests Require Local Services
Tests require MongoDB, Redis, and Kafka to be running. 

**Start services first:**
```bash
docker-compose up -d
```

### Option 1: Using Shell Script (Linux/Mac)
```bash
chmod +x scripts/build-test.sh
./scripts/build-test.sh
```

### Option 2: Using Batch Script (Windows)
```cmd
scripts\build-test.bat
```

### Option 3: Manual Maven Commands
```bash
# Start services in background (if not already running)
docker-compose up -d

# Clean build with tests
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Verify JAR
ls -lh target/journalApp-1.0.0.jar
```

### CI/CD Build (GitHub Actions)
The GitHub Actions workflow skips tests because external services (MongoDB, Redis, Kafka) are not available in the CI environment.

Tests run: **Locally only (with docker-compose)**  
Build runs: **Both locally and in CI/CD**

## Docker Build & Run

### Build Docker Image
```bash
docker build -t journalapp:latest .
```

### Run with Docker Compose (Full Stack)
```bash
docker-compose up -d
```

This starts:
- MongoDB (port 27017)
- Redis (port 6379)
- Kafka + Zookeeper (port 9092)
- Journal App (port 8080)

### Health Check
```bash
# Check application status
curl http://localhost:8080/journal/health/status

# Expected response:
# {
#   "status": "UP",
#   "application": "Journal App",
#   "version": "1.0.0",
#   "mongodb": "UP",
#   "redis": "UP"
# }
```

## GitHub Actions CI/CD Pipeline

The `.github/workflows/build.yml` workflow automatically:

1. ✓ **Compiles** the application
2. ✓ **Runs** unit tests
3. ✓ **Packages** the JAR
4. ✓ **Builds** Docker image
5. ✓ **Analyzes** code quality (SonarCloud)
6. ✓ **Verifies** all dependencies

### Required GitHub Secrets

For the CI/CD pipeline to work, add these secrets to your GitHub repository:
- `SONAR_TOKEN`: SonarCloud API token
- `GITHUB_TOKEN`: (automatically provided)

## Pushing to GitHub

### 1. Prepare Changes
```bash
git add .
git commit -m "Fix: GoogleAuthController import and add health checks"
```

### 2. Push to Master Branch
```bash
git push origin master
```

### 3. Verify Pipeline
Visit: `https://github.com/rickarya/journalapp/actions`

The build should complete in ~3-5 minutes showing:
- ✓ Build and Analyze
- ✓ Docker Build
- ✓ Deploy (if on master branch)

## Environment Variables Required

### Required for Production
- `MONGODB_URI`: MongoDB connection string
- `REDIS_HOST`: Redis hostname
- `REDIS_PASSWORD`: Redis password
- `KAFKA_SERVERS`: Kafka bootstrap servers
- `GOOGLE_CLIENT_ID`: Google OAuth client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth client secret
- `JAVA_EMAIL`: Email for notifications
- `JAVA_EMAIL_PASSWORD`: Email password
- `WEATHER_API_KEY`: Weather API key

### Production Deployment
Set these environment variables before running the Docker container:

```bash
docker run -e MONGODB_URI=mongodb://... \
           -e REDIS_PASSWORD=... \
           -e GOOGLE_CLIENT_ID=... \
           -e GOOGLE_CLIENT_SECRET=... \
           -p 8080:8080 \
           journalapp:latest
```

## Troubleshooting

### Build Failures
1. Check Java version: `java -version` (should be 17+)
2. Clear Maven cache: `mvn clean`
3. Check network connectivity

### Docker Issues
1. Ensure Docker daemon is running
2. Check disk space: `docker system df`
3. Clean up: `docker system prune`

### Application Won't Start
1. Check logs: `docker logs journal-app`
2. Verify environment variables are set
3. Check MongoDB/Redis connectivity

## Success Criteria

✓ Application builds successfully  
✓ All tests pass  
✓ Docker image builds without errors  
✓ GitHub Actions pipeline completes  
✓ Health check endpoint responds with UP status  
✓ MongoDB and Redis connections are verified  

---

**Last Updated:** June 5, 2026
