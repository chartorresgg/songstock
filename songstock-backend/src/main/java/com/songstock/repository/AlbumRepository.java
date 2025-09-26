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

/**
 * Repositorio de acceso a datos para la entidad {@link Album}.
 *
 * Proporciona métodos de búsqueda por título, artista, género, año de
 * lanzamiento
 * y consultas personalizadas relacionadas con productos físicos y digitales.
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    /**
     * Busca un álbum por su título exacto (ignorando mayúsculas/minúsculas).
     *
     * @param title Título del álbum.
     * @return {@link Optional} con el álbum si existe.
     */
    Optional<Album> findByTitleIgnoreCase(String title);

    /**
     * Obtiene todos los álbumes activos.
     *
     * @return Lista de álbumes activos.
     */
    List<Album> findByIsActiveTrue();

    /**
     * Obtiene álbumes activos por artista.
     *
     * @param artist Entidad del artista.
     * @return Lista de álbumes activos del artista.
     */
    List<Album> findByArtistAndIsActiveTrue(Artist artist);

    /**
     * Obtiene álbumes activos por ID de artista.
     *
     * @param artistId ID del artista.
     * @return Lista de álbumes activos del artista.
     */
    List<Album> findByArtistIdAndIsActiveTrue(Long artistId);

    /**
     * Obtiene álbumes activos por género.
     *
     * @param genre Entidad del género musical.
     * @return Lista de álbumes activos del género.
     */
    List<Album> findByGenreAndIsActiveTrue(Genre genre);

    /**
     * Obtiene álbumes activos por ID de género.
     *
     * @param genreId ID del género musical.
     * @return Lista de álbumes activos.
     */
    List<Album> findByGenreIdAndIsActiveTrue(Long genreId);

    /**
     * Obtiene todos los álbumes de un género, sin filtrar por estado.
     *
     * @param genreId ID del género.
     * @return Lista de álbumes del género.
     */
    List<Album> findByGenreId(Long genreId);

    /**
     * Obtiene álbumes activos por año de lanzamiento.
     *
     * @param releaseYear Año de lanzamiento.
     * @return Lista de álbumes activos.
     */
    List<Album> findByReleaseYearAndIsActiveTrue(Integer releaseYear);

    /**
     * Obtiene todos los álbumes por año de lanzamiento (sin filtrar por estado).
     *
     * @param releaseYear Año de lanzamiento.
     * @return Lista de álbumes.
     */
    List<Album> findByReleaseYear(Integer releaseYear);

    /**
     * Obtiene álbumes lanzados en un rango de años.
     *
     * @param startYear Año inicial.
     * @param endYear   Año final.
     * @return Lista de álbumes dentro del rango.
     */
    List<Album> findByReleaseYearBetween(Integer startYear, Integer endYear);

    /**
     * Verifica si existe un álbum con un título y artista específicos.
     *
     * @param title    Título del álbum.
     * @param artistId ID del artista.
     * @return true si ya existe, false en caso contrario.
     */
    @Query("SELECT COUNT(a) > 0 FROM Album a WHERE a.title = :title AND a.artist.id = :artistId")
    boolean existsByTitleAndArtistId(@Param("title") String title, @Param("artistId") Long artistId);

    /**
     * Busca álbumes activos por artista y año de lanzamiento.
     *
     * @param artistId ID del artista.
     * @param year     Año de lanzamiento.
     * @return Lista de álbumes que coinciden.
     */
    @Query("SELECT a FROM Album a WHERE a.artist.id = :artistId AND a.releaseYear = :year AND a.isActive = true")
    List<Album> findByArtistIdAndReleaseYear(@Param("artistId") Long artistId, @Param("year") Integer year);

    /**
     * Busca álbumes activos cuyo título contenga un texto parcial.
     *
     * @param title Texto a buscar en el título.
     * @return Lista de álbumes coincidentes.
     */
    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) AND a.isActive = true")
    List<Album> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Busca álbumes activos de un artista cuyo título contenga texto parcial.
     *
     * @param artistId ID del artista.
     * @param title    Texto a buscar en el título.
     * @return Lista de álbumes filtrados.
     */
    @Query("SELECT a FROM Album a WHERE a.artist.id = :artistId AND a.title LIKE %:title% AND a.isActive = true")
    List<Album> findByArtistIdAndTitleContaining(@Param("artistId") Long artistId, @Param("title") String title);

    /**
     * Obtiene todos los álbumes activos con soporte de paginación.
     *
     * @param pageable Objeto de paginación.
     * @return Página de álbumes activos.
     */
    Page<Album> findByIsActiveTrue(Pageable pageable);

    /**
     * Obtiene todos los álbumes de un artista (sin filtrar por estado).
     *
     * @param artistId ID del artista.
     * @return Lista de álbumes.
     */
    List<Album> findByArtistId(Long artistId);

    /**
     * Obtiene álbumes activos de un artista con paginación.
     *
     * @param artistId ID del artista.
     * @param pageable Objeto de paginación.
     * @return Página de álbumes activos.
     */
    Page<Album> findByArtistIdAndIsActiveTrue(Long artistId, Pageable pageable);

    /**
     * Obtiene álbumes activos de un género con paginación.
     *
     * @param genreId  ID del género.
     * @param pageable Objeto de paginación.
     * @return Página de álbumes activos.
     */
    Page<Album> findByGenreIdAndIsActiveTrue(Long genreId, Pageable pageable);

    /**
     * Busca álbumes que tienen productos activos asociados.
     *
     * @return Lista de álbumes con productos.
     */
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithProducts();

    /**
     * Busca álbumes que tienen al menos una versión en vinilo activa.
     *
     * @return Lista de álbumes con vinilos.
     */
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.productType = 'PHYSICAL' AND p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithVinylVersions();

    /**
     * Busca álbumes que tienen al menos una versión digital activa.
     *
     * @return Lista de álbumes con versiones digitales.
     */
    @Query("SELECT DISTINCT a FROM Album a JOIN a.products p WHERE p.productType = 'DIGITAL' AND p.isActive = true AND a.isActive = true")
    List<Album> findAlbumsWithDigitalVersions();

    /**
     * Busca álbumes que tienen tanto versión digital como vinilo activas.
     *
     * @return Lista de álbumes con ambos formatos.
     */
    @Query("SELECT DISTINCT a FROM Album a WHERE " +
            "EXISTS (SELECT p1 FROM Product p1 WHERE p1.album = a AND p1.productType = 'DIGITAL' AND p1.isActive = true) AND "
            +
            "EXISTS (SELECT p2 FROM Product p2 WHERE p2.album = a AND p2.productType = 'PHYSICAL' AND p2.isActive = true) AND "
            +
            "a.isActive = true")
    List<Album> findAlbumsWithBothFormats();

    /**
     * Busca un álbum activo por su número de catálogo.
     *
     * @param catalogNumber Número de catálogo.
     * @return {@link Optional} con el álbum si existe.
     */
    Optional<Album> findByCatalogNumberAndIsActiveTrue(String catalogNumber);

    /**
     * Cuenta la cantidad de productos activos asociados a un álbum.
     *
     * @param albumId ID del álbum.
     * @return Número de productos activos.
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.album.id = :albumId AND p.isActive = true")
    Long countProductsByAlbum(@Param("albumId") Long albumId);

}
