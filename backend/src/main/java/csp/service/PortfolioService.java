package csp.service;


import csp.controller.ItemDTO;
import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SteamMarketService steamMarketService;
    private final ItemMapper itemMapper;
    @Getter
    private Portfolio portfolio;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, SteamMarketService steamMarketService, ItemMapper itemMapper) {
        this.portfolioRepository = portfolioRepository;
        this.steamMarketService = steamMarketService;
        this.itemMapper = itemMapper;
    }

    @PostConstruct
    private void init() {
        // Load the last saved portfolio or create one if none exists
        if (portfolioRepository.count() == 0) {
            portfolio = portfolioRepository.save(new Portfolio());
        } else portfolio = portfolioRepository.findAll().getFirst();
        System.out.println("Loaded portfolio with ID: " + portfolio.getId());
    }

    public void addItemToPortfolio(ItemDTO itemDTO) {
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        Item item = itemMapper.itemDTOtoItem(itemDTO);
        mutablePortfolioList.add(item);
        portfolio.setItemList(mutablePortfolioList);
        // Calculate the new changes to currentValue and totalPrice etc.
        updatePortfolio();
    }

    public void updatePortfolio() {
        List<Item> updatedItemList = steamMarketService.updateItemPrices(portfolio.getItemList());
        portfolio.setItemList(updatedItemList);

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
        portfolio = portfolioRepository.save(portfolio);
    }

    public void deleteItemFromPortfolio(Long id) {
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        
        boolean removed = mutablePortfolioList.removeIf(item -> item.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("No Item with the id" + id + "found!");
        }
        portfolio.setItemList(mutablePortfolioList);
        updatePortfolio();
    }
}
