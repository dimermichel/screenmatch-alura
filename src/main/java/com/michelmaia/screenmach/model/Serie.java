package com.michelmaia.screenmach.model;

import com.michelmaia.screenmach.service.ChatGPTGet;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private Integer seasons;
    private Double rating;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private String actors;
    private String plot;
    private String poster;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episode> episodes = new ArrayList<>();

    public Serie(SeriesDTO seriesDTO) {
        this.title = seriesDTO.title();
        this.seasons = seriesDTO.seasons() != null ? Integer.parseInt(seriesDTO.seasons()) : null;
        this.rating = OptionalDouble.of(Double.valueOf(seriesDTO.rating())).orElse(0.0);
        this.genre = Genre.fromString(seriesDTO.genre().split(",")[0].trim());
        this.actors = seriesDTO.actors();
        this.poster = seriesDTO.poster();
        this.plot = ChatGPTGet.getTranslation(seriesDTO.plot()).trim();
    }

    public Serie() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        episodes.forEach(episode -> episode.setSerie(this));
        this.episodes = episodes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSeasons() {
        return seasons;
    }

    public void setSeasons(Integer seasons) {
        this.seasons = seasons;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "genre=" + genre +
                ", title='" + title + '\'' +
                ", seasons=" + seasons +  '\'' +
                ", rating=" + rating +  '\'' +
                ", actors='" + actors + '\'' +
                ", plot='" + plot + '\'' +
                ", poster='" + poster + '\'' +
                ", episodes='" + episodes;
    }
}
