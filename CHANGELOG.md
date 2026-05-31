# Changelog

All notable changes to the Journal App project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-05-30

### Added
- **Production-Ready Release**: Marked as stable version 1.0.0
- **Docker Support**: 
  - Multi-stage Dockerfile for optimized production images
  - docker-compose.yml for production deployment
  - docker-compose.dev.yml for development environment
  - .dockerignore for efficient builds
- **Environment Configuration**:
  - .env.example template for all required environment variables
  - application-prod.yml for production-specific Spring configuration
  - Environment variable externalization for all sensitive data
- **Deployment Documentation**:
  - DEPLOYMENT.md with comprehensive deployment guide
  - Security checklist for production deployment
  - Database backup and recovery procedures
  - Troubleshooting guide
- **Security Improvements**:
  - Non-root user execution in Docker
  - Kafka credentials externalized
  - Redis password support
  - Environment-based configuration for all secrets
- **Monitoring & Observability**:
  - Health checks in Docker containers
  - Prometheus metrics support in production profile
  - Structured logging configuration
- **Branding Update**: 
  - Updated maintainer from "Engineering Digest" to "Rickarya Das"
  - Updated all references and metadata

### Changed
- Version: 0.0.1-SNAPSHOT → 1.0.0
- Redis port: Externalized from hardcoded 15641 to configurable ${REDIS_PORT:6379}
- Kafka JAAS configuration: Credentials now externalized to environment variables
- .gitignore: Added .env and environment files to ignore list
- Spring Security: Stateless session management (unchanged but validated)

### Fixed
- Hardcoded Kafka credentials (username='X', password='X')
- Missing Redis port configuration
- Missing environment variable documentation
- Lack of production deployment guidance

### Security
- All API keys and credentials now require environment variables
- Docker image runs as non-root user (appuser)
- Added security section to DEPLOYMENT.md
- Configured production logging levels to prevent information leakage

### Infrastructure
- Redis persistence enabled for production
- MongoDB auto-index creation in production
- Kafka SASL/SSL security enabled
- Health checks configured for all services
- Restart policies configured for container resilience

### Documentation
- Comprehensive DEPLOYMENT.md guide
- Enhanced .env.example with descriptions
- Added CHANGELOG.md for version tracking

## [0.0.1] - Initial Release

Initial development release with core features:
- End-to-End Encryption (E2EE)
- User Authentication with JWT and OAuth2
- Sentiment Analysis
- Weather Integration
- Redis Caching
- Email Notifications
- Admin Dashboard
- API Documentation (Swagger)
- Kafka Integration
- MongoDB storage

---

## Migration Guide

### Upgrading from 0.0.1 to 1.0.0

1. **Environment Setup**: Create `.env` file from `.env.example`
2. **Docker Build**: Build production image with `docker build -t journal-app:1.0.0 .`
3. **Deployment**: Use `docker-compose up -d` for orchestrated deployment
4. **Configuration**: Review and update environment variables in `.env`
5. **Data Migration**: No database schema changes - backward compatible

No breaking API changes. All existing deployments compatible with minimal configuration updates.
