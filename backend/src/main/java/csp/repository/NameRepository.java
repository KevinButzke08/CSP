package csp.repository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class NameRepository {
    private final Set<String> names = new HashSet<>();

    @PostConstruct
    public void init() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ItemNames.txt");

        if (inputStream == null) {
            throw new FileNotFoundException("Couldn't find ItemNames.txt in classpath!");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line.trim());
            }
        }
        log.info("Loaded all " + names.size() + " items into the HashSet successfully!");
    }

    public boolean contains(String name) {
        return names.contains(name);
    }
}
