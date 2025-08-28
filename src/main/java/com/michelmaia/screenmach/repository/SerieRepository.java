package com.michelmaia.screenmach.repository;

import com.michelmaia.screenmach.model.Episode;
import com.michelmaia.screenmach.model.Genre;
import com.michelmaia.screenmach.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTitleContainingIgnoreCase(String title);
    Optional<List<Serie>> findByActorsContainingIgnoreCase(String actor);
    List<Serie> findTop5ByOrderByRatingDesc();
    List<Serie> findByGenre(Genre genre);
    List<Serie> findBySeasonsGreaterThanEqualAndRatingGreaterThanEqual (int seasons, Double rating);

    @Query("SELECT s FROM Serie s WHERE s.seasons >= :seasons AND s.rating >= :rating ORDER BY s.rating DESC, s.seasons DESC")
    List<Serie> seriesBySeasonsAndRating(int seasons, Double rating);

    @Query("SELECT e FROM Serie s JOIN s.episodes e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Episode> episodesByTitleKeyword(String keyword);

    @Query("SELECT e FROM Serie s JOIN s.episodes e WHERE s = :serie AND YEAR(e.released) >= :year")
    List<Episode> episodesByYear(Serie serie, int year);
}
