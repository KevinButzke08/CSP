package csp.service;

import csp.inventory.Item;
import csp.responses.SteamPriceOverview;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
                .flatMap(item -> getItemPriceOverview(item.getName()))
                .collectList()
                .block();
        //TODO:ERROR HANDLING HERE; IF THE REQUESTS SOMEHOW FAIL
        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setCurrentPrice(Float.parseFloat(priceOverviewList.get(i).lowest_price()));
        }
        return itemList;
    }
}
