# Docker Image Rebuild Instructions

**Date**: June 5, 2026  
**Latest Commit**: `29352a4` - Production configuration fixes

## Prerequisites

- Docker installed and running
- docker-compose installed
- Latest code pulled from GitHub (`main` branch, commit `29352a4`)

## Step 1: Clean Up Old Image and Containers

```bash
# Stop and remove all containers
docker compose down

# Remove the old image (optional, but recommended for clean rebuild)
docker rmi journalapp:latest
docker image prune -f
```

## Step 2: Rebuild Docker Image

```bash
# Navigate to project directory
cd journalApp

# Rebuild with no cache (forces rebuild of all layers)
docker compose build --no-cache
```

**Expected output:**
```
Building app
...
Successfully tagged journalapp:latest
```

## Step 3: Start Services

```bash
# Start all services (MongoDB, Redis, Kafka, App)
docker compose up -d

# Wait for services to be healthy (30 seconds)
sleep 30

# Check status
docker compose ps
```

**Expected output - All services running:**
```
NAME                COMMAND                  SERVICE      STATUS      PORTS
journal-mongo       "docker-entrypoint.sh"   mongo         Up (healthy)    27017/tcp
journal-redis       "redis-server"           redis        Up (healthy)    6379/tcp
journal-zookeeper   "sh -c 'exec /etc..."    zookeeper    Up               2181/tcp
journal-kafka       "sh -c 'exec /etc..."    kafka        Up               9092/tcp
journal-app         "java -jar /app/a..."    app          Up               8080/tcp
```

## Step 4: Verify the Fix

```bash
# Check application health
curl http://localhost:8080/journal/actuator/health

# Expected response (MongoDB, Redis, Kafka all UP):
{
  "status": "UP",
  "components": {
    "mongodb": {"status": "UP"},
    "redis": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

### View Application Logs

```bash
# Follow logs in real-time
docker compose logs -f app

# You should see:
# ✅ "Started JournalApplication"
# ✅ NO timeout errors
# ✅ NO "Connection refused" errors
```

## What Changed in the New Build

### 1. ✅ Fixed `application-prod.yml`
- **Before**: Missing MongoDB, Redis, Kafka configurations
- **After**: Complete with all service endpoints using environment variables

```yaml
# Now correctly reads from environment:
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/journaldb}  # ← Uses env var
  redis:
    host: ${REDIS_HOST:localhost}                              # ← Uses env var
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}         # ← Uses env var
```

### 2. ✅ Docker Compose Environment Variables
```yaml
environment:
  MONGODB_URI: mongodb://mongo:27017/journaldb    # ← Service name (not localhost)
  REDIS_HOST: redis                                # ← Service name (not localhost)
  KAFKA_SERVERS: kafka:9092                       # ← Service name (not localhost)
  SPRING_PROFILES_ACTIVE: production              # ← Uses production profile
```

## Troubleshooting

### Problem: Services won't start
```bash
# Check Docker daemon
docker ps

# Check disk space
docker system df

# Clean up old resources
docker system prune -a
```

### Problem: App still fails to connect
```bash
# Rebuild completely fresh
docker compose down -v
docker system prune -f
docker compose build --no-cache
docker compose up -d

# Wait and check
sleep 30
docker compose logs app
```

### Problem: Port already in use
```bash
# Find what's using the port
# Linux/Mac:
lsof -i :8080

# Windows PowerShell:
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess

# Or change port in docker-compose.yml: "8081:8080"
```

## Success Indicators

✅ **Build complete**:
- Docker image tagged `journalapp:latest`
- All containers running and healthy

✅ **App startup successful**:
- Log shows "Started JournalApplication"
- NO MongoTimeoutException
- NO Connection refused errors

✅ **Services connected**:
- Health endpoint returns UP for all components
- Can create users via API
- Can query databases
- Kafka topics available

## Revert if Needed

If something goes wrong, revert to previous image:

```bash
# Stop current services
docker compose down

# Remove problematic image
docker rmi journalapp:latest

# Rebuild from previous commit (if you have an older image)
# OR push this commit and pull the older one
```

## CI/CD Automatic Rebuild

The image also rebuilds automatically when:
1. Code is pushed to `main` branch
2. GitHub Actions workflow `.github/workflows/build.yml` runs
3. Docker image is built as part of the pipeline

---

## Files in This Build

- ✅ `src/main/resources/application-prod.yml` - **FIXED** production configuration
- ✅ `src/main/resources/application.yml` - Default development configuration
- ✅ `src/main/resources/application-ci.yml` - CI/CD configuration
- ✅ `Dockerfile` - Docker image definition
- ✅ `docker-compose.yml` - Full stack orchestration
- ✅ All Java source files - Latest codebase

---

**Ready to rebuild?** Follow **Steps 1-4** above on a machine with Docker installed! 🚀
