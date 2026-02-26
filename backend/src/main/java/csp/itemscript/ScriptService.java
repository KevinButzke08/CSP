package csp.itemscript;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Profile("script")
public class ScriptService implements CommandLineRunner {
    private final WebClient webClient;
    //TODO: Rebuild the ScriptService to take the JSON from ByMykel and extract all names from there
    public ScriptService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://steamcommunity.com/market/search/render").build();
    }

    public void getItemNames() throws InterruptedException {
        Set<String> distinctItemNames = new HashSet<>();
        for (int i = 0; i < 30051; i += 10) {
            int finalI = i;
            TimeUnit.SECONDS.sleep(10);
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

            for (SteamItem element : result.getResults()) {
                // 1. Remove StatTrak™, if existent
                // 2. Replace wear conditions e.g (Factory-New), if existent
                String clean_market_hash_name = element.getHash_name().replace("StatTrak™", "").replaceAll("\\s*\\([^)]*\\)", "").trim();

                String keyEnum = clean_market_hash_name.toUpperCase().replace(" ", "_");

                String enumEntry = keyEnum.concat("(\"").concat(clean_market_hash_name).concat("\"),");

                distinctItemNames.add(enumEntry);
                log.info(enumEntry);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("backend/src/main/java/csp/itemScript/ItemNames.txt"))) {
            for (String item : distinctItemNames) {
                writer.write(item);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        getItemNames();
    }
}
