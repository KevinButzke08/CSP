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

    public void getItemNames() throws Exception {
        Set<String> distinctItemNames = new HashSet<>();
        String json = restClient.get().retrieve().body(String.class);
        JsonParser parser = jsonFactory.createParser(ObjectReadContext.empty(), json);

        while (parser.nextToken() != null) {
            // Search for market_hash_name
            if (parser.currentToken() == JsonToken.PROPERTY_NAME && parser.currentName().contentEquals("market_hash_name")) {
                parser.nextToken();
                // If market_hash_name = null, item cant go on the market so skip it
                if (parser.getValueAsString() != null) {
                    String enumEntry = getEnumEntry(parser);
                    distinctItemNames.add(enumEntry);
                    log.info(enumEntry);
                }
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
    //TODO: Fix the space left after removing the (holo) of a sticker
    private static String getEnumEntry(JsonParser parser) {
        // Remove StatTrak and all Condition parentheses like (Factory New)
        String valueEnum = parser.getValueAsString().replace("StatTrak™ ", "").replaceAll("\\s*\\([^)]*\\)", "").trim();
        String keyEnum = valueEnum.toUpperCase()
                .replaceAll("[^A-Z0-9_]", "_")   // replace any non-alphanumeric/underscore with _
                .replaceAll("_+", "_")           // collapse multiple underscores
                .replaceAll("^_|_$", "");        // remove leading/trailing underscores
        if (Character.isDigit(keyEnum.charAt(0))) {              // E.g "2020 RMR" enum can't start with number
            keyEnum = "_" + keyEnum;
        }
        return keyEnum.concat("(\"").concat(valueEnum).concat("\"),");
    }

    @Override
    public void run(String... args) throws Exception {
        getItemNames();
        log.info("Successfully loaded all item names!");
    }
}
