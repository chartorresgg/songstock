package com.songstock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para productos del catálogo
 * Incluye información completa para mostrar en la gestión del catálogo
 */
public class CatalogProductResponseDTO {

    private Long id;
    private String sku;

    // Información del álbum
    private Long albumId;
    private String albumTitle;
    private String artistName;
    private Integer releaseYear;
    private String genreName;
    private String label;

    // Información del producto
    private String categoryName;
    private String productType;
    private String conditionType;
    private BigDecimal price;
    private Integer stockQuantity;

    // Información específica de vinilo
    private String vinylSize;
    private String vinylSpeed;
    private Integer weightGrams;

    // Estado y configuración
    private Boolean isActive;
    private Boolean featured;
    private String description;
    private String providerNotes;

    // Métricas del catálogo
    private Boolean hasStock;
    private String stockStatus; // "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;

    // Constructor vacío
    public CatalogProductResponseDTO() {
    }

    // Constructor completo
    public CatalogProductResponseDTO(Long id, String sku, Long albumId, String albumTitle,
            String artistName, Integer releaseYear, String genreName,
            String categoryName, BigDecimal price, Integer stockQuantity,
            String conditionType, Boolean isActive) {
        this.id = id;
        this.sku = sku;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.releaseYear = releaseYear;
        this.genreName = genreName;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.conditionType = conditionType;
        this.isActive = isActive;
        this.hasStock = stockQuantity != null && stockQuantity > 0;
        this.stockStatus = determineStockStatus(stockQuantity);
    }

    // Método para determinar el estado del stock
    private String determineStockStatus(Integer stock) {
        if (stock == null || stock == 0) {
            return "OUT_OF_STOCK";
        } else if (stock <= 5) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
        this.hasStock = stockQuantity != null && stockQuantity > 0;
        this.stockStatus = determineStockStatus(stockQuantity);
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getProviderNotes() {
        return providerNotes;
    }

    public void setProviderNotes(String providerNotes) {
        this.providerNotes = providerNotes;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CatalogProductResponseDTO{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", stockStatus='" + stockStatus + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}