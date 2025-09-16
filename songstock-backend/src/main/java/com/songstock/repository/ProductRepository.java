package com.songstock.repository;

import com.songstock.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByAlbum(Album album);

    List<Product> findByProvider(Provider provider);

    List<Product> findByCategory(Category category);

    List<Product> findByProductType(ProductType productType);

    List<Product> findByConditionType(ConditionType conditionType);

    List<Product> findByIsActive(Boolean isActive);

    List<Product> findByFeatured(Boolean featured);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    @Query("SELECT p FROM Product p JOIN p.album a WHERE a.title LIKE %:title%")
    List<Product> findByAlbumTitle(@Param("title") String title);

    @Query("SELECT p FROM Product p JOIN p.album a JOIN a.artist ar WHERE ar.name LIKE %:artistName%")
    List<Product> findByArtistName(@Param("artistName") String artistName);

    @Query("SELECT p FROM Product p WHERE p.provider.id = :providerId AND p.isActive = true")
    List<Product> findActiveProductsByProvider(@Param("providerId") Long providerId);

    @Query("SELECT p FROM Product p WHERE p.vinylSize = :size AND p.productType = 'PHYSICAL'")
    List<Product> findByVinylSize(@Param("size") VinylSize size);

    @Query("SELECT p FROM Product p WHERE p.vinylSpeed = :speed AND p.productType = 'PHYSICAL'")
    List<Product> findByVinylSpeed(@Param("speed") VinylSpeed speed);

    boolean existsBySku(String sku);
}