package com.songstock.repository;

import com.songstock.entity.Product;
import com.songstock.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link ProductImage}.
 *
 * Proporciona métodos para consultar, contar y verificar imágenes asociadas
 * a productos. Incluye tanto consultas derivadas de nombres de métodos como
 * consultas personalizadas con JPQL.
 */
@Repository // Marca la interfaz como un componente de acceso a datos gestionado por Spring.
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Obtiene todas las imágenes asociadas a un producto específico,
     * ordenadas por el campo {@code displayOrder}.
     *
     * @param product Entidad del producto.
     * @return Lista de imágenes del producto, ordenadas por posición.
     */
    List<ProductImage> findByProductOrderByDisplayOrder(Product product);

    /**
     * Obtiene todas las imágenes de un producto específico a partir de su ID,
     * ordenadas por {@code displayOrder}.
     *
     * @param productId Identificador del producto.
     * @return Lista de imágenes del producto, ordenadas por posición.
     */
    List<ProductImage> findByProductIdOrderByDisplayOrder(Long productId);

    /**
     * Busca la imagen principal de un producto a partir de su ID.
     *
     * @param productId Identificador del producto.
     * @return {@link Optional} que contiene la imagen principal, si existe.
     */
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    /**
     * Obtiene todas las imágenes de un producto que no son principales,
     * ordenadas por {@code displayOrder}.
     *
     * @param productId Identificador del producto.
     * @return Lista de imágenes no principales del producto.
     */
    List<ProductImage> findByProductIdAndIsPrimaryFalseOrderByDisplayOrder(Long productId);

    /**
     * Cuenta el número total de imágenes asociadas a un producto.
     *
     * @param productId Identificador del producto.
     * @return Cantidad de imágenes del producto.
     */
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    /**
     * Verifica si un producto tiene una imagen principal asociada.
     *
     * @param productId Identificador del producto.
     * @return {@code true} si el producto tiene una imagen principal,
     *         {@code false} en caso contrario.
     */
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    boolean hasMainImage(@Param("productId") Long productId);
}
