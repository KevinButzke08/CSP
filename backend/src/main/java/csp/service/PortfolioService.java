package csp.service;

import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        mutablePortfolioList.add(item);
        portfolio.setItemList(mutablePortfolioList);
        // Calculate the new changes to currentValue and totalPrice etc.
        updatePortfolio();
    }

    public void updatePortfolio() {
        List<Item> updatedItemList = steamMarketService.updateItemPrices(portfolio.getItemList());
        portfolio.setItemList(new ArrayList<>(updatedItemList));

        BigDecimal currentValue = updatedItemList.stream().map(item -> item.getCurrentPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        portfolio.setCurrentValue(currentValue);

        BigDecimal totalPurchasePrice = updatedItemList.stream().map(item -> item.getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        portfolio.setTotalPurchasePrice(totalPurchasePrice);
        // If total purchase price is 0, we need to prevent this because of division through 0
        if (portfolio.getTotalPurchasePrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal changePercentage = portfolio.getCurrentValue().subtract(portfolio.getTotalPurchasePrice()).divide(portfolio.getTotalPurchasePrice(), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            portfolio.setChangePercentage(changePercentage);
        } else {
            portfolio.setChangePercentage(BigDecimal.ZERO);
        }
        portfolioRepository.save(portfolio);
    }

    public void deleteItemFromPortfolio(Long id) {
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        System.out.println(mutablePortfolioList.getFirst());
        boolean removed = mutablePortfolioList.removeIf(item -> item.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("No Item with the id" + id + "found!");
        }
        portfolio.setItemList(mutablePortfolioList);
        updatePortfolio();
    }
}
