package csp.repository;

import csp.inventory.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface SnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    // Newest snapshot first
    List<PortfolioSnapshot> findAllByOrderByTimestampDesc();

    @Query("SELECT p.currentValue FROM PortfolioSnapshot p ORDER BY p.timestamp DESC")
    List<BigDecimal> findAllCurrentValuesOrderByTimestampDesc();

    @Query("SELECT p.totalChangePercentage FROM PortfolioSnapshot p ORDER BY p.timestamp DESC")
    List<BigDecimal> findAllChangePercentagesOrderByTimestampDesc();

    @Query("SELECT p.totalPurchasePrice FROM PortfolioSnapshot p ORDER BY p.timestamp DESC")
    List<BigDecimal> findAllTotalPurchasePriceOrderByTimestampDesc();
}
