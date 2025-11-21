package csp.service;


import csp.inventory.Portfolio;
import csp.inventory.PortfolioSnapshot;
import csp.repository.SnapshotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class SnapshotServiceTest {
    @Autowired
    private SnapshotRepository snapshotRepository;
    @Autowired
    private SnapshotService snapshotService;
    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    void testTakeNewSnapshot() {
        Portfolio mock = new Portfolio();
        mock.setCurrentValue(BigDecimal.TWO);
        mock.setChangePercentage(BigDecimal.valueOf(50));
        mock.setTotalPurchasePrice(BigDecimal.ONE);

        when(portfolioService.getPortfolio()).thenReturn(mock);
        LocalDateTime before = LocalDateTime.now();
        snapshotService.takeNewSnapshot();
        LocalDateTime after = LocalDateTime.now();

        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();
        assertEquals(1, snapshotList.size());
        
        PortfolioSnapshot snapshot = snapshotList.getFirst();
        assertEquals(BigDecimal.TWO, snapshot.getCurrentValue());
        assertEquals(BigDecimal.valueOf(50), snapshot.getChangePercentage());
        assertEquals(BigDecimal.ONE, snapshot.getTotalPurchasePrice());
        assertTrue(!snapshot.getTimestamp().isBefore(before) && !snapshot.getTimestamp().isAfter(after));

    }

}
