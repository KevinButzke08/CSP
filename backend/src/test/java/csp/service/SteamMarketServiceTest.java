package csp.service;

import csp.inventory.Item;
import csp.responses.SteamPriceOverview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SteamMarketServiceTest {
    private SteamMarketService steamMarketService;

    @BeforeEach
    void setUp() {
        steamMarketService = new SteamMarketService(WebClient.builder().baseUrl("https://steamcommunity.com/market/priceoverview"));
    }

    @Test
    void updateItemPrices() {
        Item item1 = new Item();
        item1.setName("AK-47 | Redline");
        item1.setCurrentPrice(5);

        Item item2 = new Item();
        item2.setName("AWP | Asiimov");
        item2.setCurrentPrice(0);

        SteamPriceOverview price1 = new SteamPriceOverview(true, "3.75€", "0", "0");

        SteamPriceOverview price2 = new SteamPriceOverview(true, "12.50€", "0", "0");

        SteamMarketService mockService = Mockito.spy(steamMarketService);
        doReturn(Mono.just(price1))
                .when(mockService).getItemPriceOverview("AK-47 | Redline");
        doReturn(Mono.just(price2))
                .when(mockService).getItemPriceOverview("AWP | Asiimov");

        // Act
        List<Item> updated = mockService.updateItemPrices(List.of(item1, item2));

        // Assert
        assertEquals(3.75f, updated.get(0).getCurrentPrice(), 0.001);
        assertEquals(12.50f, updated.get(1).getCurrentPrice(), 0.001);
    }
}