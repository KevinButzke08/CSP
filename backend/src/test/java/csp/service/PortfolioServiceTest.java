package csp.service;

import csp.controller.ItemDTO;
import csp.exceptions.ItemNotFoundException;
import csp.exceptions.ItemNotFoundOnMarketException;
import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
// So that for every test, the @PostConstruct init method of the PortfolioService is run
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PortfolioServiceTest {
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private ItemMapper itemMapper;
    @MockitoBean
    private SteamMarketService steamMarketService;

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
        assertEquals(0, portfolio.getChangePercentage().compareTo(BigDecimal.ZERO));
    }
    @Test
    void testDeleteNotFoundItemFromPortfolio() {
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> portfolioService.deleteItemFromPortfolio(2L));
        assertEquals("No Item with the id 2 found!", ex.getMessage());
    }
}