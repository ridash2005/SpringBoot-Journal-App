# Deployment Guide - Journal App v1.0.0

## Overview
This guide covers deploying the Journal App to production environments. The application is containerized and uses Docker Compose for orchestration.

## Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 4GB RAM (minimum)
- 10GB storage

## Production Deployment

### 1. Clone and Prepare
```bash
git clone https://github.com/ridash2005/SpringBoot-Journal-App.git
cd journalApp
cp .env.example .env
```

### 2. Configure Environment Variables
Edit `.env` with your production values:
```env
# Critical Security Settings
MONGODB_URI=mongodb://user:password@mongo-host:27017/journaldb
REDIS_PASSWORD=strong_redis_password_32_chars_or_more
KAFKA_USERNAME=kafka_user
KAFKA_PASSWORD=strong_kafka_password

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your_production_google_client_id
GOOGLE_CLIENT_SECRET=your_production_google_secret

# Email Service (Gmail with App Password)
JAVA_EMAIL=noreply@yourdomain.com
JAVA_EMAIL_PASSWORD=your_app_password

# Weather API
WEATHER_API_KEY=your_api_key

# Application Settings
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
```

### 3. Build Docker Image
```bash
docker build -t journal-app:1.0.0 .
```

### 4. Deploy with Docker Compose
```bash
docker-compose up -d
```

Monitor the startup:
```bash
docker-compose logs -f app
```

### 5. Verify Deployment
```bash
# Check all services are running
docker-compose ps

# Access API Documentation
curl http://localhost:8080/journal/swagger-ui.html

# Health check
curl http://localhost:8080/journal/health
```

## Environment-Specific Configurations

### Development
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### Staging/Production
```bash
docker-compose up -d
```

## Database Management

### MongoDB Backup
```bash
docker-compose exec mongo mongodump --out /data/backup
docker cp journal-mongo:/data/backup ./backup
```

### MongoDB Restore
```bash
docker cp ./backup journal-mongo:/data/
docker-compose exec mongo mongorestore /data/backup
```

### Redis Persistence
Redis data persists in Docker volumes. Ensure volume backups are included in your backup strategy.

## Security Checklist
- [ ] All environment variables configured with strong passwords
- [ ] HTTPS/TLS configured for external access
- [ ] Database credentials stored in secure vault
- [ ] API keys rotated regularly
- [ ] CORS configuration reviewed and restricted
- [ ] JWT secret keys configured
- [ ] Firewall rules restrict access to Kafka and Redis ports
- [ ] Regular security scanning of Docker images
- [ ] Log monitoring and alerting configured

## Monitoring

### View Logs
```bash
# Application logs
docker-compose logs -f app

# All services
docker-compose logs -f
```

### Resource Usage
```bash
docker stats
```

## Scaling

### Horizontal Scaling
The application is stateless and can be scaled horizontally:

```bash
# Scale to 3 replicas
docker-compose up -d --scale app=3
```

Use a load balancer (nginx, HAProxy) for traffic distribution.

## Troubleshooting

### Cannot connect to MongoDB
```bash
docker-compose exec mongo mongosh --eval "db.adminCommand('ping')"
```

### Redis connection issues
```bash
docker-compose exec redis redis-cli ping
```

### Kafka connection issues
```bash
docker-compose exec kafka kafka-broker-api-versions.sh --bootstrap-server kafka:9092
```

## Upgrade Process

1. Pull latest changes
```bash
git pull origin main
```

2. Update version in pom.xml and rebuild
```bash
mvn clean package -DskipTests
docker build -t journal-app:1.1.0 .
```

3. Update docker-compose.yml with new image tag
4. Perform rolling update
```bash
docker-compose up -d
```

## Backup and Recovery

### Automated Backup Strategy
- Daily MongoDB snapshots to external storage
- Redis snapshots stored with versioning
- Application logs aggregated to centralized logging

### Recovery Procedures
- RTO: Recovery Time Objective - 4 hours
- RPO: Recovery Point Objective - 1 hour

## Support
For deployment issues or questions, contact rickaryadas@gmail.com or open an issue on GitHub.

## License
This application is licensed under the MIT License. See LICENSE file for details.
