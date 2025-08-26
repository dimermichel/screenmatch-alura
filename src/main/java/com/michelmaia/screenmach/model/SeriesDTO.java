package com.michelmaia.screenmach.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeriesDTO(
    @JsonAlias("Title") String title,
    @JsonAlias("totalSeasons") String seasons,
    @JsonAlias("imdbRating") String rating,
    @JsonAlias("Genre") String genre,
    @JsonAlias("Actors") String actors,
    @JsonAlias("Plot") String plot,
    @JsonAlias("Poster") String poster
) {}
