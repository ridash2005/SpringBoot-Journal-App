package io.rickarya.journalApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for Spring Boot application startup.
 * 
 * NOTE: This test requires MongoDB, Redis, and Kafka to be running locally.
 * Run: docker-compose up
 * 
 * Or run: mvn test (with docker-compose already running)
 * 
 * Tests are SKIPPED in GitHub Actions CI/CD because external services are not available.
 * For CI/CD, use -DskipTests flag.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/journaldb-test",
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "spring.kafka.bootstrap-servers=localhost:9092"
})
class JournalApplicationTests {

    @Test
    void contextLoads() {
        // Verify that Spring context can be loaded
        // This is a smoke test to ensure main components are available
    }

}
