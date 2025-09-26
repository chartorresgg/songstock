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

/**
 * Repositorio de acceso a datos para la entidad {@link Artist}.
 * 
 * Extiende de {@link JpaRepository}, proporcionando operaciones CRUD
 * y consultas personalizadas para gestionar artistas musicales.
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /**
     * Busca un artista por su nombre exacto (ignorando mayúsculas/minúsculas).
     *
     * @param name Nombre del artista.
     * @return {@link Optional} con el artista encontrado o vacío si no existe.
     */
    Optional<Artist> findByNameIgnoreCase(String name);

    /**
     * Obtiene todos los artistas que están activos.
     *
     * @return Lista de artistas activos.
     */
    List<Artist> findByIsActiveTrue();

    /**
     * Obtiene todos los artistas activos que pertenecen a un país específico.
     *
     * @param country País de los artistas.
     * @return Lista de artistas activos de ese país.
     */
    List<Artist> findByCountryIgnoreCaseAndIsActiveTrue(String country);

    /**
     * Busca artistas activos por año de formación.
     *
     * @param formedYear Año de formación de la banda o artista.
     * @return Lista de artistas activos con ese año de formación.
     */
    List<Artist> findByFormedYearAndIsActiveTrue(Integer formedYear);

    /**
     * Busca artistas cuyo nombre contenga una cadena dada (búsqueda parcial).
     *
     * @param name Fragmento del nombre del artista.
     * @return Lista de artistas activos que contienen el texto en su nombre.
     */
    @Query("SELECT a FROM Artist a WHERE a.name LIKE %:name% AND a.isActive = true")
    List<Artist> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtiene artistas activos con soporte de paginación.
     *
     * @param pageable Objeto de paginación.
     * @return Página con artistas activos.
     */
    Page<Artist> findByIsActiveTrue(Pageable pageable);

    /**
     * Busca artistas activos por país con paginación.
     *
     * @param country  País de los artistas.
     * @param pageable Parámetros de paginación.
     * @return Página con artistas activos del país indicado.
     */
    Page<Artist> findByCountryIgnoreCaseAndIsActiveTrue(String country, Pageable pageable);

    /**
     * Cuenta la cantidad de artistas activos en un país específico.
     *
     * @param country País a evaluar.
     * @return Número de artistas activos en el país.
     */
    @Query("SELECT COUNT(a) FROM Artist a WHERE a.country = :country AND a.isActive = true")
    Long countByCountryAndIsActive(@Param("country") String country);

    /**
     * Obtiene artistas que tienen al menos un álbum activo asociado.
     *
     * @return Lista de artistas con álbumes activos.
     */
    @Query("SELECT DISTINCT a FROM Artist a JOIN a.albums alb WHERE alb.isActive = true AND a.isActive = true")
    List<Artist> findArtistsWithAlbums();
}
