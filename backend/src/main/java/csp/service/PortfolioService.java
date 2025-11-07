package csp.service;

import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

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
        portfolio.setTotalPurchasePrice(portfolio.getTotalPurchasePrice() + item.getPurchasePrice() * item.getQuantity());

        // Calculate the new changes to currentValue and totalPrice etc.
        updatePortfolio();
    }

    public void updatePortfolio() {
        List<Item> updatedItemList = steamMarketService.updateItemPrices(portfolio.getItemList());
        portfolio.setItemList(updatedItemList);

        float currentValue = (float) updatedItemList.stream().mapToDouble(item -> item.getCurrentPrice() * item.getQuantity()).sum();
        portfolio.setCurrentValue(currentValue);

        float changePercentage = ((portfolio.getCurrentValue() - portfolio.getTotalPurchasePrice()) / portfolio.getTotalPurchasePrice()) * 100f;
        portfolio.setChangePercentage(changePercentage);

        portfolioRepository.save(portfolio);
    }

    public void deleteItemFromPortfolio(String name) {
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        boolean removed = mutablePortfolioList.removeIf(item -> item.getName().equals(name));
        if (!removed) {
            throw new IllegalArgumentException("No Item with the name" + name + "found!");
        }
        portfolio.setItemList(mutablePortfolioList);
        updatePortfolio();
    }
}
