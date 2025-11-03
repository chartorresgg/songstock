package com.songstock.repository;

import com.songstock.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByAlbumIdAndIsActiveTrue(Long albumId);

    // Búsqueda de canciones por título (parcial, case insensitive)
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) AND s.isActive = true")
    List<Song> searchByTitle(String query);

    // Búsqueda avanzada: título o álbum o artista
    @Query("SELECT s FROM Song s WHERE (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.album.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.album.artist.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND s.isActive = true")
    List<Song> searchByTitleOrAlbumOrArtist(String query);
}