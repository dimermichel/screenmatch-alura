package com.michelmaia.screenmach;

import com.michelmaia.screenmach.model.Series;
import com.michelmaia.screenmach.service.CallAPI;
import com.michelmaia.screenmach.service.DataConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class ScreenmachApplication implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(ScreenmachApplication.class.getName());
    private static final String API_URL = "https://www.omdbapi.com/?t=";

    @Value("${omdb.api.key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(ScreenmachApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        try {
            var callAPI = new CallAPI();
            LOGGER.info("Starting Screenmach Application...");
            LOGGER.info("Please enter the series title (default: 'Game of Thrones'): ");
            var title = scanner.nextLine();

            if (title.isEmpty()) {
                LOGGER.warning("Title cannot be empty. Using default: 'Game of Thrones'");
                title = "Game of Thrones";
            } else {
                LOGGER.info("Searching for series: " + title);
            }

            scanner.close();
            var encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            var json = callAPI.getData(API_URL + encodedTitle + "&apikey=" + apiKey);
            
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("API Response received");
            }
            
            DataConverter converter = new DataConverter();
            Series series = converter.getData(json, Series.class);
            
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Series data: " + series.toString());
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing series data", e);
            throw e;
        }
    }
}
