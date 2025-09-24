package com.songstock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para información de inventario de un producto
 * Contiene información detallada del producto y su stock
 */
public class ProductInventoryResponseDTO {

    private Long productId;
    private String sku;
    private String albumTitle;
    private String artistName;
    private BigDecimal price;
    private Integer currentStock;
    private String productType; // PHYSICAL, DIGITAL
    private String conditionType; // NEW, USED, etc.
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime lastStockUpdate;

    // Constructor vacío
    public ProductInventoryResponseDTO() {
    }

    // Constructor completo
    public ProductInventoryResponseDTO(Long productId, String sku, String albumTitle,
            String artistName, BigDecimal price, Integer currentStock,
            String productType, String conditionType, Boolean isActive,
            Boolean isFeatured, LocalDateTime lastStockUpdate) {
        this.productId = productId;
        this.sku = sku;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.price = price;
        this.currentStock = currentStock;
        this.productType = productType;
        this.conditionType = conditionType;
        this.isActive = isActive;
        this.isFeatured = isFeatured;
        this.lastStockUpdate = lastStockUpdate;
    }

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public LocalDateTime getLastStockUpdate() {
        return lastStockUpdate;
    }

    public void setLastStockUpdate(LocalDateTime lastStockUpdate) {
        this.lastStockUpdate = lastStockUpdate;
    }

    @Override
    public String toString() {
        return "ProductInventoryResponseDTO{" +
                "productId=" + productId +
                ", sku='" + sku + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                ", price=" + price +
                ", currentStock=" + currentStock +
                ", productType='" + productType + '\'' +
                ", conditionType='" + conditionType + '\'' +
                ", isActive=" + isActive +
                ", isFeatured=" + isFeatured +
                ", lastStockUpdate=" + lastStockUpdate +
                '}';
    }
}