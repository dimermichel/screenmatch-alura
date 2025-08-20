package com.michelmaia.screenmach.principal;

import com.michelmaia.screenmach.model.Episode;
import com.michelmaia.screenmach.model.EpisodeDTO;
import com.michelmaia.screenmach.model.Season;
import com.michelmaia.screenmach.model.Series;
import com.michelmaia.screenmach.service.CallAPI;
import com.michelmaia.screenmach.service.DataConverter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Principal {
    private static final Logger LOGGER = Logger.getLogger(Principal.class.getName());
    private static final String API_URL = "https://www.omdbapi.com/?t=";

    private CallAPI callAPI = new CallAPI();
    private DataConverter converter = new DataConverter();
    private Scanner scanner = new Scanner(System.in);

    public void showMenu(String apiKey) {
        LOGGER.info("Please enter the series title (default: 'Game of Thrones'): ");
        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.severe("API key is not set. Please set the 'omdb.api.key' property.");
            return;
        }
        var titleInput = scanner.nextLine();
        if (titleInput.isEmpty()) {
            LOGGER.warning("Title cannot be empty. Using default: 'Game of Thrones'");
            titleInput = "Game of Thrones";
        } else {
            LOGGER.info("Searching for series: " + titleInput);
        }
        try {
            var encodedTitle = java.net.URLEncoder.encode(titleInput, StandardCharsets.UTF_8);
            var json = callAPI.getData(API_URL + encodedTitle + "&apikey=" + apiKey);

            Series series = converter.getData(json, Series.class);

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("API Response received");
                LOGGER.info("Series data: " + series.toString());
            }

            List<Season> seasons = new ArrayList<>();

            for (int i = 1; i <= Integer.parseInt(series.seasons()); i++) {
                var seasonJson = callAPI.getData(API_URL + encodedTitle + "&Season=" + i + "&apikey=" + apiKey);
                Season season = converter.getData(seasonJson, Season.class);
                seasons.add(season);
            }

            seasons.forEach(season -> {
                        LOGGER.info("Season: " + season.season());
                        season.episodeDTOS().forEach(episodeDTO -> {
                            LOGGER.info("#: " + episodeDTO.episode() + " - " + episodeDTO.title());
                        });
                    }
            );

            List<EpisodeDTO> episodeDTOS = seasons.stream()
                    .flatMap(season -> season.episodeDTOS().stream())
                    .collect(Collectors.toList());

            episodeDTOS.stream()
                    .filter(episodeDTO -> !episodeDTO.rating().equalsIgnoreCase("N/A"))
                    .sorted(Comparator.comparing(EpisodeDTO::rating).reversed())
                    .limit(5)
                    .forEach(System.out::println);

            List<Episode> episodes = seasons.stream()
                        .flatMap(season -> season.episodeDTOS().stream()
                                .map(episodeDTO -> new Episode(Integer.parseInt(season.season()), episodeDTO))
                    ).collect(Collectors.toList());


            episodes.forEach(System.out::println);

            LOGGER.info("Total episodes found: " + episodes.size());

            Map<Integer, Double> averageRatings = episodes.stream()
                    .filter(episode -> episode.getRating() != null && episode.getRating() > 0)
                    .collect(Collectors.groupingBy(Episode::getSeason,
                            Collectors.averagingDouble(Episode::getRating)));

            DoubleSummaryStatistics statistics = episodes.stream()
                    .filter(episode -> episode.getRating() != null && episode.getRating() > 0)
                    .collect(Collectors.summarizingDouble(Episode::getRating));
            LOGGER.info("Average rating per season: " + averageRatings);
            LOGGER.info("Overall average rating: " + statistics.getAverage());
            LOGGER.info("Highest rating: " + statistics.getMax());
            LOGGER.info("Lowest rating: " + statistics.getMin());
            LOGGER.info("Total number of episodes: " + statistics.getCount());

            System.out.println(averageRatings);

            LOGGER.info("What is the starting year of the episode do you want to search? (default: current year)");
            var yearInput = scanner.nextInt();
            scanner.nextLine();

            if (yearInput < 1900 || yearInput > LocalDate.now().getYear()) {
                LOGGER.warning("Year cannot be less than 1900 or greater than the current year. Using default: " + LocalDate.now().getYear());
                yearInput = LocalDate.now().getYear();
            } else {
                LOGGER.info("Searching for episodes after the year: " + yearInput);
            }

            LocalDate searchDate = LocalDate.of(yearInput, 1, 1);

            List<Episode> filteredEpisodes = episodes.stream()
                    .filter(episode -> episode.getReleased() != null && episode.getReleased().isAfter(searchDate))
                    .sorted(Comparator.comparing(Episode::getReleased))
                    .collect(Collectors.toList());

            if (filteredEpisodes.isEmpty()) {
                LOGGER.info("No episodes found after " + yearInput);
            } else {
                LOGGER.info("Episodes after " + yearInput + ":");
                filteredEpisodes.forEach(episode -> {
                    String formattedDate = episode.getReleased() != null
                            ? episode.getReleased().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            : "N/A";

                    LOGGER.info("Season: " + episode.getSeason() + ", Episode: " + episode.getEpisode() +
                            ", Title: " + episode.getTitle() + ", Rating: " + episode.getRating() +
                            ", Released: " + formattedDate);
                });
            }

            LOGGER.info("Please enter the title of the episode you want to search for: ");
            var episodeTitleInput = scanner.nextLine();

            if (episodeTitleInput.isEmpty()) {
                LOGGER.warning("Episode title cannot be empty. Using default: 'Winter Is Coming'");
                episodeTitleInput = "Winter Is Coming";
            } else {
                LOGGER.info("Searching for episode: " + episodeTitleInput);
            }

            String finalEpisodeTitleInput = episodeTitleInput;
            Optional<Episode> episodeFind = episodes.stream()
                    .filter(episode -> episode.getTitle().toUpperCase().contains(finalEpisodeTitleInput.toUpperCase()))
                    .findFirst();

        if (episodeFind.isPresent()) {
            Episode episode = episodeFind.get();
            LOGGER.info("Episode found: " + episode.toString());
        } else {
            LOGGER.info("Episode not found with title: " + finalEpisodeTitleInput);
        }

        } catch (Exception e) {
            LOGGER.severe("Error processing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
