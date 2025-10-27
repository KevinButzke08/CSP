package github.project.CSP.service;

import github.project.CSP.inventory.Item;
import github.project.CSP.responses.SteamPriceOverview;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
    public float getCurrentPortfolioValue(List<Item> portfolioItems) {
        //Using flux, iterate over all Portfolio List<Items> and make a shared request and block until all are done, then return
        //complete currentPortfolio value, but also set for each Item the currentPrice
        //TODO: Implement Portfolio Service, that calls this method with its Portfolio items
        return 0;
    }

}
