package io.rickarya.journalApp.service;

import io.rickarya.journalApp.entity.JournalEntry;
import io.rickarya.journalApp.repository.JournalEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class JournalEntryServiceTests {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testContextLoads() {
        // Verify test framework is working
        assertNotNull(journalEntryRepository);
    }

}
