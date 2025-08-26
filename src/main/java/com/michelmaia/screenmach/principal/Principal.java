package com.michelmaia.screenmach.principal;

import com.michelmaia.screenmach.model.Season;
import com.michelmaia.screenmach.model.Serie;
import com.michelmaia.screenmach.model.SeriesDTO;
import com.michelmaia.screenmach.repository.SerieRepository;
import com.michelmaia.screenmach.service.CallAPI;
import com.michelmaia.screenmach.service.DataConverter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class Principal {
    private static final Logger LOGGER = Logger.getLogger(Principal.class.getName());
    private static final String API_URL = "https://www.omdbapi.com/?t=";
    private static final String API_URL_PARAM = "&apikey=";


    private CallAPI callAPI = new CallAPI();
    private DataConverter converter = new DataConverter();
    private Scanner scanner = new Scanner(System.in);
    private String apiKey = System.getenv("OMDB_API_KEY");
    private SerieRepository serieRepository;
    private List<SeriesDTO> seriesDTOList = new ArrayList<>();

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void showMenu() {
        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.severe("API key is not set. Please set the 'OMDB_API_KEY' property.");
            return;
        }

        var option = -1;
        LOGGER.info("Welcome to the ScreenMatch Application!");
        while (option != 0) {
            LOGGER.info("--------------------------------");
            LOGGER.info("Please choose an option:");
            LOGGER.info("1. Search for a series");
            LOGGER.info("2. Search for a episodes");
            LOGGER.info("3. List searched series");
            LOGGER.info("0. Exit");
            LOGGER.info("--------------------------------");

            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid input. Please enter a number.");
                continue;
            }

            switch (option) {
                case 1 -> searchSeriesWeb();
                case 2 -> searchEpisodeBySeries();
                case 3 -> listSearchedSeries();
                case 0 -> LOGGER.info("Exiting the application. Goodbye!");
                default -> LOGGER.warning("Invalid option. Please try again.");
            }
        }
    }

    private void listSearchedSeries() {
        List<Serie> seriesList = serieRepository.findAll();

        LOGGER.info("List of searched series:");
        if (seriesList.isEmpty()) {
            LOGGER.info("No series have been searched yet.");
        } else {
              seriesList.stream().sorted(Comparator.comparing(Serie::getGenre))
                      .forEach(serie -> LOGGER.info(serie.toString()));
        }
    }

    private void searchSeriesWeb() {
        SeriesDTO seriesDTO = searchSeries();
        if (seriesDTO == null || seriesDTO.title() == null) {
            LOGGER.warning("No series found. Please try again.");
        } else {
            Serie serie = new Serie(seriesDTO);
            serieRepository.save(serie);
            seriesDTOList.add(seriesDTO);
            LOGGER.info("Series found: " + seriesDTO);
        }
    }

    private SeriesDTO searchSeries() {
        LOGGER.info("Please enter the series title: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) {
            LOGGER.warning("Title cannot be empty. Using default: 'Game of Thrones'");
            title = "Game of Thrones";
        }
        try {
            var encodedTitle = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
            var json = callAPI.getData(API_URL + encodedTitle + API_URL_PARAM + apiKey);
            return converter.getData(json, SeriesDTO.class);
        } catch (Exception e) {
            LOGGER.severe("Error searching for series: " + e.getMessage());
            return null;
        }
    }

    private void searchEpisodeBySeries() {
        LOGGER.info("Please enter the series title: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) {
            LOGGER.warning("Title cannot be empty. Using default: 'Game of Thrones'");
            title = "Game of Thrones";
        }
        try {
            var encodedTitle = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);
            var json = callAPI.getData(API_URL + encodedTitle + API_URL_PARAM + apiKey);
            SeriesDTO seriesDTO = converter.getData(json, SeriesDTO.class);

            List<Season> seasons = new ArrayList<>();

            for (int i = 1; i <= Integer.parseInt(seriesDTO.seasons()); i++) {
                var seasonJson = callAPI.getData(API_URL + encodedTitle + "&Season=" + i + API_URL_PARAM + apiKey);
                Season season = converter.getData(seasonJson, Season.class);
                seasons.add(season);
            }

            seasons.forEach(season -> {
                        LOGGER.info("Season: " + season.season());
                        season.episodeDTOS().forEach(episodeDTO ->
                            LOGGER.info("#: " + episodeDTO.episode() + " - " + episodeDTO.title())
                        );
                    }
            );
        } catch (Exception e) {
            LOGGER.severe("Error searching for episodes: " + e.getMessage());
        }
    }
}
