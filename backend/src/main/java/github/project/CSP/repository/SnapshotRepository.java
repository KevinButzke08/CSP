package github.project.CSP.repository;

import github.project.CSP.inventory.PortfolioSnapshots;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<PortfolioSnapshots, Long> {
}
