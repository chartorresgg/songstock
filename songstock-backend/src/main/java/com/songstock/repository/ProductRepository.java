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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

import java.util.List;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        // Buscar por SKU
        Optional<Product> findBySkuAndIsActiveTrue(String sku);

        // Buscar por álbum
        List<Product> findByAlbumAndIsActiveTrue(Album album);

        // Buscar por álbum ID
        List<Product> findByAlbumIdAndIsActiveTrue(Long albumId);

        // Buscar por rango de precios
        @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
        List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice);

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
        List<Product> findAlternativeFormats(@Param("albumId") Long albumId,
                        @Param("currentType") ProductType currentType);

        // Buscar todos los formatos disponibles de un álbum
        @Query("SELECT p FROM Product p WHERE p.album.id = :albumId AND p.isActive = true ORDER BY p.productType, p.price")
        List<Product> findAllFormatsByAlbumId(@Param("albumId") Long albumId);

        // CONSULTAS ESPECÍFICAS PARA VINILOS

        // Buscar por tamaño de vinilo
        List<Product> findByVinylSizeAndIsActiveTrue(VinylSize vinylSize);

        // Buscar por velocidad de vinilo
        List<Product> findByVinylSpeedAndIsActiveTrue(VinylSpeed vinylSpeed);

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

        /**
         * Verificar si existe un producto con el SKU dado
         */
        boolean existsBySku(String sku);

        /**
         * Encontrar todos los productos de un proveedor específico
         */
        List<Product> findByProviderId(Long providerId);

        /**
         * Encontrar productos destacados de un proveedor
         */
        List<Product> findByProviderIdAndFeaturedTrue(Long providerId);

        /**
         * Encontrar productos destacados y activos de un proveedor
         */
        List<Product> findByProviderIdAndFeaturedTrueAndIsActiveTrue(Long providerId);

        /**
         * Buscar productos por título de álbum o nombre de artista
         */
        @Query("SELECT p FROM Product p JOIN p.album a JOIN a.artist ar " +
                        "WHERE (LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(ar.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND p.isActive = true")
        List<Product> searchByAlbumOrArtist(@Param("query") String query);

        /**
         * Encontrar productos por categoría
         */
        List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

        /**
         * Encontrar productos por artista (a través del álbum)
         */
        @Query("SELECT p FROM Product p JOIN p.album a WHERE a.artist.id = :artistId AND p.isActive = true")
        List<Product> findByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId);

        /**
         * Encontrar productos por tipo de producto
         */
        List<Product> findByProductTypeAndIsActiveTrue(ProductType productType);

        /**
         * Encontrar productos por condición
         */
        List<Product> findByConditionTypeAndIsActiveTrue(ConditionType conditionType);

        /**
         * Encontrar productos con precio mayor o igual
         */
        List<Product> findByPriceGreaterThanEqualAndIsActiveTrue(BigDecimal minPrice);

        /**
         * Encontrar productos con precio menor o igual
         */
        List<Product> findByPriceLessThanEqualAndIsActiveTrue(BigDecimal maxPrice);

        /**
         * Encontrar productos por año de lanzamiento (a través del álbum)
         */
        @Query("SELECT p FROM Product p JOIN p.album a WHERE a.releaseYear BETWEEN :minYear AND :maxYear AND p.isActive = true")
        List<Product> findByReleaseYearBetweenAndIsActiveTrue(@Param("minYear") Integer minYear,
                        @Param("maxYear") Integer maxYear);

        /**
         * Encontrar productos por año específico
         */
        @Query("SELECT p FROM Product p JOIN p.album a WHERE a.releaseYear = :year AND p.isActive = true")
        List<Product> findByReleaseYearAndIsActiveTrue(@Param("year") Integer year);

        /**
         * Encontrar productos sin stock
         */
        List<Product> findByStockQuantityEqualsAndIsActiveTrue(Integer stock);

        /**
         * Encontrar productos destacados con stock
         */
        List<Product> findByFeaturedTrueAndStockQuantityGreaterThanAndIsActiveTrue(Integer minStock);

        /**
         * Encontrar productos destacados activos
         */
        List<Product> findByFeaturedTrueAndIsActiveTrue();

        /**
         * Contar productos por proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId")
        Long countByProviderId(@Param("providerId") Long providerId);

        /**
         * Contar productos por proveedor y estado
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = :isActive")
        Long countByProviderIdAndIsActive(@Param("providerId") Long providerId, @Param("isActive") Boolean isActive);

        /**
         * Contar productos con stock por proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.stockQuantity > 0")
        Long countInStockByProviderId(@Param("providerId") Long providerId);

        /**
         * Contar productos sin stock por proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.stockQuantity = 0")
        Long countOutOfStockByProviderId(@Param("providerId") Long providerId);

        /**
         * Contar productos por tipo y proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.productType = :productType")
        Long countByProviderIdAndProductType(@Param("providerId") Long providerId,
                        @Param("productType") ProductType productType);

        /**
         * Contar productos por condición y proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.conditionType = :conditionType")
        Long countByProviderIdAndConditionType(@Param("providerId") Long providerId,
                        @Param("conditionType") ConditionType conditionType);

        /**
         * Obtener suma del valor total del inventario por proveedor (precio * stock)
         */
        @Query("SELECT COALESCE(SUM(p.price * p.stockQuantity), 0) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
        BigDecimal getTotalInventoryValueByProviderId(@Param("providerId") Long providerId);

        /**
         * Buscar productos con filtros múltiples usando JPQL (más portable que SQL
         * nativo)
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.album a " +
                        "JOIN a.artist ar " +
                        "LEFT JOIN a.genre g " +
                        "WHERE (:searchQuery IS NULL OR :searchQuery = '' OR " +
                        "       LOWER(a.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(ar.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
                        "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
                        "AND (:genreId IS NULL OR g.id = :genreId) " +
                        "AND (:artistId IS NULL OR ar.id = :artistId) " +
                        "AND (:productType IS NULL OR p.productType = :productType) " +
                        "AND (:conditionType IS NULL OR p.conditionType = :conditionType) " +
                        "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                        "AND (:minYear IS NULL OR a.releaseYear >= :minYear) " +
                        "AND (:maxYear IS NULL OR a.releaseYear <= :maxYear) " +
                        "AND (:inStockOnly = false OR p.stockQuantity > 0) " +
                        "AND (:featuredOnly = false OR p.featured = true) " +
                        "AND (:activeOnly = false OR p.isActive = true)")
        List<Product> findWithFilters(@Param("searchQuery") String searchQuery,
                        @Param("categoryId") Long categoryId,
                        @Param("genreId") Long genreId,
                        @Param("artistId") Long artistId,
                        @Param("productType") ProductType productType,
                        @Param("conditionType") ConditionType conditionType,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("minYear") Integer minYear,
                        @Param("maxYear") Integer maxYear,
                        @Param("inStockOnly") Boolean inStockOnly,
                        @Param("featuredOnly") Boolean featuredOnly,
                        @Param("activeOnly") Boolean activeOnly);

        /**
         * Buscar productos de un proveedor específico con filtros
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.album a " +
                        "JOIN a.artist ar " +
                        "LEFT JOIN a.genre g " +
                        "WHERE p.provider.id = :providerId " +
                        "AND (:searchQuery IS NULL OR :searchQuery = '' OR " +
                        "     LOWER(a.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "     LOWER(ar.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
                        "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
                        "AND (:productType IS NULL OR p.productType = :productType) " +
                        "AND (:conditionType IS NULL OR p.conditionType = :conditionType) " +
                        "AND (:inStockOnly = false OR p.stockQuantity > 0) " +
                        "AND (:featuredOnly = false OR p.featured = true) " +
                        "AND (:activeOnly = false OR p.isActive = true)")
        List<Product> findProviderProductsWithFilters(@Param("providerId") Long providerId,
                        @Param("searchQuery") String searchQuery,
                        @Param("categoryId") Long categoryId,
                        @Param("productType") ProductType productType,
                        @Param("conditionType") ConditionType conditionType,
                        @Param("inStockOnly") Boolean inStockOnly,
                        @Param("featuredOnly") Boolean featuredOnly,
                        @Param("activeOnly") Boolean activeOnly);

        /**
         * Obtener productos más recientes
         */
        @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
        List<Product> findRecentProducts(Pageable pageable);

        /**
         * Obtener productos más populares (por featured y stock)
         */
        @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity > 0 " +
                        "ORDER BY p.featured DESC, p.stockQuantity DESC, p.createdAt DESC")
        List<Product> findPopularProducts(Pageable pageable);

        /**
         * Encontrar productos activos
         */
        List<Product> findByIsActiveTrue();

        /**
         * Encontrar productos activos de un proveedor
         */
        List<Product> findByProviderIdAndIsActiveTrue(Long providerId);

        /**
         * Encontrar productos por género (a través del álbum)
         */
        @Query("SELECT p FROM Product p JOIN p.album a WHERE a.genre.id = :genreId AND p.isActive = true")
        List<Product> findByGenreIdAndIsActiveTrue(@Param("genreId") Long genreId);

        /**
         * Encontrar productos en rango de precio
         */
        List<Product> findByPriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

        /**
         * Encontrar productos con stock disponible
         */
        List<Product> findByStockQuantityGreaterThanAndIsActiveTrue(Integer minStock);

        /**
         * Contar productos destacados por proveedor
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.provider.id = :providerId AND p.featured = true")
        Long countFeaturedByProviderId(@Param("providerId") Long providerId);

        /**
         * Obtener precio promedio de productos de un proveedor
         */
        @Query("SELECT AVG(p.price) FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
        BigDecimal getAveragePriceByProviderId(@Param("providerId") Long providerId);

        /**
         * Buscar productos con filtros múltiples (consulta nativa para mayor
         * flexibilidad)
         */
        @Query(value = "SELECT p.* FROM products p " +
                        "JOIN albums a ON p.album_id = a.id " +
                        "JOIN artists ar ON a.artist_id = ar.id " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "WHERE (:searchQuery IS NULL OR " +
                        "       LOWER(a.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "       LOWER(ar.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
                        "AND (:categoryId IS NULL OR p.category_id = :categoryId) " +
                        "AND (:genreId IS NULL OR a.genre_id = :genreId) " +
                        "AND (:productType IS NULL OR p.product_type = :productType) " +
                        "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                        "AND (:minYear IS NULL OR a.release_year >= :minYear) " +
                        "AND (:maxYear IS NULL OR a.release_year <= :maxYear) " +
                        "AND (:inStockOnly = false OR p.stock_quantity > 0) " +
                        "AND (:featuredOnly = false OR p.featured = true) " +
                        "AND (:activeOnly = false OR p.is_active = true) " +
                        "ORDER BY " +
                        "CASE WHEN :sortBy = 'price' AND :sortDirection = 'asc' THEN p.price END ASC, " +
                        "CASE WHEN :sortBy = 'price' AND :sortDirection = 'desc' THEN p.price END DESC, " +
                        "CASE WHEN :sortBy = 'createdAt' AND :sortDirection = 'asc' THEN p.created_at END ASC, " +
                        "CASE WHEN :sortBy = 'createdAt' AND :sortDirection = 'desc' THEN p.created_at END DESC, " +
                        "p.created_at DESC", nativeQuery = true)
        List<Product> findWithFilters(@Param("searchQuery") String searchQuery,
                        @Param("categoryId") Long categoryId,
                        @Param("genreId") Long genreId,
                        @Param("productType") String productType,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("minYear") Integer minYear,
                        @Param("maxYear") Integer maxYear,
                        @Param("inStockOnly") Boolean inStockOnly,
                        @Param("featuredOnly") Boolean featuredOnly,
                        @Param("activeOnly") Boolean activeOnly,
                        @Param("sortBy") String sortBy,
                        @Param("sortDirection") String sortDirection);

        /**
         * Contar productos por estado activo
         */
        Long countByIsActive(Boolean isActive);

}