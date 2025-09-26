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

/**
 * Repositorio para la entidad {@link Genre}.
 *
 * Extiende de {@link JpaRepository} para proporcionar operaciones CRUD básicas
 * y consultas automáticas. Además, define consultas personalizadas para manejar
 * la lógica específica de géneros en el sistema.
 */
@Repository // Indica que esta interfaz es un componente de persistencia gestionado por
            // Spring.
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Busca un género por su nombre exacto, ignorando mayúsculas/minúsculas.
     *
     * @param name Nombre del género a buscar.
     * @return Un {@link Optional} que contiene el género si existe, o vacío en caso
     *         contrario.
     */
    Optional<Genre> findByNameIgnoreCase(String name);

    /**
     * Obtiene todos los géneros que están activos.
     *
     * @return Lista de géneros activos.
     */
    List<Genre> findByIsActiveTrue();

    /**
     * Busca géneros cuyo nombre contenga una subcadena, solo entre los que están
     * activos.
     * 
     * Esta consulta usa JPQL para filtrar resultados de forma explícita.
     *
     * @param name Subcadena a buscar dentro del nombre del género.
     * @return Lista de géneros activos cuyo nombre contiene la subcadena indicada.
     */
    @Query("SELECT g FROM Genre g WHERE g.name LIKE %:name% AND g.isActive = true")
    List<Genre> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Obtiene todos los géneros activos con soporte para paginación.
     *
     * @param pageable Parámetros de paginación (página, tamaño, ordenamiento).
     * @return Página de géneros activos.
     */
    Page<Genre> findByIsActiveTrue(Pageable pageable);

    /**
     * Busca géneros que tengan al menos un álbum activo asociado.
     *
     * @return Lista de géneros que contienen álbumes activos.
     */
    @Query("SELECT DISTINCT g FROM Genre g JOIN g.albums a WHERE a.isActive = true AND g.isActive = true")
    List<Genre> findGenresWithAlbums();

    /**
     * Cuenta el número de álbumes activos asociados a un género específico.
     *
     * @param genreId Identificador del género.
     * @return Número de álbumes activos asociados al género.
     */
    @Query("SELECT COUNT(a) FROM Album a WHERE a.genre.id = :genreId AND a.isActive = true")
    Long countAlbumsByGenre(@Param("genreId") Long genreId);
}
