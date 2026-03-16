package csp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NameRepositoryTest {
    @Autowired
    private NameRepository nameRepository;

    @Test
    void testContains() {
        assertTrue(nameRepository.contains("UMP-45 | Momentum (Well-Worn)"));
        assertTrue(nameRepository.contains("USP-S | Para Green (Minimal Wear)"));
        assertTrue(nameRepository.contains("Sticker | kyxsan (Foil) | Austin 2025"));
        assertFalse(nameRepository.contains("UMP-45 | Momentum"));
        assertFalse(nameRepository.contains(""));
    }
}
