package csp.service;

import csp.exceptions.ItemNotFoundOnMarketException;
import csp.inventory.Item;
import csp.responses.SteamPriceOverview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SteamMarketService {
    private final WebClient webClient;

    public SteamMarketService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://steamcommunity.com/market/priceoverview").build();
    }

    public Mono<SteamPriceOverview> getItemPriceOverview(String marketHashName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("appid", "730")
                        .queryParam("currency", "3")
                        .queryParam("market_hash_name", marketHashName)
                        .build())
                .retrieve()
                .bodyToMono(SteamPriceOverview.class);
    }

    public List<Item> updateItemPrices(List<Item> itemList) {
        List<SteamPriceOverview> priceOverviewList = Flux.fromIterable(itemList)
                .flatMap(item -> getItemPriceOverview(item.getName())
                        .onErrorResume(e -> {
                            log.error("Failed to fetch price for " + item.getName() + ": " + e.getMessage());
                            return Mono.just(new SteamPriceOverview(false, "0", "0", "0"));
                        }))
                .collectList()
                .block();

        List<Item> itemsToRemove = new ArrayList<>();
        //TODO: FIX BUG THAT DELETES ALL ELEMENTS OF THE ITEM LIST AND WRITE TESTS
        for (int i = 0; i < itemList.size(); i++) {
            try {
                if (priceOverviewList.get(i).lowest_price() == null) {
                    itemsToRemove.add(itemList.get(i));
                    throw new ItemNotFoundOnMarketException(itemList.get(i).getName());
                }
                itemList.get(i).setCurrentPrice(BigDecimal.valueOf(Float.parseFloat(removeLastCharOptional(priceOverviewList.get(i).lowest_price()).replace(",", "."))).setScale(2, RoundingMode.HALF_UP));
            } catch (ItemNotFoundOnMarketException itemNotFoundOnMarketException) {
                // DO NOT parse price! Just log + continue
                log.warn("Item removed: {}", itemNotFoundOnMarketException.getMessage());
            } catch (Exception e) {
                log.error("Failed to parse price for " + itemList.get(i).getName() + ": " + e.getMessage());
                itemList.get(i).setCurrentPrice(BigDecimal.ZERO);
            }
        }
        itemList.removeAll(itemsToRemove);
        return itemList;
    }

    //Used to remove the currency indicator brought by the market response
    private static String removeLastCharOptional(String s) {
        return Optional.ofNullable(s)
                .filter(str -> !str.isEmpty())
                .map(str -> str.substring(0, str.length() - 1))
                .orElse(s);
    }
}
