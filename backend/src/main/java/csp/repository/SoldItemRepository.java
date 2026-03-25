package csp.repository;

import csp.inventory.SoldItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {
    @Query("SELECT SUM(s.sellPrice * s.quantity) FROM SoldItem s")
    Optional<BigDecimal> getTotalSellPrice();

    @Query("""
                SELECT SUM((s.sellPrice - s.purchasePrice) * s.quantity)
                FROM SoldItem s
            """)
    Optional<BigDecimal> getTotalRealizedValue();
}
