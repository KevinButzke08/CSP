package csp.service;


import csp.inventory.Portfolio;
import csp.inventory.PortfolioSnapshot;
import csp.repository.SnapshotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup() {
        Portfolio mock = new Portfolio();
        mock.setCurrentValue(BigDecimal.TWO);
        mock.setTotalChangePercentage(BigDecimal.valueOf(50));
        mock.setTotalPurchasePrice(BigDecimal.ONE);

        when(portfolioService.getPortfolio()).thenReturn(mock);
    }

    @AfterEach
    void tearDown() {
        snapshotRepository.deleteAll();
    }

    @Test
    void testTakeNewSnapshot() {
        // Arrange
        snapshotService.takeNewSnapshot();
        LocalDateTime after = LocalDateTime.now();

        // Act
        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();

        // Assert
        assertEquals(1, snapshotList.size());
        PortfolioSnapshot snapshot = snapshotList.getFirst();
        assertEquals(BigDecimal.TWO, snapshot.getCurrentValue());
        assertEquals(BigDecimal.valueOf(50), snapshot.getTotalChangePercentage());
        assertEquals(BigDecimal.ONE, snapshot.getTotalPurchasePrice());
        assertTrue(snapshot.getTimestamp().isBefore(after));
    }

    @Test
    void testGetAllSnapshotsOrderedByTimeStamp() {
        // Arrange
        snapshotService.takeNewSnapshot();
        snapshotService.takeNewSnapshot();

        // Act
        List<PortfolioSnapshot> snapshotList = snapshotService.getAllSnapshots();

        // Assert
        assertEquals(2, snapshotList.size());
        assertTrue(snapshotList.get(0).getTimestamp().isAfter(snapshotList.get(1).getTimestamp()));
    }

    @Test
    void testGetAllSnapshotsEmptyRepository() {
        // Act
        List<PortfolioSnapshot> snapshotList = snapshotService.getAllSnapshots();

        // Assert
        assertTrue(snapshotList.isEmpty());
    }

    @Test
    void testGetValueHistory() {
        // Arrange
        snapshotService.takeNewSnapshot();

        Portfolio mock = new Portfolio();
        mock.setCurrentValue(BigDecimal.TEN);
        mock.setTotalChangePercentage(BigDecimal.valueOf(50));
        mock.setTotalPurchasePrice(BigDecimal.valueOf(9));

        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();

        mock.setCurrentValue(BigDecimal.valueOf(3));
        mock.setTotalChangePercentage(BigDecimal.valueOf(20));
        mock.setTotalPurchasePrice(BigDecimal.ONE);
        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();
        // Act
        List<BigDecimal> result = snapshotService.getValueHistory();

        // Assert
        assertEquals(3, result.size());
        assertEquals(BigDecimal.valueOf(3), result.get(0));
        assertEquals(BigDecimal.valueOf(10), result.get(1));
        assertEquals(BigDecimal.valueOf(2), result.get(2));
    }

    @Test
    void testGetChangePercentageHistory() {
        // Arrange
        snapshotService.takeNewSnapshot();

        Portfolio mock = new Portfolio();
        mock.setCurrentValue(BigDecimal.TEN);
        mock.setTotalChangePercentage(BigDecimal.valueOf(500));
        mock.setTotalPurchasePrice(BigDecimal.valueOf(9));

        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();

        mock.setCurrentValue(BigDecimal.valueOf(3));
        mock.setTotalChangePercentage(BigDecimal.valueOf(20));
        mock.setTotalPurchasePrice(BigDecimal.ONE);
        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();
        // Act
        List<BigDecimal> result = snapshotService.getChangePercentageHistory();

        // Assert
        assertEquals(3, result.size());
        assertEquals(BigDecimal.valueOf(20), result.get(0));
        assertEquals(BigDecimal.valueOf(500), result.get(1));
        assertEquals(BigDecimal.valueOf(50), result.get(2));
    }

    @Test
    void testGetPurchasePriceHistory() {
        // Arrange
        snapshotService.takeNewSnapshot();

        Portfolio mock = new Portfolio();
        mock.setCurrentValue(BigDecimal.TEN);
        mock.setTotalChangePercentage(BigDecimal.valueOf(500));
        mock.setTotalPurchasePrice(BigDecimal.valueOf(9));

        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();

        mock.setCurrentValue(BigDecimal.valueOf(3));
        mock.setTotalChangePercentage(BigDecimal.valueOf(175));
        mock.setTotalPurchasePrice(BigDecimal.valueOf(29));
        when(portfolioService.getPortfolio()).thenReturn(mock);

        snapshotService.takeNewSnapshot();
        // Act
        List<BigDecimal> result = snapshotService.getPurchasePriceHistory();

        // Assert
        assertEquals(3, result.size());
        assertEquals(BigDecimal.valueOf(29), result.get(0));
        assertEquals(BigDecimal.valueOf(9), result.get(1));
        assertEquals(BigDecimal.valueOf(1), result.get(2));
    }

}
