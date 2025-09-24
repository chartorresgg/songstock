package com.songstock.dto;

import java.util.List;

/**
 * DTO para el resumen del inventario completo de un proveedor
 * Incluye estadísticas generales y lista de productos
 */
public class ProviderInventorySummaryDTO {

    private Long providerId;
    private String providerBusinessName;
    private Integer totalProducts;
    private Integer totalUnitsInStock;
    private Integer productsWithStock;
    private Integer productsOutOfStock;
    private List<ProductInventoryResponseDTO> products;

    // Constructor vacío
    public ProviderInventorySummaryDTO() {
    }

    // Constructor con parámetros básicos
    public ProviderInventorySummaryDTO(Long providerId, String providerBusinessName) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
    }

    // Constructor completo
    public ProviderInventorySummaryDTO(Long providerId, String providerBusinessName,
            Integer totalProducts, Integer totalUnitsInStock,
            Integer productsWithStock, Integer productsOutOfStock,
            List<ProductInventoryResponseDTO> products) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
        this.totalProducts = totalProducts;
        this.totalUnitsInStock = totalUnitsInStock;
        this.productsWithStock = productsWithStock;
        this.productsOutOfStock = productsOutOfStock;
        this.products = products;
    }

    // Getters y Setters
    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getProviderBusinessName() {
        return providerBusinessName;
    }

    public void setProviderBusinessName(String providerBusinessName) {
        this.providerBusinessName = providerBusinessName;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getTotalUnitsInStock() {
        return totalUnitsInStock;
    }

    public void setTotalUnitsInStock(Integer totalUnitsInStock) {
        this.totalUnitsInStock = totalUnitsInStock;
    }

    public Integer getProductsWithStock() {
        return productsWithStock;
    }

    public void setProductsWithStock(Integer productsWithStock) {
        this.productsWithStock = productsWithStock;
    }

    public Integer getProductsOutOfStock() {
        return productsOutOfStock;
    }

    public void setProductsOutOfStock(Integer productsOutOfStock) {
        this.productsOutOfStock = productsOutOfStock;
    }

    public List<ProductInventoryResponseDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInventoryResponseDTO> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProviderInventorySummaryDTO{" +
                "providerId=" + providerId +
                ", providerBusinessName='" + providerBusinessName + '\'' +
                ", totalProducts=" + totalProducts +
                ", totalUnitsInStock=" + totalUnitsInStock +
                ", productsWithStock=" + productsWithStock +
                ", productsOutOfStock=" + productsOutOfStock +
                ", products=" + (products != null ? products.size() + " products" : "null") +
                '}';
    }
}