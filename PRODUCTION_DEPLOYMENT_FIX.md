# Production Deployment - MongoDB Connection Fix

**Issue Date**: June 5, 2026  
**Status**: ✅ FIXED

## Problem

When deploying the Journal App in Docker with the production profile, it failed with:
```
com.mongodb.MongoTimeoutException: Timed out after 30000 ms while waiting to connect. 
Client view of cluster state is {type=UNKNOWN, servers=[{address=localhost:27017, type=UNKNOWN, ...}]
```

The app was trying to connect to `localhost:27017` instead of the Docker service `mongo:27017`.

## Root Cause

The `application-prod.yml` file was incomplete. It had:
- ❌ Missing MongoDB URI configuration
- ❌ Missing Redis configuration
- ❌ Missing Kafka configuration
- ❌ Missing email and OAuth configuration

When Spring Boot activated the `production` profile, it used `application-prod.yml`, which didn't override the service endpoints from `application.yml`. Since `application.yml` has `mongodb://localhost:27017/journaldb`, the app tried to connect to localhost instead of the Docker service name `mongo`.

## Solution Implemented

Updated `src/main/resources/application-prod.yml` to:
1. ✅ Include all required service configurations
2. ✅ Use environment variables with proper defaults
3. ✅ Read `MONGODB_URI` from environment (set by docker-compose)
4. ✅ Read `REDIS_HOST`, `KAFKA_SERVERS`, etc. from environment
5. ✅ Fix YAML indentation issues

## Key Changes

### Before (Broken)
```yaml
# application-prod.yml was incomplete
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  data:
    mongodb:
      auto-index-creation: true
# Missing: MongoDB URI, Redis, Kafka, OAuth, Email config
```

### After (Fixed)
```yaml
# application-prod.yml now complete
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/journaldb}  # ← Uses environment variable
      database: journaldb
  redis:
    host: ${REDIS_HOST:localhost}  # ← Uses environment variable
    port: ${REDIS_PORT:6379}
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}  # ← Uses environment variable
  # ... all other configs
```

## Docker Deployment Flow

**docker-compose.yml sets:**
```yaml
environment:
  MONGODB_URI: mongodb://mongo:27017/journaldb  # ← Service name (not localhost)
  REDIS_HOST: redis                               # ← Service name (not localhost)
  KAFKA_SERVERS: kafka:9092                       # ← Service name (not localhost)
  SPRING_PROFILES_ACTIVE: production              # ← Activates production profile
```

**Spring Boot loads:**
```
1. application.yml (base config)
2. application-prod.yml (production overrides)  ← NOW reads env vars correctly
3. Environment variables override YAML values
```

## How to Deploy to Production

### 1. Start the Full Stack
```bash
docker-compose up -d
```

**This will start:**
- MongoDB on `mongo:27017`
- Redis on `redis:6379`
- Kafka on `kafka:9092`
- Journal App on `http://localhost:8080/journal`

### 2. Verify Deployment
```bash
# Check health
curl http://localhost:8080/journal/actuator/health

# Should return:
{
  "status": "UP",
  "components": {
    "mongodb": {"status": "UP"},
    "redis": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

### 3. Check Logs
```bash
# View app logs
docker-compose logs app

# Follow logs in real-time
docker-compose logs -f app

# Check specific service
docker-compose logs mongo
docker-compose logs redis
```

## Production Configuration Profiles

### Default Profile (`application.yml`)
- **Use for**: Local development with `docker-compose.dev.yml`
- **MongoDB**: `localhost:27017`
- **Redis**: `localhost:6379`
- **Kafka**: `localhost:9092`
- **Auto-index**: Enabled

### Production Profile (`application-prod.yml`)
- **Use for**: Docker container deployment with `docker-compose.yml`
- **MongoDB**: Reads from `${MONGODB_URI}` env var → `mongodb://mongo:27017/journaldb`
- **Redis**: Reads from `${REDIS_HOST}` env var → `redis:6379`
- **Kafka**: Reads from `${KAFKA_SERVERS}` env var → `kafka:9092`
- **Auto-index**: Enabled
- **Logging**: Reduced verbosity (WARN level)

### CI Profile (`application-ci.yml`)
- **Use for**: GitHub Actions CI/CD build without services
- **MongoDB**: Auto-index disabled (prevents connection attempts)
- **Built as**: `mvn clean compile -Dspring.profiles.active=ci`

## Environment Variables Required for Production

