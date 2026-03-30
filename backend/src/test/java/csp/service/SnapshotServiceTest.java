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
        // Because of the LocalDateTime.now used in the SnapshotService, during testing this can lead to the same values present there
        // So we correct this maybe identical value by setting it our self again
        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.of(2026, 3, 17, 10, 0, 0);
        for (int i = 0; i < snapshotList.size(); i++) {
            snapshotList.get(i).setTimestamp(baseTime.plusSeconds(i));
        }
        snapshotRepository.saveAll(snapshotList);

        // Act
        List<PortfolioSnapshot> resultList = snapshotService.getAllSnapshots();

        // Assert
        assertEquals(2, resultList.size());
        assertTrue(resultList.get(0).getTimestamp().isAfter(resultList.get(1).getTimestamp()));
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

        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(10), BigDecimal.valueOf(50), BigDecimal.valueOf(9));
        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(3), BigDecimal.valueOf(20), BigDecimal.valueOf(1));
        // Because of the LocalDateTime.now used in the SnapshotService, during testing this can lead to the same values present there
        // So we correct this maybe identical value by setting it our self again
        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.of(2026, 3, 17, 10, 0, 0);
        for (int i = 0; i < snapshotList.size(); i++) {
            snapshotList.get(i).setTimestamp(baseTime.plusSeconds(i));
        }
        snapshotRepository.saveAll(snapshotList);
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

        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(10), BigDecimal.valueOf(500), BigDecimal.valueOf(9));
        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(3), BigDecimal.valueOf(20), BigDecimal.valueOf(1));
        // Because of the LocalDateTime.now used in the SnapshotService, during testing this can lead to the same values present there
        // So we correct this maybe identical value by setting it our self again
        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.of(2026, 3, 17, 10, 0, 0);
        for (int i = 0; i < snapshotList.size(); i++) {
            snapshotList.get(i).setTimestamp(baseTime.plusSeconds(i));
        }
        snapshotRepository.saveAll(snapshotList);
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

        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(10), BigDecimal.valueOf(500), BigDecimal.valueOf(9));
        mockPortfolioAndTakeSnapshot(BigDecimal.valueOf(3), BigDecimal.valueOf(175), BigDecimal.valueOf(29));
        // Because of the LocalDateTime.now used in the SnapshotService, during testing this can lead to the same values present there
        // So we correct this maybe identical value by setting it our self again
        List<PortfolioSnapshot> snapshotList = snapshotRepository.findAll();
        LocalDateTime baseTime = LocalDateTime.of(2026, 3, 17, 10, 0, 0);
        for (int i = 0; i < snapshotList.size(); i++) {
            snapshotList.get(i).setTimestamp(baseTime.plusSeconds(i));
        }
        snapshotRepository.saveAll(snapshotList);
        // Act
        List<BigDecimal> result = snapshotService.getPurchasePriceHistory();

        // Assert
        assertEquals(3, result.size());
        assertEquals(BigDecimal.valueOf(29), result.get(0));
        assertEquals(BigDecimal.valueOf(9), result.get(1));
        assertEquals(BigDecimal.valueOf(1), result.get(2));
    }

    private void mockPortfolioAndTakeSnapshot(BigDecimal current, BigDecimal change, BigDecimal purchase) {
        Portfolio mock = new Portfolio();
        mock.setCurrentValue(current);
        mock.setTotalChangePercentage(change);
        mock.setTotalPurchasePrice(purchase);
        when(portfolioService.getPortfolio()).thenReturn(mock);
        snapshotService.takeNewSnapshot();


    }

}
