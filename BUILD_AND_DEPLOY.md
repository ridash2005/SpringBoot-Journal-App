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

## Verify Services Integration

This section covers how to verify that the application works correctly with MongoDB, Redis, and Kafka services.

### 1. Start All Services
```bash
# Start MongoDB, Redis, Kafka, and Zookeeper
docker-compose up -d

# Verify services are running
docker-compose ps
```

All services should show as **healthy** (especially MongoDB and Redis which have health checks).

### 2. Build the Application
```bash
# Clean build (skips tests initially)
mvn clean package -DskipTests

# Or build with the default profile (not CI)
mvn clean package -DskipTests -Dspring.profiles.active=default
```

### 3. Run the Application Locally

**Option A: Run with Maven (fastest for testing)**
```bash
mvn spring-boot:run
```

**Option B: Run the JAR directly**
```bash
java -jar target/journalApp-1.0.0.jar
```

**Expected output** (should see):
```
[main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port(s): 8080 (http)
[main] o.rickarya.journalApp.JournalApplication : Started JournalApplication
```

### 4. Verify Services Are Connected

#### Check Health Endpoints
```bash
# Health check (includes MongoDB, Redis, Kafka status)
curl http://localhost:8080/journal/actuator/health

# Should return something like:
# {
#   "status": "UP",
#   "components": {
#     "mongodb": {"status": "UP"},
#     "redis": {"status": "UP"},
#     "kafka": {"status": "UP"}
#   }
# }
```

#### Test MongoDB
```bash
# Create a test user via API
curl -X POST http://localhost:8080/journal/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123"
  }'

# Check if user was saved
mongo mongodb://localhost:27017/journaldb
# In MongoDB shell:
db.user.find()
```

#### Test Redis Cache
```bash
# Connect to Redis
redis-cli

# Check if keys are stored
KEYS *
GET user:testuser

# Ping Redis
PING
# Response: PONG
```

#### Test Kafka
```bash
# Check Kafka topics
docker exec journal-kafka kafka-topics --bootstrap-server localhost:9092 --list

# You should see topics like: weekly-sentiment-group, etc.
```

### 5. Run Integration Tests
```bash
# Run all tests (requires all services running)
mvn test

# Run specific test class
mvn test -Dtest=JournalApplicationTests

# Run with detailed output
mvn test -X
```

### 6. Useful Docker Commands

```bash
# View logs for a specific service
docker-compose logs mongo
docker-compose logs redis
docker-compose logs kafka
docker-compose logs app

# Follow logs in real-time
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes (fresh restart)
docker-compose down -v

# Restart services
docker-compose restart
```

### 7. Quick Verification Checklist

| Service | Command | Expected Result |
|---------|---------|-----------------|
| **MongoDB** | `curl localhost:27017` | Connection refused (expected) |
| **Redis** | `redis-cli ping` | `PONG` |
| **Kafka** | `docker ps \| grep kafka` | Container running |
| **App** | `curl http://localhost:8080/journal/actuator/health` | `{"status":"UP"}` |

### 8. Troubleshooting

```bash
# If services won't start, check Docker daemon
docker ps

# If app crashes, check logs
docker-compose logs app

# If MongoDB won't connect, verify URI
# Should be: mongodb://localhost:27017/journaldb

# If Redis password issues, check docker-compose.dev.yml
# Default dev password is: (empty/no password)

# If tests timeout, increase timeout in pom.xml
# Current: 30000ms, try 60000ms
```

**Quick Start**: `docker-compose up -d && mvn spring-boot:run` and check `http://localhost:8080/journal/actuator/health` ✅

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