| Variable | Default | Docker Value | Example |
|----------|---------|--------------|---------|
| `MONGODB_URI` | `mongodb://localhost:27017/journaldb` | `mongodb://mongo:27017/journaldb` | User provides |
| `REDIS_HOST` | `localhost` | `redis` | User provides |
| `REDIS_PORT` | `6379` | `6379` | User provides |
| `REDIS_PASSWORD` | (none) | `redis_password` | User provides |
| `KAFKA_SERVERS` | `localhost:9092` | `kafka:9092` | User provides |
| `GOOGLE_CLIENT_ID` | (required) | (required) | User provides |
| `GOOGLE_CLIENT_SECRET` | (required) | (required) | User provides |
| `JAVA_EMAIL` | (required) | (required) | User provides |
| `JAVA_EMAIL_PASSWORD` | (required) | (required) | User provides |
| `WEATHER_API_KEY` | (required) | (required) | User provides |

## Troubleshooting

### MongoDB Connection Refused
**Error**: `Connection refused at localhost:27017`

**Solution**:
1. Check MongoDB is running: `docker ps | grep mongo`
2. Check logs: `docker-compose logs mongo`
3. Verify `MONGODB_URI` environment variable is set correctly
4. Ensure Docker network is connected: `docker network ls`

### Redis Connection Issues
**Error**: `Redis connection failed`

**Solution**:
1. Check Redis is running: `docker ps | grep redis`
2. Test Redis: `docker exec journal-redis redis-cli ping`
3. Verify `REDIS_HOST` is `redis` (not `localhost`)
4. Check password: `docker-compose logs redis`

### Kafka Connection Issues
**Error**: `Unable to connect to Kafka broker`

**Solution**:
1. Check Kafka is running: `docker ps | grep kafka`
2. Check Zookeeper: `docker ps | grep zookeeper`
3. Verify `KAFKA_SERVERS` is `kafka:9092` (not `localhost:9092`)
4. Check logs: `docker-compose logs kafka`

### App Crashes on Startup
**Error**: `Application context failed to start`

**Solution**:
```bash
# Check full error logs
docker-compose logs app

# Rebuild image
docker-compose build --no-cache

# Restart everything
docker-compose down -v
docker-compose up -d
```

### Ports Already in Use
**Error**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution**:
```bash
# Find process using port 8080
# Linux/Mac:
lsof -i :8080

# Windows PowerShell:
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess

# Kill the process or use different port in docker-compose
# Change port: "8081:8080" instead of "8080:8080"
```

## Performance Optimization

### Production Settings (Already Configured)
```yaml
# Logging: Only WARN and ERROR (reduced I/O)
logging:
  level:
    root: WARN
    io.rickarya.journalApp: INFO

# Tomcat threads optimized
tomcat:
  threads:
    max: 200
    min-spare: 10
  max-connections: 10000

# Response compression enabled
compression:
  enabled: true
  min-response-size: 1024

# Redis caching: 1 hour TTL
cache:
  redis:
    time-to-live: 3600000
```

## Monitoring & Health Checks

### Actuator Endpoints (Production)
```bash
# Health (shows component status)
curl http://localhost:8080/journal/actuator/health

# Basic info
curl http://localhost:8080/journal/actuator/info

# Prometheus metrics
curl http://localhost:8080/journal/actuator/prometheus
```

### Docker Compose Health Checks
```yaml
# MongoDB has health check
healthcheck:
  test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
  interval: 10s
  timeout: 5s
  retries: 5

# Redis has health check
healthcheck:
  test: ["CMD", "redis-cli", "ping"]
  interval: 10s
  timeout: 5s
  retries: 5
```

## Next Steps

1. **Verify deployment works locally**:
   ```bash
   docker-compose up -d
   curl http://localhost:8080/journal/actuator/health
   ```

2. **Deploy to cloud** (Azure, AWS, etc.):
   - Use the same `docker-compose.yml`
   - Set required environment variables
   - Ensure security groups/firewalls allow ports

3. **Setup monitoring**:
   - Configure log aggregation (ELK, Splunk, etc.)
   - Setup metrics collection (Prometheus, Grafana)
   - Configure alerts for health check failures

---

## Files Modified

- ✅ `src/main/resources/application-prod.yml` - Fixed production configuration
- ✅ `docker-compose.yml` - Already has correct environment variables
- ✅ `.github/workflows/build.yml` - Already uses CI profile for CI/CD

## Testing the Fix

```bash
# 1. Start services
docker-compose up -d

# 2. Wait for services to be healthy (30 seconds)
docker-compose ps

# 3. Check app logs
docker-compose logs app

# 4. Test health endpoint
curl http://localhost:8080/journal/actuator/health

# 5. Run integration tests
docker-compose exec app mvn test

# 6. Cleanup
docker-compose down
```

---

**For more information**, see:
- [TESTING_WITH_SERVICES.md](../TESTING_WITH_SERVICES.md) - Service verification guide
- [BUILD_AND_DEPLOY.md](../BUILD_AND_DEPLOY.md) - Build and deployment guide
- [DEPLOYMENT.md](../DEPLOYMENT.md) - Cloud deployment guide
