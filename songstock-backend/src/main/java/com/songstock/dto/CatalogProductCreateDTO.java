package com.songstock.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

/**
 * DTO para crear productos en el catálogo del proveedor
 * Incluye campos básicos para vinilo: nombre, artista, año, precio
 */
public class CatalogProductCreateDTO {

    @NotNull(message = "El álbum es obligatorio")
    private Long albumId;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    @NotNull(message = "El tipo de producto es obligatorio")
    private String productType = "PHYSICAL"; // Por defecto PHYSICAL para vinilos

    @NotNull(message = "La condición es obligatoria")
    private String conditionType = "NEW";

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal price;

    @NotNull(message = "La cantidad de stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockQuantity;

    // Campos específicos para vinilos físicos
    private String vinylSize = "12_INCH"; // Por defecto 12 pulgadas
    private String vinylSpeed = "33_RPM"; // Por defecto 33 RPM

    @Min(value = 1, message = "El peso debe ser mayor que 0")
    private Integer weightGrams = 180; // Peso típico de un vinilo

    private Boolean featured = false;

    private String description; // Descripción adicional del proveedor

    // Constructor vacío
    public CatalogProductCreateDTO() {
    }

    // Constructor completo
    public CatalogProductCreateDTO(Long albumId, Long categoryId, String sku,
            BigDecimal price, Integer stockQuantity) {
        this.albumId = albumId;
        this.categoryId = categoryId;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters y Setters
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getVinylSize() {
        return vinylSize;
    }

    public void setVinylSize(String vinylSize) {
        this.vinylSize = vinylSize;
    }

    public String getVinylSpeed() {
        return vinylSpeed;
    }

    public void setVinylSpeed(String vinylSpeed) {
        this.vinylSpeed = vinylSpeed;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CatalogProductCreateDTO{" +
                "albumId=" + albumId +
                ", categoryId=" + categoryId +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", vinylSize='" + vinylSize + '\'' +
                ", vinylSpeed='" + vinylSpeed + '\'' +
                '}';
    }
}