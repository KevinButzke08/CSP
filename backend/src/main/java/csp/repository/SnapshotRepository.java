package csp.repository;

import csp.inventory.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    // Newest snapshot first
    List<PortfolioSnapshot> findAllByOrderByTimestampDesc();
}
