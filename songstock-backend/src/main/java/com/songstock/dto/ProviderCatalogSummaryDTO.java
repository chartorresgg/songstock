package com.songstock.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO resumen del catálogo completo de un proveedor
 * Incluye estadísticas y lista de productos
 */
public class ProviderCatalogSummaryDTO {

    private Long providerId;
    private String providerBusinessName;

    // Estadísticas del catálogo
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer inactiveProducts;
    private Integer featuredProducts;
    private Integer productsInStock;
    private Integer productsOutOfStock;
    private BigDecimal averagePrice;
    private BigDecimal totalCatalogValue; // suma de precio * stock

    // Desglose por tipo
    private Integer physicalProducts;
    private Integer digitalProducts;

    // Desglose por condición
    private Integer newProducts;
    private Integer usedProducts;

    // Lista de productos del catálogo
    private List<ProductCatalogResponseDTO> products;

    // Constructor vacío
    public ProviderCatalogSummaryDTO() {
    }

    // Constructor con datos básicos
    public ProviderCatalogSummaryDTO(Long providerId, String providerBusinessName) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
    }

    // Constructor completo
    public ProviderCatalogSummaryDTO(Long providerId, String providerBusinessName,
            Integer totalProducts, Integer activeProducts,
            Integer featuredProducts, Integer productsInStock,
            BigDecimal averagePrice, List<ProductCatalogResponseDTO> products) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
        this.totalProducts = totalProducts;
        this.activeProducts = activeProducts;
        this.featuredProducts = featuredProducts;
        this.productsInStock = productsInStock;
        this.averagePrice = averagePrice;
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

    public Integer getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(Integer activeProducts) {
        this.activeProducts = activeProducts;
    }

    public Integer getInactiveProducts() {
        return inactiveProducts;
    }

    public void setInactiveProducts(Integer inactiveProducts) {
        this.inactiveProducts = inactiveProducts;
    }

    public Integer getFeaturedProducts() {
        return featuredProducts;
    }

    public void setFeaturedProducts(Integer featuredProducts) {
        this.featuredProducts = featuredProducts;
    }

    public Integer getProductsInStock() {
        return productsInStock;
    }

    public void setProductsInStock(Integer productsInStock) {
        this.productsInStock = productsInStock;
    }

    public Integer getProductsOutOfStock() {
        return productsOutOfStock;
    }

    public void setProductsOutOfStock(Integer productsOutOfStock) {
        this.productsOutOfStock = productsOutOfStock;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getTotalCatalogValue() {
        return totalCatalogValue;
    }

    public void setTotalCatalogValue(BigDecimal totalCatalogValue) {
        this.totalCatalogValue = totalCatalogValue;
    }

    public Integer getPhysicalProducts() {
        return physicalProducts;
    }

    public void setPhysicalProducts(Integer physicalProducts) {
        this.physicalProducts = physicalProducts;
    }

    public Integer getDigitalProducts() {
        return digitalProducts;
    }

    public void setDigitalProducts(Integer digitalProducts) {
        this.digitalProducts = digitalProducts;
    }

    public Integer getNewProducts() {
        return newProducts;
    }

    public void setNewProducts(Integer newProducts) {
        this.newProducts = newProducts;
    }

    public Integer getUsedProducts() {
        return usedProducts;
    }

    public void setUsedProducts(Integer usedProducts) {
        this.usedProducts = usedProducts;
    }

    public List<ProductCatalogResponseDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCatalogResponseDTO> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProviderCatalogSummaryDTO{" +
                "providerId=" + providerId +
                ", providerBusinessName='" + providerBusinessName + '\'' +
                ", totalProducts=" + totalProducts +
                ", activeProducts=" + activeProducts +
                ", productsInStock=" + productsInStock +
                ", averagePrice=" + averagePrice +
                ", products=" + (products != null ? products.size() + " items" : "null") +
                '}';
    }
}