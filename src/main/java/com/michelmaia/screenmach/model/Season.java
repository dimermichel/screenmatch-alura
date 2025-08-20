package com.michelmaia.screenmach.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Season(
    @JsonAlias("Title") String title,
    @JsonAlias("Season") String season,
    @JsonAlias("Episodes") List<EpisodeDTO> episodeDTOS
) {
}
