package com.michelmaia.screenmach.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EpisodeDTO(
    @JsonAlias("Title") String title,
    @JsonAlias("Season") String season,
    @JsonAlias("Episode") String episode,
    @JsonAlias("imdbRating") String rating,
    @JsonAlias("Released") String released
) {
}
