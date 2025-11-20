package csp.repository;

import csp.inventory.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    List<PortfolioSnapshot> findAllByOrderByTimestampDesc();
}
