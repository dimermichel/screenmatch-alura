package com.michelmaia.screenmach.principal;

import com.michelmaia.screenmach.model.*;
import com.michelmaia.screenmach.repository.SerieRepository;
import com.michelmaia.screenmach.service.CallAPI;
import com.michelmaia.screenmach.service.DataConverter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private List<Serie> seriesList = new ArrayList<>();

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
            LOGGER.info("4. Search series by title");
            LOGGER.info("5. Search series by actor");
            LOGGER.info("6. Top 5 series by rating");
            LOGGER.info("7. List series by genre");
            LOGGER.info("8. List series by number of seasons and rating (e.g., more than 3 seasons and rating above 8.0)");
            LOGGER.info("9. List episodes by title keyword (e.g., 'dragon', 'space', 'detective')");
            LOGGER.info("10. List top 5 episodes per series");
            LOGGER.info("11. List episodes by release date");
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
                case 4 -> listSeriesByTitle();
                case 5 -> listSeriesByActor();
                case 6 -> listTop5Series();
                case 7 -> listSeriesByGenre();
                case 8 -> listSeriesByNumberOfSeasonsAndRating();
                case 9 -> listEpisodesByTitle();
                case 10 -> listTop5EpisodesBySeries();
                case 11 -> listEpisodesByDate();
                case 0 -> LOGGER.info("Exiting the application. Goodbye!");
                default -> LOGGER.warning("Invalid option. Please try again.");
            }
        }
    }

    private void listEpisodesByDate() {
        LOGGER.info("Please enter the series title: ");
        var title = scanner.nextLine();
        Optional<Serie> serie = serieRepository.findByTitleContainingIgnoreCase(title);
        var serieGet = serie.get();
        if (serie.isPresent()) {
            LOGGER.info("Series found: " + serie.get());
            LOGGER.info("Please insert the year (YYYY): ");
            var yearInput = scanner.nextLine();
            int year;
            try {
                year = Integer.parseInt(yearInput);
                List<Episode> episodesByYear = serieRepository.episodesByYear(serieGet, year);
                if (episodesByYear.isEmpty()) {
                    LOGGER.info("No episodes found for the series " + serieGet.getTitle() + " from the year " + year + " and above.");
                } else {
                    LOGGER.info("Episodes from the year " + year + " for the series " + serieGet.getTitle() + ":");
                    episodesByYear.forEach(episode -> LOGGER.info("Season: " + episode.getSeason() +
                            " | Released: " + episode.getReleased() +
                            " | Episode: " + episode.getEpisode() +
                            " | Title: " + episode.getTitle() +
                            " | Rating: " + episode.getRating()));
                }
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid year format. Please enter a valid year (YYYY).");
                return;
            }
        } else {
            LOGGER.info("No series found with the title containing: " + title);
        }
    }

    private void listTop5EpisodesBySeries() {
        LOGGER.info("Listing top 5 episodes by series. Please enter the series title: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) {
            LOGGER.warning("Title cannot be empty. Please try again.");
            return;
        }
        Optional<Serie> serieOpt = serieRepository.findByTitleContainingIgnoreCase(title);
        if (serieOpt.isEmpty()) {
            LOGGER.info("No series found with the title containing: " + title);
            return;
        }
        Serie serie = serieOpt.get();
        List<Episode> top5Episodes = serie.getEpisodes().stream()
                .sorted(Comparator.comparing(Episode::getRating).reversed())
                .limit(5)
                .collect(Collectors.toList());
        if (top5Episodes.isEmpty()) {
            LOGGER.info("No episodes found for the series: " + serie.getTitle());
        } else {
            LOGGER.info("Top 5 episodes for the series " + serie.getTitle() + ":");
            top5Episodes.forEach(episode -> LOGGER.info("Season: " + episode.getSeason() +
                    " | Episode: " + episode.getEpisode() +
                    " | Title: " + episode.getTitle() +
                    " | Rating: " + episode.getRating()));
        }
    }

    private void listEpisodesByTitle() {
        LOGGER.info("Please enter a keyword to search in episode titles: ");
        String keyword = scanner.nextLine();
        if (keyword.isEmpty()) {
            LOGGER.warning("Keyword cannot be empty. Please try again.");
            return;
        }

        List<Episode> matchingEpisodes = serieRepository.episodesByTitleKeyword(keyword);

        if (matchingEpisodes.isEmpty()) {
            LOGGER.info("No episodes found with the keyword: " + keyword);
        } else {
            LOGGER.info("Episodes found with the keyword '" + keyword + "':");
            matchingEpisodes.stream()
                    .sorted(Comparator.comparing(Episode::getTitle))
                    .forEach(episode -> LOGGER.info("Series: " + episode.getSerie().getTitle() +
                            " | Season: " + episode.getSeason() +
                            " | Episode: " + episode.getEpisode() +
                            " | Title: " + episode.getTitle() +
                            " | Rating: " + episode.getRating()));
        }

    }

    private void listSeriesByNumberOfSeasonsAndRating() {
        LOGGER.info("Please enter the minimum number of seasons: ");
        int minSeasons;
        try {
            minSeasons = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid input for number of seasons. Please enter a valid integer.");
            return;
        }

        LOGGER.info("Please enter the minimum rating (e.g., 8.0): ");
        double minRating;
        try {
            minRating = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid input for rating. Please enter a valid number.");
            return;
        }

        List<Serie> filteredSeries = serieRepository.seriesBySeasonsAndRating(minSeasons, minRating);

        if (filteredSeries.isEmpty()) {
            LOGGER.info("No series found with more than " + minSeasons + " seasons and rating above " + minRating);
        } else {
            LOGGER.info("Series found with more than " + minSeasons + " seasons and rating above " + minRating + ":");
            filteredSeries.stream().sorted(Comparator.comparing(Serie::getTitle))
                    .forEach(serie -> LOGGER.info(serie.getTitle() + " - Seasons: " + serie.getSeasons() + ", Rating: " + serie.getRating()));
        }
    }

    private void listSeriesByGenre() {
        LOGGER.info("Please enter the genre: ");
        var genreInput = scanner.nextLine();
        var genre = Arrays.stream(Genre.values())
                .filter(g -> g.name().equalsIgnoreCase(genreInput))
                .findFirst()
                .orElse(null);

        if (genre == null) {
            LOGGER.warning("Invalid genre. Please try again.");
            return;
        }

        List<Serie> seriesByGenre = serieRepository.findByGenre(genre);

        if (seriesByGenre.isEmpty()) {
            LOGGER.info("No series found in the genre: " + genre);
        } else {
            LOGGER.info("Series found in the genre " + genre + ":");
            seriesByGenre.stream().sorted(Comparator.comparing(Serie::getTitle))
                    .forEach(serie -> LOGGER.info(serie.toString()));
        }
    }

    private void listTop5Series() {
        var top5Series = serieRepository.findTop5ByOrderByRatingDesc();
        if (top5Series.isEmpty()) {
            LOGGER.info("No series found in the database.");
        } else {
            LOGGER.info("Top 5 series by rating:");
            top5Series.forEach(serie -> LOGGER.info(serie.getTitle() + " - Rating: " + serie.getRating()));
        }
    }

    private void listSeriesByActor() {
        LOGGER.info("Please enter the actor's name: ");
        var actor = scanner.nextLine();
        Optional<List<Serie>> series = serieRepository.findByActorsContainingIgnoreCase(actor);

        if (series.isEmpty()) {
            LOGGER.info("No series found with the actor: " + actor);
        } else {
            LOGGER.info("Series found with actor " + actor + ":");
            series.get().stream().sorted(Comparator.comparing(Serie::getTitle))
                    .forEach(serie -> LOGGER.info(serie.toString()));
        }
    }

    private void listSeriesByTitle() {
        LOGGER.info("Please enter the series title: ");
        var title = scanner.nextLine();
        Optional<Serie> serie = serieRepository.findByTitleContainingIgnoreCase(title);

        if (serie.isPresent()) {
            LOGGER.info("Series found: " + serie.get());
        } else {
            LOGGER.info("No series found with the title containing: " + title);
        }
    }

    private void listSearchedSeries() {
        seriesList = serieRepository.findAll();

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
        listSearchedSeries();
        LOGGER.info("Please enter the series title: ");
        String title = scanner.nextLine();
        if (title.isEmpty()) {
            LOGGER.warning("Title cannot be empty. Using default: 'Game of Thrones'");
            title = "Game of Thrones";
        }

        Optional<Serie> serie = serieRepository.findByTitleContainingIgnoreCase(title);

        if (serie.isPresent()) {
            try {
                var serieGet = serie.get();
                var encodedTitle = java.net.URLEncoder.encode(title, StandardCharsets.UTF_8);

                List<Season> seasons = new ArrayList<>();

                for (int i = 1; i <= serieGet.getSeasons(); i++) {
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

                List<Episode> episodes = seasons.stream().flatMap(e -> e.episodeDTOS().stream()
                                .map(ep -> new Episode(Integer.parseInt(e.season()), ep)))
                        .collect(Collectors.toList());

                serieGet.setEpisodes(episodes);
                serieRepository.save(serieGet);
            } catch (Exception e) {
                LOGGER.severe("Error searching for episodes: " + e.getMessage());
            }
        }
    }
}
