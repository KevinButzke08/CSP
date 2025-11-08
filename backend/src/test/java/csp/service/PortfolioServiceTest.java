package csp.service;

import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
    @MockitoBean
    private SteamMarketService steamMarketService;

    @Test
    void testInit_loadsExistingPortfolioAutomatically() {
        Portfolio portfolio = portfolioService.getPortfolio();
        System.out.println(portfolio.toString());
        assertNotNull(portfolio);
        assertEquals(1, portfolio.getId());
    }

    @Test
    void testUpdatePortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> inv.getArgument(0));

        Item item = new Item();
        item.setName("AK-47 | Redline");
        item.setCurrentPrice(5f);
        item.setPurchasePrice(2.5f);
        item.setQuantity(1);

        Item item2 = new Item();
        item2.setName("AK-47 | Cartel");
        item2.setCurrentPrice(10f);
        item2.setPurchasePrice(5f);
        item2.setQuantity(1);

        portfolioService.addItemToPortfolio(item);
        portfolioService.addItemToPortfolio(item2);
        portfolioService.updatePortfolio();

        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(2, portfolio.getItemList().size());
        assertEquals(100f, portfolio.getChangePercentage());
        verify(steamMarketService, times(3)).updateItemPrices(anyList());
    }

    @Test
    void testAddItemToPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> inv.getArgument(0));

        Item item = new Item();
        item.setName("AK-47 | Cartel");
        item.setCurrentPrice(20f);
        item.setPurchasePrice(10f);
        item.setQuantity(2);
        portfolioService.addItemToPortfolio(item);

        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(20f, portfolio.getItemList().getFirst().getCurrentPrice());
        assertEquals(20f, portfolio.getTotalPurchasePrice());
        assertEquals(40f, portfolio.getCurrentValue());
    }

    @Test
    void testDeleteItemFromPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> inv.getArgument(0));

        Item item = new Item();
        item.setName("AK-47 | Cartel");
        item.setCurrentPrice(20f);
        item.setPurchasePrice(10f);
        item.setQuantity(2);
        portfolioService.addItemToPortfolio(item);

        portfolioService.deleteItemFromPortfolio("AK-47 | Cartel");
        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(0, portfolio.getItemList().size());
        assertEquals(0, portfolio.getTotalPurchasePrice());
        assertEquals(0, portfolio.getCurrentValue());
        assertEquals(0, portfolio.getChangePercentage());
    }
}