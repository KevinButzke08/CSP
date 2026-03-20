package csp.service;

import csp.controller.ItemDTO;
import csp.exceptions.ItemNotFoundException;
import csp.exceptions.ItemNotFoundOnMarketException;
import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.NameRepository;
import csp.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PortfolioServiceTest {
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private NameRepository nameRepository;
    @MockitoBean
    private SteamMarketService steamMarketService;

    @BeforeEach
    void setup() {
        portfolioService.initializePortfolio();
    }

    @Test
    void testInit_loadsExistingPortfolioAutomatically() {
        Portfolio portfolio = portfolioService.getPortfolio();
        assertNotNull(portfolio);
        assertEquals(1, portfolio.getId());
    }

    @Test
    void testAddItemToPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(20)));
            return items;
        });

        ItemDTO item = new ItemDTO("Horizon Case", 2, BigDecimal.valueOf(10));
        portfolioService.addItemToPortfolio(item);

        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(0, portfolio.getItemList().getFirst().getCurrentPrice().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, portfolio.getTotalPurchasePrice().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, portfolio.getCurrentValue().compareTo(BigDecimal.valueOf(40)));
        verify(steamMarketService, times(1)).updateItemPrices(anyList());
    }

    @Test
    void testAddItem_mergesExistingItem() {
        ItemDTO firstItem = new ItemDTO("Horizon Case", 100, BigDecimal.valueOf(1.0));
        ItemDTO secondItem = new ItemDTO("Horizon Case", 100, BigDecimal.valueOf(3.0));
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(10)));
            return items;
        });

        portfolioService.addItemToPortfolio(firstItem);
        portfolioService.addItemToPortfolio(secondItem);

        List<Item> items = portfolioService.getPortfolio().getItemList();

        assertEquals(1, items.size());

        Item result = items.getFirst();

        assertEquals(200, result.getQuantity());
        assertEquals(0, result.getPurchasePrice().compareTo(BigDecimal.valueOf(2.0)));
    }

    @Test
    void testInvalidItemToPortfolio() {
        ItemDTO item = new ItemDTO("Invalid Case Name", 2, BigDecimal.valueOf(5));
        ItemNotFoundOnMarketException ex = assertThrows(ItemNotFoundOnMarketException.class, () -> portfolioService.addItemToPortfolio(item));
        assertEquals("No Item with the name " + item.name() + " found on the Steam market!", ex.getMessage());
    }

    @Test
    void testInvalidItemToPortfolioDueToCaseSensitivity() {
        ItemDTO item = new ItemDTO("horizon CASE", 2, BigDecimal.valueOf(5));
        ItemNotFoundOnMarketException ex = assertThrows(ItemNotFoundOnMarketException.class, () -> portfolioService.addItemToPortfolio(item));
        assertEquals("No Item with the name " + item.name() + " found on the Steam market!", ex.getMessage());
    }

    @Test
    @Transactional
    void testRefreshPortfolioWithChangedPrice() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(20)));
            return items;
        });
        ItemDTO item = new ItemDTO("Horizon Case", 2, BigDecimal.valueOf(10));
        portfolioService.addItemToPortfolio(item);

        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(30)));
            return items;
        });

        Portfolio portfolio = portfolioService.refreshPortfolio();
        assertEquals(0, portfolio.getItemList().getFirst().getCurrentPrice().compareTo(BigDecimal.valueOf(30)));
        assertEquals(0, portfolio.getTotalPurchasePrice().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, portfolio.getCurrentValue().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    void testDeleteItemFromPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(20)));
            return items;
        });

        ItemDTO itemDTO = new ItemDTO("Horizon Case", 2, BigDecimal.valueOf(10));
        portfolioService.addItemToPortfolio(itemDTO);

        Portfolio portfolio = portfolioService.getPortfolio();

        portfolioService.deleteItemFromPortfolio(portfolio.getItemList().getFirst().getId());
        assertEquals(0, portfolio.getItemList().size());
        assertEquals(0, portfolio.getTotalPurchasePrice().compareTo(BigDecimal.ZERO));
        assertEquals(0, portfolio.getCurrentValue().compareTo(BigDecimal.ZERO));
        assertEquals(0, portfolio.getTotalChangePercentage().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testDeleteNotFoundItemFromPortfolio() {
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> portfolioService.deleteItemFromPortfolio(2L));
        assertEquals("No Item with the id 2 found!", ex.getMessage());
    }

    @Test
    void testGetMostProfitableItem() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> {
            List<Item> items = inv.getArgument(0);
            items.forEach(i -> i.setCurrentPrice(BigDecimal.valueOf(20)));
            items.forEach(i -> i.setChangePercentage(BigDecimal.valueOf(50)));
            return items;
        });

        ItemDTO itemDTO = new ItemDTO("Horizon Case", 2, BigDecimal.valueOf(10));

        portfolioService.addItemToPortfolio(itemDTO);

        Optional<Item> resultItem = portfolioService.getMostProfitableItem();

        assertEquals(0, resultItem.get().getChangePercentage().compareTo(BigDecimal.valueOf(50)));
    }

    @Test
    void testGetMostProfitableItemEWithEmptyPortfolio() {
        Optional<Item> resultItem = portfolioService.getMostProfitableItem();

        assertTrue(resultItem.isEmpty());
    }
}