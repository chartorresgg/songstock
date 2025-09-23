package com.songstock.repository;

import com.songstock.entity.Product;
import com.songstock.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    // Buscar por producto
    List<ProductImage> findByProductOrderByDisplayOrder(Product product);
    
    // Buscar por producto ID
    List<ProductImage> findByProductIdOrderByDisplayOrder(Long productId);
    
    // Buscar imagen principal de un producto
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    
    // Buscar imágenes no principales
    List<ProductImage> findByProductIdAndIsPrimaryFalseOrderByDisplayOrder(Long productId);
    
    // Contar imágenes por producto
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    // Verificar si producto tiene imagen principal
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    boolean hasMainImage(@Param("productId") Long productId);
}