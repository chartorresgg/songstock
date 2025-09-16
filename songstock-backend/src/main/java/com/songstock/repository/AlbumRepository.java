package com.songstock.repository;

import com.songstock.entity.Album;
import com.songstock.entity.Artist;
import com.songstock.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByTitleContainingIgnoreCase(String title);

    List<Album> findByArtist(Artist artist);

    List<Album> findByGenre(Genre genre);

    List<Album> findByReleaseYear(Integer releaseYear);

    List<Album> findByLabel(String label);

    List<Album> findByIsActive(Boolean isActive);

    @Query("SELECT a FROM Album a WHERE a.title LIKE %:title% AND a.artist.name LIKE %:artistName%")
    List<Album> findByTitleAndArtistName(@Param("title") String title, @Param("artistName") String artistName);

    @Query("SELECT a FROM Album a WHERE a.releaseYear BETWEEN :startYear AND :endYear")
    List<Album> findByReleaseYearBetween(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);

    @Query("SELECT a FROM Album a JOIN a.artist ar WHERE ar.country = :country")
    List<Album> findByArtistCountry(@Param("country") String country);
}