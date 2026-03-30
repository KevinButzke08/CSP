package csp.service;


import csp.controller.ItemDTO;
import csp.exceptions.ItemNotFoundException;
import csp.exceptions.ItemNotFoundOnMarketException;
import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.NameRepository;
import csp.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final NameRepository nameRepository;
    private final SteamMarketService steamMarketService;
    private final ItemMapper itemMapper;
    @Getter
    private Portfolio portfolio;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, NameRepository nameRepository, SteamMarketService steamMarketService, ItemMapper itemMapper) {
        this.portfolioRepository = portfolioRepository;
        this.nameRepository = nameRepository;
        this.steamMarketService = steamMarketService;
        this.itemMapper = itemMapper;
    }

    @PostConstruct
    private void init() {
        initializePortfolio();
    }

    public void initializePortfolio() {
        // Load the last saved portfolio or create one if none exists
        if (portfolioRepository.count() == 0) {
            portfolio = portfolioRepository.save(new Portfolio());
        } else portfolio = portfolioRepository.findAll().getFirst();
        log.info("Loaded portfolio with ID: " + portfolio.getId());
    }

    public void addItemToPortfolio(ItemDTO itemDTO) {
        // If market name given not in the name hashset, not valid, throw exception!
        if (!nameRepository.contains(itemDTO.name())) {
            throw new ItemNotFoundOnMarketException(itemDTO.name());
        }
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());
        Item item = itemMapper.itemDTOtoItem(itemDTO);
        // If item is not present, add it. Else merge it with the existing one
        Optional<Item> existingItem = mutablePortfolioList.stream().filter(item1 -> item1.getName().equals(item.getName())).findFirst();
        if (existingItem.isEmpty()) {
            mutablePortfolioList.add(item);
        } else {
            Item existing = existingItem.get();
            int oldQuantity = existing.getQuantity();
            int addedQuantity = item.getQuantity();
            int newQuantity = oldQuantity + addedQuantity;

            BigDecimal newAvgPurchasePrice = existing.getPurchasePrice().multiply(BigDecimal.valueOf(oldQuantity))
                    .add(item.getPurchasePrice().multiply(BigDecimal.valueOf(addedQuantity)));

            existing.setPurchasePrice(newAvgPurchasePrice.divide(BigDecimal.valueOf(newQuantity), RoundingMode.HALF_UP));
            existing.setQuantity(newQuantity);
        }
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
            BigDecimal totalChangePercentage = currentValue.subtract(totalPurchasePrice).divide(totalPurchasePrice, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            portfolio.setTotalChangePercentage(totalChangePercentage);
        } else {
            portfolio.setTotalChangePercentage(BigDecimal.ZERO);
        }
        portfolio = portfolioRepository.save(portfolio);
    }

    public Portfolio refreshPortfolio() {
        updatePortfolio();
        return portfolio;
    }

    public void deleteItemFromPortfolio(Long id) {
        List<Item> mutablePortfolioList = new ArrayList<>(portfolio.getItemList());

        boolean removed = mutablePortfolioList.removeIf(item -> item.getId().equals(id));
        if (!removed) {
            throw new ItemNotFoundException(id);
        }
        portfolio.setItemList(mutablePortfolioList);
        updatePortfolio();
    }

    public Optional<Item> getMostProfitableItem() {
        return portfolio.getItemList().stream().max(Comparator.comparing(Item::getChangePercentage));
    }
}
