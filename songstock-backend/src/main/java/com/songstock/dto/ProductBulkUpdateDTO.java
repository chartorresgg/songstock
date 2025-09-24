package com.songstock.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para actualizaciones masivas de productos en el catálogo
 * Permite modificar múltiples productos a la vez
 */
public class ProductBulkUpdateDTO {

    @NotEmpty(message = "La lista de IDs de productos no puede estar vacía")
    private List<Long> productIds;

    // Campos a actualizar (todos opcionales)
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean featured;
    private Boolean isActive;
    private String updateReason;

    // Tipo de operación para stock
    public enum StockOperation {
        SET, // Establecer valor absoluto
        INCREMENT, // Aumentar
        DECREMENT // Disminuir
    }

    private StockOperation stockOperation = StockOperation.SET;

    // Constructor vacío
    public ProductBulkUpdateDTO() {
    }

    // Constructor con IDs
    public ProductBulkUpdateDTO(List<Long> productIds) {
        this.productIds = productIds;
    }

    // Getters y Setters
    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
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

    public String getUpdateReason() {
        return updateReason;
    }

    public void setUpdateReason(String updateReason) {
        this.updateReason = updateReason;
    }

    public StockOperation getStockOperation() {
        return stockOperation;
    }

    public void setStockOperation(StockOperation stockOperation) {
        this.stockOperation = stockOperation;
    }

    @Override
    public String toString() {
        return "ProductBulkUpdateDTO{" +
                "productIds=" + productIds +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", featured=" + featured +
                ", isActive=" + isActive +
                ", stockOperation=" + stockOperation +
                ", updateReason='" + updateReason + '\'' +
                '}';
    }
}