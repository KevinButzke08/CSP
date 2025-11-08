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
        // Calculate the new changes to currentValue and totalPrice etc.
        updatePortfolio();
    }

    public void updatePortfolio() {
        List<Item> updatedItemList = steamMarketService.updateItemPrices(portfolio.getItemList());
        portfolio.setItemList(updatedItemList);

        float currentValue = (float) updatedItemList.stream().mapToDouble(item -> item.getCurrentPrice() * item.getQuantity()).sum();
        portfolio.setCurrentValue(currentValue);

        float totalPurchasePrice = (float) updatedItemList.stream().mapToDouble(item -> item.getPurchasePrice() * item.getQuantity()).sum();
        portfolio.setTotalPurchasePrice(totalPurchasePrice);
        // If total purchase price is 0, we need to prevent this because of division through 0
        if (portfolio.getTotalPurchasePrice() > 0f) {
            float changePercentage = ((portfolio.getCurrentValue() - portfolio.getTotalPurchasePrice()) / portfolio.getTotalPurchasePrice()) * 100f;
            portfolio.setChangePercentage(changePercentage);
        } else {
            portfolio.setChangePercentage(0f);
        }
        portfolioRepository.save(portfolio);
    }

    //TODO: Taking the name, will delete every item with the same market hash name as well, needs to be set to id probably
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
