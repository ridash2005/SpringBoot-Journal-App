# Testing with Services - MongoDB, Redis, and Kafka

**Last Updated**: June 5, 2026

This guide explains how to verify that the Journal App works correctly with all required services: MongoDB, Redis, and Kafka.

---

## Table of Contents
1. [Quick Start](#quick-start)
2. [Detailed Verification Steps](#detailed-verification-steps)
3. [Service Health Checks](#service-health-checks)
4. [Docker Management Commands](#docker-management-commands)
5. [Troubleshooting](#troubleshooting)

---

## Quick Start

```bash
# Start all services
docker-compose up -d

# Build the application
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run

# Verify in another terminal
curl http://localhost:8080/journal/actuator/health
```

---

## Detailed Verification Steps

### Step 1: Verify Services Are Running
```bash
docker-compose ps
```

Expected output (all should be running):
- **journal-mongo** (MongoDB)
- **journal-redis** (Redis)
- **journal-kafka** (Kafka)
- **journal-zookeeper** (Zookeeper)

### Step 2: Build the Application
```bash
# Skip tests for now - just verify services work
mvn clean package -DskipTests

# Or use Maven spring-boot plugin for development
mvn spring-boot:run
```

### Step 3: Check Application Startup Logs
Look for these messages in the console:
```
[main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080
[main] o.rickarya.journalApp.JournalApplication : Started JournalApplication
```

### Step 4: Test MongoDB Connection
```bash
# Via API - Create a test user
curl -X POST http://localhost:8080/journal/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123"
  }'

# Direct MongoDB check
mongo mongodb://localhost:27017/journaldb
# In MongoDB shell:
> db.user.findOne()
# Should return your user

# Exit MongoDB shell
> exit
```

### Step 5: Test Redis Connection
```bash
# Connect to Redis CLI
redis-cli

# Test basic operations
127.0.0.1:6379> PING
PONG

# Check stored cache keys
127.0.0.1:6379> KEYS *
# Should see keys like: user:*, journal:*, etc.

# Check specific key
127.0.0.1:6379> GET user:testuser

# Exit Redis
127.0.0.1:6379> EXIT
```

### Step 6: Test Kafka Connection
```bash
# Check Kafka topics
docker exec journal-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list

# You should see topics created by the app
# Example: weekly-sentiment-group

# Check topic details
docker exec journal-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe --topic weekly-sentiment-group
```

### Step 7: Run Integration Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JournalApplicationTests

# Run with verbose output
mvn test -X

# Run only controller tests
mvn test -Dtest=*Controller*Test
```

---

## Service Health Checks

### API Health Endpoint
```bash
curl http://localhost:8080/journal/actuator/health
```

**Response format:**
```json
{
  "status": "UP",
  "components": {
    "mongodb": {
      "status": "UP",
      "details": {
        "version": "7.0"
      }
    },
    "redis": {
      "status": "UP"
    },
    "kafka": {
      "status": "UP"
    }
  }
}
```

### Docker Health Status
```bash
# Check if MongoDB is healthy
docker inspect journal-mongo | grep -A 5 "Health"

# Check if Redis is healthy
docker inspect journal-redis | grep -A 5 "Health"

# Check if Kafka is running
docker inspect journal-kafka | grep -A 5 "State"
```

---

## Docker Management Commands

### View Service Logs
```bash
# View MongoDB logs
docker-compose logs mongo

# View Redis logs
docker-compose logs redis

# View Kafka logs
docker-compose logs kafka

# View Application logs
docker-compose logs app

# Follow logs in real-time (-f flag)
docker-compose logs -f app

# View last 50 lines
docker-compose logs --tail=50 mongo
```

### Manage Services
```bash
# Stop all services (keep volumes)
docker-compose stop

# Start services again
docker-compose start

# Restart all services
docker-compose restart

# Stop and remove containers (keep volumes)
docker-compose down

# Stop and remove everything (including volumes - fresh restart)
docker-compose down -v

# Remove only volumes
docker volume prune
```

### Debug Individual Services
```bash
# Access MongoDB shell in container
docker exec -it journal-mongo mongosh

# Access Redis CLI in container
docker exec -it journal-redis redis-cli

# Access Kafka broker
docker exec -it journal-kafka bash

# Access Zookeeper
docker exec -it journal-zookeeper zkCli.sh -server localhost:2181
```

---

## Service Details

### MongoDB (Port 27017)
- **Image**: mongo:7.0-alpine
- **Database**: journaldb
- **Volume**: mongo_data (persistent)
- **Health Check**: Every 10 seconds
- **Default Connection**: mongodb://localhost:27017/journaldb

### Redis (Port 6379)
- **Image**: redis:7-alpine
- **Volume**: redis_data (persistent)
- **Health Check**: Every 10 seconds
- **Default Connection**: localhost:6379 (no password in dev)

### Kafka (Port 9092)
- **Image**: confluentinc/cp-kafka:7.5.0
- **Depends On**: Zookeeper (port 2181)
- **Auto Topic Creation**: Enabled
- **Bootstrap Servers**: localhost:9092

### Zookeeper (Port 2181)
- **Image**: confluentinc/cp-zookeeper:7.5.0
- **Role**: Coordinates Kafka brokers
- **Persistence**: In-memory (dev environment)

---

## Troubleshooting

### Problem: Services won't start
```bash
# Check Docker daemon status
docker ps

# Check if ports are already in use
# MongoDB (27017), Redis (6379), Kafka (9092)
netstat -an | grep 27017

# Kill processes using those ports
lsof -i :27017
kill -9 <PID>
```

### Problem: App crashes on startup
```bash
# Check application logs
docker-compose logs app

# Check MongoDB is healthy
docker-compose logs mongo

# Restart with verbose output
docker-compose up
```

### Problem: MongoDB connection refused
```bash
# Verify MongoDB container is running
docker ps | grep mongo

# Check MongoDB logs
docker-compose logs mongo

# Verify connection string is correct
# Should be: mongodb://localhost:27017/journaldb
# NOT: mongodb://127.0.0.1:27017/...

# Try connecting directly
mongo mongodb://localhost:27017/journaldb
```

### Problem: Redis connection issues
```bash
# Check Redis is running
docker ps | grep redis

# Test Redis connection
redis-cli ping

# If password error, check docker-compose.yml
# Dev environment has NO password by default
```

### Problem: Kafka topics not created
```bash
# Check if Kafka is healthy
docker-compose logs kafka | tail -20

# Manually create a topic
docker exec journal-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic weekly-sentiment-group \
  --partitions 1 \
  --replication-factor 1

# List all topics
docker exec journal-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list
```

### Problem: Tests timeout
```bash
# Increase test timeout in pom.xml
# Look for: <timeout>30000</timeout>
# Change to: <timeout>60000</timeout>

# Or run tests with more verbose output
mvn test -X

# Run single test only
mvn test -Dtest=JournalApplicationTests -X
```

### Problem: Port already in use
```bash
# Find process using port (example: 27017)
# Linux/Mac:
lsof -i :27017

# Windows PowerShell:
Get-Process -Id (Get-NetTCPConnection -LocalPort 27017).OwningProcess

# Kill the process
kill -9 <PID>

# Or use different ports in docker-compose.yml
# Example: "27018:27017" (external:internal)
```

---

## Performance Tips

### Optimize Local Development
```bash
# 1. Use volume mounts for faster updates (already in docker-compose)
# 2. Skip tests during development if they're slow
mvn clean package -DskipTests

# 3. Use Maven incremental compilation
mvn clean compile -DskipTests

# 4. Run only changed tests
# Requires Maven 2.2.0+
```

### Clean Up Resources
```bash
# Remove unused Docker images
docker image prune

# Remove unused volumes
docker volume prune

# Remove unused networks
docker network prune

# Remove everything (WARNING: will delete data)
docker system prune -a
```

---

## Verification Checklist

- [ ] All Docker containers running: `docker-compose ps`
- [ ] App logs show "Started JournalApplication"
- [ ] Health check returns UP: `curl http://localhost:8080/journal/actuator/health`
- [ ] Can create user via API
- [ ] MongoDB contains the new user: `mongo` → `db.user.findOne()`
- [ ] Redis has cached keys: `redis-cli` → `KEYS *`
- [ ] Kafka topics exist: `docker exec journal-kafka kafka-topics --list`
- [ ] All tests pass: `mvn test`

---

## Next Steps

After verifying services work:
1. Review API documentation: http://localhost:8080/journal/swagger-ui.html
2. Check health metrics: http://localhost:8080/journal/actuator
3. Run end-to-end tests
4. Deploy to production following [DEPLOYMENT.md](../DEPLOYMENT.md)

---

**For more information**, see:
- [BUILD_AND_DEPLOY.md](../BUILD_AND_DEPLOY.md) - Build and deployment guide
- [README.md](../README.md) - Project overview
- [DEPLOYMENT.md](../DEPLOYMENT.md) - Production deployment
