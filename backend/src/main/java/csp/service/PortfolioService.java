package csp.service;

import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SteamMarketService steamMarketService;
    @Getter
    private Portfolio portfolio;

    public PortfolioService(PortfolioRepository portfolioRepository, SteamMarketService steamMarketService) {
        this.portfolioRepository = portfolioRepository;
        this.steamMarketService = steamMarketService;
    }
    @PostConstruct
    private void init() {
        // Load the last saved portfolio or create one if none exists
        if (portfolioRepository.count() == 0) {
            portfolio = portfolioRepository.save(new Portfolio());
        } else portfolio = portfolioRepository.findAll().getFirst();
        System.out.println("Loaded portfolio with ID: " + portfolio.getId());
    }
    public void addItemToPortfolio(Item item) {
        portfolio.getItemList().add(item);
        // Calculate the new changes to currentValue and totalPrice etc.
        updatePortfolio(portfolio.getItemList());
    }
    public void updatePortfolio(List<Item> items) {
        //Update all the values (PricePercentage, CurrentValue of Portfolio and Items! and save to repository)
        portfolio.setItemList(items);
        portfolio = steamMarketService.getPortfolioPriceUpdate(portfolio);
        portfolioRepository.save(portfolio);
    }
}
