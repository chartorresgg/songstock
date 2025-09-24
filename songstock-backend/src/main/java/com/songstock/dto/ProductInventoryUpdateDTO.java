package com.songstock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el inventario de un producto
 * Usado cuando un proveedor quiere actualizar el stock de sus productos
 */
public class ProductInventoryUpdateDTO {

    @NotNull(message = "La cantidad de stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockQuantity;

    private String updateReason; // Opcional: razón del cambio de inventario

    // Constructor vacío
    public ProductInventoryUpdateDTO() {
    }

    // Constructor con parámetros
    public ProductInventoryUpdateDTO(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductInventoryUpdateDTO(Integer stockQuantity, String updateReason) {
        this.stockQuantity = stockQuantity;
        this.updateReason = updateReason;
    }

    // Getters y Setters
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUpdateReason() {
        return updateReason;
    }

    public void setUpdateReason(String updateReason) {
        this.updateReason = updateReason;
    }

    @Override
    public String toString() {
        return "ProductInventoryUpdateDTO{" +
                "stockQuantity=" + stockQuantity +
                ", updateReason='" + updateReason + '\'' +
                '}';
    }
}