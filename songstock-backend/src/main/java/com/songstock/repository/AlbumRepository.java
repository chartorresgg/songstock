package com.songstock.repository;

import com.songstock.entity.Album;
import com.songstock.entity.Artist;
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
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Buscar por título exacto
    Optional<Album> findByTitleIgnoreCase(String title);

    // Buscar activos
    List<Album> findByIsActiveTrue();

    // Buscar por artista
    List<Album> findByArtistAndIsActiveTrue(Artist artist);

    // Buscar por artista ID
    List<Album> findByArtistIdAndIsActiveTrue(Long artistId);

    // Buscar por género
    List<Album> findByGenreAndIsActiveTrue(Genre genre);

    // Buscar por género ID
    List<Album> findByGenreIdAndIsActiveTrue(Long genreId);

    /**
     * Encontrar álbumes por género
     */
    List<Album> findByGenreId(Long genreId);

    // Buscar por año de lanzamiento
    List<Album> findByReleaseYearAndIsActiveTrue(Integer releaseYear);

    /**
     * Encontrar álbumes por año de lanzamiento
     */
    List<Album> findByReleaseYear(Integer releaseYear);

    /**
     * Encontrar álbumes en rango de años
     */
    List<Album> findByReleaseYearBetween(Integer startYear, Integer endYear);

    /**
     * Verificar si existe un álbum con el mismo título y artista
     */
    @Query("SELECT COUNT(a) > 0 FROM Album a WHERE a.title = :title AND a.artist.id = :artistId")
    boolean existsByTitleAndArtistId(@Param("title") String title, @Param("artistId") Long artistId);

    /**
     * Buscar álbumes por artista y año
     */
    @Query("SELECT a FROM Album a WHERE a.artist.id = :artistId AND a.releaseYear = :year AND a.isActive = true")
    List<Album> findByArtistIdAndReleaseYear(@Param("artistId") Long artistId, @Param("year") Integer year);

    /**
     * Buscar álbumes por título (búsqueda parcial)
     */
    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) AND a.isActive = true")
    List<Album> findByTitleContainingIgnoreCase(@Param("title") String title);

    // Buscar por artista y título
    @Query("SELECT a FROM Album a WHERE a.artist.id = :artistId AND a.title LIKE %:title% AND a.isActive = true")
    List<Album> findByArtistIdAndTitleContaining(@Param("artistId") Long artistId, @Param("title") String title);

    // Buscar con paginación
    Page<Album> findByIsActiveTrue(Pageable pageable);

    /**
     * Encontrar álbumes por artista
     */
    List<Album> findByArtistId(Long artistId);

    // Buscar por artista con paginación
    Page<Album> findByArtistIdAndIsActiveTrue(Long artistId, Pageable pageable);

    // Buscar por género con paginación
    Page<Album> findByGenreIdAndIsActiveTrue(Long genreId, Pageable pageable);

    // Álbumes con productos (que tienen versiones físicas o digitales)
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithProducts();

    // Álbumes con versiones en vinilo
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.productType = 'PHYSICAL' AND p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithVinylVersions();

    // Álbumes con versiones digitales
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.productType = 'DIGITAL' AND p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithDigitalVersions();

    // Buscar álbumes que tienen AMBAS versiones (digital Y vinilo)
    @Query("SELECT DISTINCT a FROM Album a WHERE " +
            "EXISTS (SELECT p1 FROM Product p1 WHERE p1.album = a AND p1.productType = 'DIGITAL' AND p1.isActive = true) AND "
            +
            "EXISTS (SELECT p2 FROM Product p2 WHERE p2.album = a AND p2.productType = 'PHYSICAL' AND p2.isActive = true) AND "
            +
            "a.isActive = true")
    List<Album> findAlbumsWithBothFormats();

    // Buscar por número de catálogo
    Optional<Album> findByCatalogNumberAndIsActiveTrue(String catalogNumber);

    // Contar productos por álbum
    @Query("SELECT COUNT(p) FROM Product p WHERE p.album.id = :albumId AND p.isActive = true")
    Long countProductsByAlbum(@Param("albumId") Long albumId);

}