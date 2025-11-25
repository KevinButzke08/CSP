package csp.itemScript;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Slf4j
@Service
@Profile("script")
public class ScriptService implements CommandLineRunner {
    private final WebClient webClient;

    public ScriptService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://steamcommunity.com/market/search/render").build();
    }

    public void getItemNames() {
        ArrayList<SteamMarketListings> resultList = new ArrayList<>();
        for (int i = 0; i < 100; i += 10) {
            int finalI = i;
            SteamMarketListings result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("start", finalI)
                            .queryParam("count", "10")
                            .queryParam("search_descriptions", "0")
                            .queryParam("sort_column", "popular")
                            .queryParam("sort_dir", "desc")
                            .queryParam("appid", "730")
                            .queryParam("norender", "1")
                            .build())
                    .retrieve()
                    .bodyToMono(SteamMarketListings.class)
                    .block();
            resultList.add(result);
        }
        log.info("INFO: " + resultList.size());
    }

    @Override
    public void run(String... args) throws Exception {
        getItemNames();
    }
}
