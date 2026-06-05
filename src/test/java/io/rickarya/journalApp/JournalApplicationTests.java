package io.rickarya.journalApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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
