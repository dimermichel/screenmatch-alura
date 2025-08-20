package com.michelmaia.screenmach;

import com.michelmaia.screenmach.principal.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmachApplication implements CommandLineRunner {

    @Value("${omdb.api.key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(ScreenmachApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal();
        principal.showMenu(apiKey);

        /*
        LOGGER.info("Insert a season number to get more details (1 to " + series.seasons() + "): ");
        var seasonInput = scanner.nextLine();
        if (seasonInput.isEmpty() || Integer.parseInt(seasonInput) < 1 || Integer.parseInt(seasonInput) > Integer.parseInt(series.seasons())) {
            seasonInput = "1";
            LOGGER.warning("Season cannot be empty or greater than " + series.seasons() + ". Using default: 1");
        }

        var seasonJson = callAPI.getData(API_URL + encodedTitle + "&Season=" + seasonInput + "&apikey=" + apiKey);
        Season season = converter.getData(seasonJson, Season.class);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Season data: " + season.toString());
        }

        LOGGER.info("Insert a episode number to get more details (1 to " + season.episodes().size() + "): ");
        var episodeInput = scanner.nextLine();
        if (episodeInput.isEmpty() || Integer.parseInt(episodeInput) < 1 || Integer.parseInt(episodeInput) > season.episodes().size()) {
            episodeInput = "1";
            LOGGER.warning("Episode cannot be empty or greater than "+ season.episodes().size() + ". Using default: 1");
        }

        var episodeJson = callAPI.getData(API_URL + encodedTitle + "&Season=" + seasonInput + "&Episode=" + episodeInput + "&apikey=" + apiKey);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("API Response received");
        }
        Episode episode = converter.getData(episodeJson, Episode.class);
        LOGGER.info("Episode data: " + episode.toString());
         */

    }
}
