package com.songstock.repository;

import com.songstock.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    // Buscar por nombre exacto
    Optional<Genre> findByNameIgnoreCase(String name);
    
    // Buscar activos
    List<Genre> findByIsActiveTrue();
    
    // Buscar por nombre que contenga
    @Query("SELECT g FROM Genre g WHERE g.name LIKE %:name% AND g.isActive = true")
    List<Genre> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Buscar con paginación
    Page<Genre> findByIsActiveTrue(Pageable pageable);
    
    // Géneros con álbumes
    @Query("SELECT DISTINCT g FROM Genre g JOIN g.albums a WHERE a.isActive = true AND g.isActive = true")
    List<Genre> findGenresWithAlbums();
    
    // Contar álbumes por género
    @Query("SELECT COUNT(a) FROM Album a WHERE a.genre.id = :genreId AND a.isActive = true")
    Long countAlbumsByGenre(@Param("genreId") Long genreId);
}