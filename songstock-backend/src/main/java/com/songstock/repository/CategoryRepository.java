package com.songstock.repository;

import com.songstock.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Category}.
 * 
 * Esta interfaz extiende de {@link JpaRepository}, lo que permite
 * realizar operaciones CRUD y consultas sobre la base de datos sin
 * necesidad de implementar manualmente el acceso a datos.
 * 
 * Además, define métodos de consulta personalizados basados en
 * la convención de nombres de Spring Data JPA.
 */
@Repository // Indica que esta interfaz es un componente de persistencia gestionado por
            // Spring.
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param name Nombre de la categoría a buscar.
     * @return Un {@link Optional} que contiene la categoría si existe, o vacío si
     *         no se encuentra.
     */
    Optional<Category> findByName(String name);

    /**
     * Obtiene una lista de categorías filtradas por su estado activo.
     *
     * @param isActive true para categorías activas, false para inactivas.
     * @return Lista de categorías que cumplen con el estado indicado.
     */
    List<Category> findByIsActive(Boolean isActive);

    /**
     * Busca categorías cuyo nombre contenga una subcadena (ignorando
     * mayúsculas/minúsculas).
     *
     * @param name Subcadena a buscar dentro del nombre de las categorías.
     * @return Lista de categorías cuyo nombre contenga la subcadena especificada.
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Verifica si existe una categoría con un nombre dado.
     *
     * @param name Nombre de la categoría a verificar.
     * @return true si existe una categoría con ese nombre, false en caso contrario.
     */
    boolean existsByName(String name);
}
