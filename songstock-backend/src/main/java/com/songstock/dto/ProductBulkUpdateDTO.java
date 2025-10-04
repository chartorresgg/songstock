package com.songstock.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para actualizaciones masivas de productos en el catálogo
 * Permite modificar múltiples productos a la vez
 */
public class ProductBulkUpdateDTO {

    @NotNull(message = "Los IDs de productos son obligatorios")
    @Size(min = 1, message = "Debe seleccionar al menos un producto")
    private List<Long> productIds;

    @NotNull(message = "El tipo de actualización es obligatorio")
    private UpdateType updateType;

    // Para actualizaciones de precio
    private BigDecimal value; // Porcentaje o valor fijo

    // Para operaciones booleanas
    private Boolean booleanValue;

    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String reason;

    // Tipo de operación para stock
    public enum StockOperation {
        SET, // Establecer valor absoluto
        INCREMENT, // Aumentar
        DECREMENT // Disminuir
    }

    public enum UpdateType {
        PRICE_INCREASE_PERCENTAGE, // Incrementar precio por porcentaje
        PRICE_DECREASE_PERCENTAGE, // Decrementar precio por porcentaje
        PRICE_SET_FIXED, // Establecer precio fijo
        STOCK_SET, // Establecer stock fijo
        STOCK_INCREMENT, // Incrementar stock
        STOCK_DECREMENT, // Decrementar stock
        TOGGLE_FEATURED, // Cambiar estado destacado
        TOGGLE_ACTIVE // Cambiar estado activo
    }

    private StockOperation stockOperation = StockOperation.SET;

    // Campos adicionales para compatibilidad
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean featured;
    private Boolean isActive;
    private String updateReason;

    // Constructor vacío
    public ProductBulkUpdateDTO() {
    }

    // Constructor con IDs
    public ProductBulkUpdateDTO(List<Long> productIds) {
        this.productIds = productIds;
    }

    // Constructor para actualizaciones de precio
    public ProductBulkUpdateDTO(List<Long> productIds, UpdateType updateType, BigDecimal value, String reason) {
        this.productIds = productIds;
        this.updateType = updateType;
        this.value = value;
        this.reason = reason;
    }

    // Constructor para actualizaciones booleanas
    public ProductBulkUpdateDTO(List<Long> productIds, UpdateType updateType, Boolean booleanValue, String reason) {
        this.productIds = productIds;
        this.updateType = updateType;
        this.booleanValue = booleanValue;
        this.reason = reason;
    }

    // Getters y Setters
    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
                ", updateType=" + updateType +
                ", value=" + value +
                ", booleanValue=" + booleanValue +
                ", reason='" + reason + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", featured=" + featured +
                ", isActive=" + isActive +
                ", stockOperation=" + stockOperation +
                ", updateReason='" + updateReason + '\'' +
                '}';
    }
}