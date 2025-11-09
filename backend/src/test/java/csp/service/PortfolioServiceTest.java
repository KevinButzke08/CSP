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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        item.setCurrentPrice(BigDecimal.valueOf(5));
        item.setPurchasePrice(BigDecimal.valueOf(2.5));
        item.setQuantity(1);

        Item item2 = new Item();
        item2.setName("AK-47 | Cartel");
        item2.setCurrentPrice(BigDecimal.TEN);
        item2.setPurchasePrice(BigDecimal.valueOf(5));
        item2.setQuantity(1);

        portfolioService.addItemToPortfolio(item);
        portfolioService.addItemToPortfolio(item2);
        portfolioService.updatePortfolio();

        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(2, portfolio.getItemList().size());
        assertEquals(0, portfolio.getChangePercentage().compareTo(BigDecimal.valueOf(100)));
        verify(steamMarketService, times(3)).updateItemPrices(anyList());
    }

    @Test
    void testAddItemToPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> inv.getArgument(0));

        Item item = new Item();
        item.setName("AK-47 | Cartel");
        item.setCurrentPrice(BigDecimal.valueOf(20));
        item.setPurchasePrice(BigDecimal.valueOf(10));
        item.setQuantity(2);
        portfolioService.addItemToPortfolio(item);

        Portfolio portfolio = portfolioService.getPortfolio();
        assertEquals(0, portfolio.getItemList().getFirst().getCurrentPrice().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, portfolio.getTotalPurchasePrice().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, portfolio.getCurrentValue().compareTo(BigDecimal.valueOf(40)));
    }

    @Test
    void testDeleteItemFromPortfolio() {
        when(steamMarketService.updateItemPrices(anyList())).thenAnswer(inv -> inv.getArgument(0));

        Item item = new Item();
        item.setName("AK-47 | Cartel");
        item.setCurrentPrice(BigDecimal.valueOf(20));
        item.setPurchasePrice(BigDecimal.valueOf(10));
        item.setQuantity(2);
        portfolioService.addItemToPortfolio(item);
        Portfolio portfolio = portfolioService.getPortfolio();

        portfolioService.deleteItemFromPortfolio(portfolio.getItemList().getFirst().getId());
        assertEquals(0, portfolio.getItemList().size());
        assertEquals(0, portfolio.getTotalPurchasePrice().compareTo(BigDecimal.ZERO));
        assertEquals(0, portfolio.getCurrentValue().compareTo(BigDecimal.ZERO));
        assertEquals(0, portfolio.getChangePercentage().compareTo(BigDecimal.ZERO));
    }
}