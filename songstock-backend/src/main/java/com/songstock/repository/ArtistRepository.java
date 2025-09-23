package com.songstock.repository;

import com.songstock.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    // Buscar por nombre exacto
    Optional<Artist> findByNameIgnoreCase(String name);
    
    // Buscar activos
    List<Artist> findByIsActiveTrue();
    
    // Buscar por país
    List<Artist> findByCountryIgnoreCaseAndIsActiveTrue(String country);
    
    // Buscar por año de formación
    List<Artist> findByFormedYearAndIsActiveTrue(Integer formedYear);
    
    // Buscar por nombre que contenga (para búsquedas parciales)
    @Query("SELECT a FROM Artist a WHERE a.name LIKE %:name% AND a.isActive = true")
    List<Artist> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Buscar con paginación
    Page<Artist> findByIsActiveTrue(Pageable pageable);
    
    // Buscar artistas por país con paginación
    Page<Artist> findByCountryIgnoreCaseAndIsActiveTrue(String country, Pageable pageable);
    
    // Contar artistas por país
    @Query("SELECT COUNT(a) FROM Artist a WHERE a.country = :country AND a.isActive = true")
    Long countByCountryAndIsActive(@Param("country") String country);
    
    // Artistas con álbumes
    @Query("SELECT DISTINCT a FROM Artist a JOIN a.albums alb WHERE alb.isActive = true AND a.isActive = true")
    List<Artist> findArtistsWithAlbums();
}