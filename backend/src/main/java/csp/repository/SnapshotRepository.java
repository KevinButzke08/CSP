package csp.repository;

import csp.inventory.PortfolioSnapshots;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<PortfolioSnapshots, Long> {
}
