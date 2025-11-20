package csp.service;

import csp.inventory.Portfolio;
import csp.inventory.PortfolioSnapshot;
import csp.repository.SnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SnapshotService {
    private final SnapshotRepository snapshotRepository;
    private final PortfolioService portfolioService;

    @Autowired
    public SnapshotService(SnapshotRepository snapshotRepository, PortfolioService portfolioService) {
        this.snapshotRepository = snapshotRepository;
        this.portfolioService = portfolioService;
    }

    public void takeNewSnapshot() {
        Portfolio currentPortfolio = portfolioService.getPortfolio();

        if (currentPortfolio == null) {
            log.warn("No portfolio found, no snapshot taken!");
            return;
        }

        PortfolioSnapshot portfolioSnapshot = new PortfolioSnapshot();
        portfolioSnapshot.setTimestamp(LocalDateTime.now());
        portfolioSnapshot.setCurrentValue(currentPortfolio.getCurrentValue());
        portfolioSnapshot.setTotalPurchasePrice(currentPortfolio.getTotalPurchasePrice());
        portfolioSnapshot.setChangePercentage(currentPortfolio.getChangePercentage());

        snapshotRepository.save(portfolioSnapshot);
    }

    public List<PortfolioSnapshot> getAllSnapshots() {
        return snapshotRepository.findAllByOrderByTimestampDesc();
    }

}
