package com.songstock.dto;

import com.songstock.entity.ProductType;
import com.songstock.entity.ConditionType;
import com.songstock.entity.VinylSize;
import com.songstock.entity.VinylSpeed;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO para crear productos en el catálogo
 * Usado por proveedores para agregar nuevos vinilos a su catálogo
 */
public class ProductCatalogCreateDTO {

    @NotNull(message = "El ID del álbum es obligatorio")
    private Long albumId;

    @JsonProperty("providerId")
    private Long providerId; // Opcional: solo usado por ADMIN

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    @NotNull(message = "El tipo de producto es obligatorio")
    private ProductType productType;

    @NotNull(message = "El tipo de condición es obligatorio")
    private ConditionType conditionType;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockQuantity;

    // Campos específicos para vinilos físicos
    private VinylSize vinylSize;
    private VinylSpeed vinylSpeed;
    private Integer weightGrams;

    // Campos específicos para productos digitales
    private String fileFormat; // MP3, FLAC, WAV
    private Integer fileSizeMb;

    private Boolean featured = false;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    // Constructor vacío
    public ProductCatalogCreateDTO() {
    }

    // Constructor con campos básicos
    public ProductCatalogCreateDTO(Long albumId, Long categoryId, String sku,
            ProductType productType, ConditionType conditionType,
            BigDecimal price, Integer stockQuantity) {
        this.albumId = albumId;
        this.categoryId = categoryId;
        this.sku = sku;
        this.productType = productType;
        this.conditionType = conditionType;
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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
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

    public VinylSize getVinylSize() {
        return vinylSize;
    }

    public void setVinylSize(VinylSize vinylSize) {
        this.vinylSize = vinylSize;
    }

    public VinylSpeed getVinylSpeed() {
        return vinylSpeed;
    }

    public void setVinylSpeed(VinylSpeed vinylSpeed) {
        this.vinylSpeed = vinylSpeed;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(Integer fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
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
        return "ProductCatalogCreateDTO{" +
                "albumId=" + albumId +
                ", categoryId=" + categoryId +
                ", sku='" + sku + '\'' +
                ", productType=" + productType +
                ", conditionType=" + conditionType +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", featured=" + featured +
                '}';
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}