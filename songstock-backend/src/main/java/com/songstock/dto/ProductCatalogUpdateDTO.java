package com.songstock.dto;

import com.songstock.entity.ConditionType;
import com.songstock.entity.VinylSize;
import com.songstock.entity.VinylSpeed;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO para actualizar productos del catálogo
 * Permite modificar información de productos existentes
 */
public class ProductCatalogUpdateDTO {

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    private ConditionType conditionType;

    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockQuantity;

    // Campos específicos para vinilos físicos
    private VinylSize vinylSize;
    private VinylSpeed vinylSpeed;
    private Integer weightGrams;

    // Campos específicos para productos digitales
    private String fileFormat;
    private Integer fileSizeMb;

    private Boolean featured;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    private String updateReason; // Razón del cambio (opcional)

    // Constructor vacío
    public ProductCatalogUpdateDTO() {
    }

    // Constructor con campos básicos
    public ProductCatalogUpdateDTO(String sku, ConditionType conditionType,
            BigDecimal price, Integer stockQuantity) {
        this.sku = sku;
        this.conditionType = conditionType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters y Setters
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public String getUpdateReason() {
        return updateReason;
    }

    public void setUpdateReason(String updateReason) {
        this.updateReason = updateReason;
    }

    @Override
    public String toString() {
        return "ProductCatalogUpdateDTO{" +
                "sku='" + sku + '\'' +
                ", conditionType=" + conditionType +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", featured=" + featured +
                ", updateReason='" + updateReason + '\'' +
                '}';
    }
}