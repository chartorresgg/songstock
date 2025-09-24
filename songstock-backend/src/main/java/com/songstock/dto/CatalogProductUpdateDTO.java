package com.songstock.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

/**
 * DTO para actualizar productos en el catálogo del proveedor
 * Permite modificar precio, stock, condición y otros campos editables
 */
public class CatalogProductUpdateDTO {

    private Long categoryId;

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    private String conditionType;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockQuantity;

    // Campos específicos para vinilos físicos
    private String vinylSize;
    private String vinylSpeed;

    @Min(value = 1, message = "El peso debe ser mayor que 0")
    private Integer weightGrams;

    private Boolean featured;
    private Boolean isActive;

    private String description; // Descripción adicional del proveedor

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String providerNotes; // Notas internas del proveedor

    // Constructor vacío
    public CatalogProductUpdateDTO() {
    }

    // Constructor con campos principales
    public CatalogProductUpdateDTO(BigDecimal price, Integer stockQuantity, String conditionType) {
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.conditionType = conditionType;
    }

    // Getters y Setters
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderNotes() {
        return providerNotes;
    }

    public void setProviderNotes(String providerNotes) {
        this.providerNotes = providerNotes;
    }

    @Override
    public String toString() {
        return "CatalogProductUpdateDTO{" +
                "categoryId=" + categoryId +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", conditionType='" + conditionType + '\'' +
                ", featured=" + featured +
                ", isActive=" + isActive +
                '}';
    }
}