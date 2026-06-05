# Journal App

[![Version](https://img.shields.io/badge/VERSION-1.0.0-brightgreen?style=for-the-badge&logo=github)](CHANGELOG.md)
[![License](https://img.shields.io/badge/LICENSE-MIT-blue?style=for-the-badge&logo=opensourceinitiative)](LICENSE)
[![Java](https://img.shields.io/badge/JAVA-17-red?style=for-the-badge&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/SPRING%20BOOT-2.7.16-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Status](https://img.shields.io/badge/STATUS-PRODUCTION%20READY-brightgreen?style=for-the-badge&logo=checkmarx)](DEPLOYMENT.md)
[![Docker](https://img.shields.io/badge/DOCKER-SUPPORTED-blue?style=for-the-badge&logo=docker)](Dockerfile)
[![Maven](https://img.shields.io/badge/BUILD-MAVEN-C71A36?style=for-the-badge&logo=apachemaven)](https://maven.apache.org)

A secure, end-to-end encrypted (E2EE) journal application built with Spring Boot. This application provides a modern REST API for managing personal journal entries with sentiment analysis, weather integration, and advanced security features.

**Maintained by**: [Rickarya Das](https://github.com/ridash2005)

## Features

- **End-to-End Encryption (E2EE)**: Your journal entries are encrypted for maximum privacy
- **User Authentication**: Secure authentication with JWT tokens and OAuth2 (Google)
- **Sentiment Analysis**: Automatic sentiment detection for your journal entries
- **Weather Integration**: Correlate entries with real-time weather data
- **Caching**: Redis-powered caching for improved performance
- **Email Notifications**: Scheduled email reminders and notifications
- **Admin Dashboard**: Administrative controls for system management
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Kafka Integration**: Event-driven architecture for processing
- **MongoDB**: Flexible document storage for entries

## Tech Stack

- **Framework**: Spring Boot 2.7.16
- **Language**: Java 17
- **Database**: MongoDB
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Authentication**: JWT, OAuth2
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 17+
- Maven 3.8+
- Docker and Docker Compose (for containerized setup)
- MongoDB (or use Docker)
- Redis (or use Docker)
- Kafka (or use Docker)

## Installation & Setup

### Using Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/ridash2005/SpringBoot-Journal-App.git
cd journalApp
```

2. Copy the environment template:
```bash
cp .env.example .env
```

3. Update `.env` with your configuration:
```env
MONGODB_URI=mongodb://mongo:27017
REDIS_HOST=redis
REDIS_PASSWORD=your_redis_password
KAFKA_SERVERS=kafka:9092
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
JAVA_EMAIL=your_email@gmail.com
JAVA_EMAIL_PASSWORD=your_app_password
WEATHER_API_KEY=your_weather_api_key
SERVER_PORT=8080
```

4. Start the application:
```bash
docker-compose up -d
```

The application will be available at: `http://localhost:8080/journal`

### Local Development Setup

1. Clone the repository:
```bash
git clone https://github.com/ridash2005/SpringBoot-Journal-App.git
cd journalApp
```

2. Copy the environment template:
```bash
cp .env.example .env
```

3. Update `.env` with your local configuration

4. Start required services (using Docker):
```bash
docker-compose -f docker-compose.dev.yml up -d
```

5. Build the application:
```bash
mvn clean package -DskipTests
```

6. Run the application:
```bash
mvn spring-boot:run
```

## Configuration

### Environment Variables

All configuration is managed through environment variables. See `.env.example` for all available options.

Key configurations:
- `MONGODB_URI`: MongoDB connection string
- `REDIS_HOST`: Redis server host
- `KAFKA_SERVERS`: Kafka bootstrap servers
- `GOOGLE_CLIENT_ID` & `GOOGLE_CLIENT_SECRET`: Google OAuth2 credentials
- `JAVA_EMAIL` & `JAVA_EMAIL_PASSWORD`: Gmail credentials for sending emails
- `WEATHER_API_KEY`: API key for weather service
- `SERVER_PORT`: Application server port (default: 8080)

## API Documentation

Once the application is running, access the interactive API documentation at:

```
http://localhost:8080/journal/swagger-ui.html
```

Or view the OpenAPI specification at:

```
http://localhost:8080/journal/v3/api-docs
```

## Project Structure

```
src/
├── main/
│   ├── java/net/rickarya/journalApp/
│   │   ├── controller/          # REST API Controllers
│   │   ├── service/             # Business logic services
│   │   ├── entity/              # JPA entities
│   │   ├── repository/          # Data access layer
│   │   ├── config/              # Spring configuration
│   │   ├── filter/              # Security filters
│   │   ├── dto/                 # Data transfer objects
│   │   ├── utils/               # Utility classes
│   │   ├── cache/               # Caching logic
│   │   ├── scheduler/           # Scheduled tasks
│   │   ├── constants/           # Application constants
│   │   ├── enums/               # Enumeration types
│   │   ├── model/               # Domain models
│   │   └── api/response/        # API response wrappers
│   └── resources/
│       ├── application.yml      # Spring configuration
│       └── logback.xml          # Logging configuration
└── test/
    └── java/                    # Unit and integration tests
```

## Building & Testing

### Build the application:
```bash
mvn clean package
```

### Run tests:
```bash
mvn test
```

### Run specific test class:
```bash
mvn test -Dtest=UserServiceTest
```

### Generate code coverage report:
```bash
mvn clean test jacoco:report
```

## API Endpoints

### Public Endpoints
- `POST /journal/public/signup` - User registration
- `POST /journal/public/login` - User login
- `GET /journal/public/health` - Health check

### User Endpoints (Authenticated)
- `GET /journal/user/all` - Get all journal entries
- `POST /journal/user/add` - Create new journal entry
- `GET /journal/user/{id}` - Get journal entry by ID
- `PUT /journal/user/{id}` - Update journal entry
- `DELETE /journal/user/{id}` - Delete journal entry

### Admin Endpoints (Admin role required)
- `GET /journal/admin/all-users` - Get all users
- `DELETE /journal/admin/user/{id}` - Delete user
- `GET /journal/admin/stats` - System statistics

For complete API documentation, see Swagger UI at `/journal/swagger-ui.html`

## Security Considerations

- **JWT Tokens**: All user requests require valid JWT tokens
- **Password Encryption**: Passwords are hashed using BCrypt
- **CORS**: Configured to allow trusted origins
- **CSRF Protection**: Disabled for stateless API but ensure HTTPS in production
- **Sensitive Data**: Never commit `.env` files or secrets to version control

## Development Guidelines

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods small and focused (max 30 lines)

### Commit Messages
- Use conventional commits: `feat:`, `fix:`, `docs:`, `style:`, `refactor:`, `test:`, `chore:`
- Example: `feat: add sentiment analysis for journal entries`

### Pull Requests
- Ensure all tests pass before submitting PR
- Update documentation if needed
- Keep PRs focused on a single feature/fix

## Troubleshooting

### Port already in use
If port 8080 is already in use, change it in `.env`:
```env
SERVER_PORT=8081
```

### MongoDB connection issues
Verify MongoDB URI in `.env` and that MongoDB service is running:
```bash
docker-compose logs mongo
```

### Kafka connection issues
Ensure Kafka and Zookeeper are running:
```bash
docker-compose logs kafka
```

### Test failures
Run tests with verbose output:
```bash
mvn test -X
```

## Documentation

Comprehensive guides for building, testing, and deploying the application:

- **[BUILD_AND_DEPLOY.md](BUILD_AND_DEPLOY.md)** - Complete build and deployment guide
  - Local build and testing procedures
  - Docker build and run instructions
  - GitHub Actions CI/CD pipeline
  - Environment variable setup
  
- **[TESTING_WITH_SERVICES.md](TESTING_WITH_SERVICES.md)** - Service integration testing guide ⭐ **START HERE**
  - How to verify MongoDB, Redis, and Kafka are working
  - Step-by-step service verification
  - Health check endpoints
  - Docker management commands
  - Troubleshooting common issues

- **[PRODUCTION_DEPLOYMENT_FIX.md](PRODUCTION_DEPLOYMENT_FIX.md)** - Production deployment guide
  - MongoDB connection issue resolution
  - Production profile configuration
  - Environment variable setup for Docker
  - Monitoring and health checks
  
- **[MONGODB_SESSION_ERROR_FIX.md](MONGODB_SESSION_ERROR_FIX.md)** - CI/CD error resolution
  - Explains the MongoDB session error fix
  - CI profile configuration
  
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Production deployment guide
- **[GITHUB_ACTIONS_FIX.md](GITHUB_ACTIONS_FIX.md)** - CI/CD pipeline fixes
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and changes

### Quick Start with Services
```bash
# 1. Start all services (MongoDB, Redis, Kafka)
docker-compose up -d

# 2. Build the application
mvn clean package -DskipTests

# 3. Run locally
mvn spring-boot:run

# 4. Verify everything is working
curl http://localhost:8080/journal/actuator/health

# 5. Run integration tests
mvn test
```

For detailed verification steps, see **[TESTING_WITH_SERVICES.md](TESTING_WITH_SERVICES.md)**.

## Performance & Monitoring

- **Request Logging**: All requests are logged in `logs/` directory
- **Metrics**: Use Spring Boot Actuator endpoints for monitoring
- **Caching**: Redis caching reduces database load
- **Rate Limiting**: Configure in application.yml for production

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## Support

For issues and questions:
- 📧 Email: rickaryadas@gmail.com
- 🐛 Report bugs: [GitHub Issues](https://github.com/ridash2005/SpringBoot-Journal-App/issues)
- 💡 Suggest features: [GitHub Discussions](https://github.com/ridash2005/SpringBoot-Journal-App/discussions)

## Roadmap

- [ ] Mobile application (iOS/Android)
- [ ] Advanced analytics and insights
- [ ] AI-powered writing suggestions
- [ ] Multi-language support
- [ ] Collaborative journal entries
- [ ] Export to PDF/Word

## Authors

- **Rickarya Das** - [GitHub](https://github.com/ridash2005)

## Acknowledgments

- Spring Boot community
- MongoDB and Redis documentation
- OpenAPI/Swagger specification
