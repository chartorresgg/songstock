package com.songstock.repository;

import com.songstock.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Buscar por SKU
    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    // Buscar activos
    List<Product> findByIsActiveTrue();

    // Buscar por tipo de producto
    List<Product> findByProductTypeAndIsActiveTrue(ProductType productType);

    // Buscar por álbum
    List<Product> findByAlbumAndIsActiveTrue(Album album);

    // Buscar por álbum ID
    List<Product> findByAlbumIdAndIsActiveTrue(Long albumId);

    // Buscar por categoría
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    // Buscar productos destacados
    List<Product> findByFeaturedTrueAndIsActiveTrue();

    // Buscar por rango de precios
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Buscar productos en stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.isActive = true")
    List<Product> findInStockProducts();

    // Buscar productos con bajo stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.stockQuantity <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // Buscar productos sin stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0 AND p.isActive = true")
    List<Product> findOutOfStockProducts();

    // Buscar con paginación
    Page<Product> findByIsActiveTrue(Pageable pageable);

    // Buscar por tipo con paginación
    Page<Product> findByProductTypeAndIsActiveTrue(ProductType productType, Pageable pageable);

    // Buscar por álbum con paginación
    Page<Product> findByAlbumIdAndIsActiveTrue(Long albumId, Pageable pageable);

    // CONSULTAS ESPECÍFICAS PARA LA HISTORIA DE USUARIO

    // Buscar versiones digitales de un álbum
    @Query("SELECT p FROM Product p WHERE p.album.id = :albumId AND p.productType = 'DIGITAL' AND p.isActive = true")
    List<Product> findDigitalVersionsByAlbumId(@Param("albumId") Long albumId);

    // Buscar versiones físicas (vinilo) de un álbum
    @Query("SELECT p FROM Product p WHERE p.album.id = :albumId AND p.productType = 'PHYSICAL' AND p.isActive = true")
    List<Product> findVinylVersionsByAlbumId(@Param("albumId") Long albumId);

    // Verificar si un álbum tiene versión en vinilo
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.album.id = :albumId AND p.productType = 'PHYSICAL' AND p.isActive = true")
    boolean hasVinylVersion(@Param("albumId") Long albumId);

    // Verificar si un álbum tiene versión digital
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.album.id = :albumId AND p.productType = 'DIGITAL' AND p.isActive = true")
    boolean hasDigitalVersion(@Param("albumId") Long albumId);

    // Buscar productos alternativos por álbum (diferente tipo)
    @Query("SELECT p FROM Product p WHERE p.album.id = :albumId AND p.productType != :currentType AND p.isActive = true")
    List<Product> findAlternativeFormats(@Param("albumId") Long albumId, @Param("currentType") ProductType currentType);

    // Buscar todos los formatos disponibles de un álbum
    @Query("SELECT p FROM Product p WHERE p.album.id = :albumId AND p.isActive = true ORDER BY p.productType, p.price")
    List<Product> findAllFormatsByAlbumId(@Param("albumId") Long albumId);

    // CONSULTAS ESPECÍFICAS PARA VINILOS

    // Buscar por tamaño de vinilo
    List<Product> findByVinylSizeAndIsActiveTrue(VinylSize vinylSize);

    // Buscar por velocidad de vinilo
    List<Product> findByVinylSpeedAndIsActiveTrue(VinylSpeed vinylSpeed);

    // Buscar por condición
    List<Product> findByConditionTypeAndIsActiveTrue(ConditionType conditionType);

    // CONSULTAS ESPECÍFICAS PARA DIGITALES

    // Buscar por formato de archivo
    List<Product> findByFileFormatIgnoreCaseAndIsActiveTrue(String fileFormat);

    // CONSULTAS DE ESTADÍSTICAS

    // Contar productos por tipo
    @Query("SELECT COUNT(p) FROM Product p WHERE p.productType = :productType AND p.isActive = true")
    Long countByProductType(@Param("productType") ProductType productType);

    // Contar productos por proveedor
    @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
    Long countByProvider(@Param("providerId") Long providerId);

    // Valor total del inventario por proveedor
    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
    BigDecimal getTotalInventoryValueByProvider(@Param("providerId") Long providerId);

    // Productos más populares (aquí asumiríamos que hay una tabla de ventas, por
    // ahora ordenamos por featured)
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.featured DESC, p.createdAt DESC")
    List<Product> findPopularProducts(Pageable pageable);

    /**
     * Encontrar todos los productos activos de un proveedor específico
     */
    List<Product> findByProviderIdAndIsActiveTrue(Long providerId);

    /**
     * Encontrar productos de un proveedor con stock menor o igual al especificado
     */
    List<Product> findByProviderIdAndIsActiveTrueAndStockQuantityLessThanEqual(Long providerId, Integer maxStock);

    /**
     * Encontrar productos de un proveedor sin stock (stock = 0)
     */
    @Query("SELECT p FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true AND p.stockQuantity = 0")
    List<Product> findOutOfStockProductsByProvider(@Param("providerId") Long providerId);

    /**
     * Encontrar productos de un proveedor con stock bajo (menor al mínimo
     * especificado)
     */
    @Query("SELECT p FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true AND p.stockQuantity <= :minStock AND p.stockQuantity > 0")
    List<Product> findLowStockProductsByProvider(@Param("providerId") Long providerId,
            @Param("minStock") Integer minStock);

    /**
     * Contar total de productos de un proveedor
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
    Long countActiveProductsByProvider(@Param("providerId") Long providerId);

    /**
     * Sumar total de unidades en stock de un proveedor
     */
    @Query("SELECT COALESCE(SUM(p.stockQuantity), 0) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
    Long sumStockByProvider(@Param("providerId") Long providerId);
}