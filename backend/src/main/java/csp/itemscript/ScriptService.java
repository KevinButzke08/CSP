package csp.itemscript;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("script")
public class ScriptService implements CommandLineRunner {
    // Using RestClient, as we need to make one time request, which can also be blocking
    private final RestClient restClient;
    private final JsonFactory jsonFactory = new JsonFactory();

    public ScriptService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://raw.githubusercontent.com/ByMykel/CSGO-API/main/public/api/en/all.json").build();
    }

    public void getItemNames() {
        Set<String> distinctItemNames = new HashSet<>();
        String json = restClient.get().retrieve().body(String.class);
        JsonParser parser = jsonFactory.createParser(ObjectReadContext.empty(), json);

        while (parser.nextToken() != null) {
            // Search for market_hash_name
            if (parser.currentToken() == JsonToken.PROPERTY_NAME && parser.currentName().contentEquals("market_hash_name")) {
                parser.nextToken();
                // If market_hash_name = null, item cant go on the market so skip it
                if (parser.getValueAsString() != null) {
                    String enumEntry = parser.getValueAsString().trim();
                    distinctItemNames.add(enumEntry);
                    log.info(enumEntry);
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("backend/src/main/resources/ItemNames.txt"))) {
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
        log.info("Successfully loaded all item names!");
    }
}
