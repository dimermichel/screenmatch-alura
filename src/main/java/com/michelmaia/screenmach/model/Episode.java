package com.michelmaia.screenmach.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

@Entity
@Table(name = "episodes")
public class Episode {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String title;
   private Integer season;
   private Integer episode;
   private Double rating;
   private LocalDate released;

   @ManyToOne
   private Serie serie;

    public Episode(Integer season, EpisodeDTO episodeDTO) {
        this.season = season;
        this.title = episodeDTO.title();
        this.episode = episodeDTO.episode() != null ? Integer.parseInt(episodeDTO.episode()) : null;
        try {
            this.rating = Double.parseDouble(episodeDTO.rating());
        } catch (NumberFormatException e) {
            this.rating = 0.0; // Handle case where a rating is not a valid double
        }
        try {
            this.released = LocalDate.parse(episodeDTO.released());
        } catch (DateTimeParseException e) {
            this.released = null; // Handle case where the released date is not valid
        }
    }

    public Episode() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDate getReleased() {
        return released;
    }

    public void setReleased(LocalDate released) {
        this.released = released;
    }

    @Override
    public String toString() {
        String formattedDate = released != null 
            ? released.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            : "N/A";
            
        return "Episode{" +
                "season=" + season +
                ", episode=" + episode +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", released=" + formattedDate +
                '}';
    }
}
