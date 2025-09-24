package com.songstock.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para ajustar el stock de un producto (incrementar o decrementar)
 * Usado para operaciones como "agregar 5 unidades" o "restar 2 unidades"
 */
public class ProductStockAdjustmentDTO {

    public enum AdjustmentType {
        INCREMENT, // Aumentar stock
        DECREMENT // Disminuir stock
    }

    @NotNull(message = "El tipo de ajuste es obligatorio")
    private AdjustmentType adjustmentType;

    @NotNull(message = "La cantidad de ajuste es obligatoria")
    private Integer quantity;

    private String reason; // Razón del ajuste (opcional)

    // Constructor vacío
    public ProductStockAdjustmentDTO() {
    }

    // Constructor con parámetros
    public ProductStockAdjustmentDTO(AdjustmentType adjustmentType, Integer quantity) {
        this.adjustmentType = adjustmentType;
        this.quantity = quantity;
    }

    public ProductStockAdjustmentDTO(AdjustmentType adjustmentType, Integer quantity, String reason) {
        this.adjustmentType = adjustmentType;
        this.quantity = quantity;
        this.reason = reason;
    }

    // Getters y Setters
    public AdjustmentType getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(AdjustmentType adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ProductStockAdjustmentDTO{" +
                "adjustmentType=" + adjustmentType +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                '}';
    }
}