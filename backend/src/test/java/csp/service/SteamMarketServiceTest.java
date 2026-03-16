package csp.service;

import csp.inventory.Item;
import csp.responses.SteamPriceOverview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

class SteamMarketServiceTest {
    private SteamMarketService steamMarketService;

    @BeforeEach
    void setUp() {
        steamMarketService = new SteamMarketService(WebClient.builder().baseUrl("https://steamcommunity.com/market/priceoverview"));
    }

    @Test
    void updateItemPrices() {
        Item item1 = new Item();
        item1.setName("Operation Riptide Case");
        item1.setCurrentPrice(BigDecimal.valueOf(5));

        Item item2 = new Item();
        item2.setName("Operation Bravo Case");
        item2.setCurrentPrice(BigDecimal.ZERO);

        SteamPriceOverview price1 = new SteamPriceOverview(true, "3.75€", "0", "0");

        SteamPriceOverview price2 = new SteamPriceOverview(true, "2,--€", "0", "0");

        SteamMarketService mockService = Mockito.spy(steamMarketService);
        doReturn(Mono.just(price1))
                .when(mockService).getItemPriceOverview("Operation Riptide Case");
        doReturn(Mono.just(price2))
                .when(mockService).getItemPriceOverview("Operation Bravo Case");

        // Act
        List<Item> updated = mockService.updateItemPrices(List.of(item1, item2));

        // Assert
        assertEquals(0, updated.get(0).getCurrentPrice().compareTo(BigDecimal.valueOf(3.75)));
        assertEquals(0, updated.get(1).getCurrentPrice().compareTo(BigDecimal.valueOf(2)));
    }
}