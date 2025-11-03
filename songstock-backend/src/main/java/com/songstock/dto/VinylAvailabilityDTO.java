package com.songstock.dto;

import java.math.BigDecimal;

public class VinylAvailabilityDTO {
    private Long productId;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String conditionType;
    private String vinylSize;
    private String vinylSpeed;
    private String providerName;
    private Long providerId;

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

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}