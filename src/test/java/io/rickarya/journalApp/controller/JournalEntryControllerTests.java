package io.rickarya.journalApp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests for Journal Entry endpoints.
 * 
 * NOTE: This test requires MongoDB, Redis, and Kafka to be running locally.
 * Run: docker-compose up
 * 
 * Or run: mvn test (with docker-compose already running)
 * 
 * Tests are SKIPPED in GitHub Actions CI/CD because external services are not available.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/journaldb-test",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class JournalEntryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthCheckEndpoint() throws Exception {
        // Test the health check endpoint
        mockMvc.perform(get("/journal/health/status"))
                .andExpect(status().isOk());
    }

}
