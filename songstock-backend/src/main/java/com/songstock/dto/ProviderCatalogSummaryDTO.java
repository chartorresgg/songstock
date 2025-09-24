package com.songstock.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para resumen del catálogo completo del proveedor
 * Incluye métricas y estadísticas del catálogo
 */
public class ProviderCatalogSummaryDTO {

    private Long providerId;
    private String providerBusinessName;

    // Estadísticas básicas
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer inactiveProducts;
    private Integer featuredProducts;

    // Estadísticas de stock
    private Integer productsInStock;
    private Integer productsLowStock;
    private Integer productsOutOfStock;
    private Integer totalUnitsInStock;

    // Estadísticas financieras
    private BigDecimal totalCatalogValue; // Suma del precio * stock de todos los productos
    private BigDecimal averagePrice;
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;

    // Distribución por categorías
    private Integer totalCategories;
    private Integer totalGenres;
    private Integer totalArtists;

    // Lista de productos del catálogo
    private List<CatalogProductResponseDTO> products;

    // Constructor vacío
    public ProviderCatalogSummaryDTO() {
    }

    // Constructor con información básica
    public ProviderCatalogSummaryDTO(Long providerId, String providerBusinessName) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
    }

    // Constructor completo
    public ProviderCatalogSummaryDTO(Long providerId, String providerBusinessName,
            Integer totalProducts, Integer activeProducts,
            Integer productsInStock, Integer totalUnitsInStock,
            BigDecimal totalCatalogValue, BigDecimal averagePrice,
            List<CatalogProductResponseDTO> products) {
        this.providerId = providerId;
        this.providerBusinessName = providerBusinessName;
        this.totalProducts = totalProducts;
        this.activeProducts = activeProducts;
        this.productsInStock = productsInStock;
        this.totalUnitsInStock = totalUnitsInStock;
        this.totalCatalogValue = totalCatalogValue;
        this.averagePrice = averagePrice;
        this.products = products;

        // Calcular campos derivados
        this.inactiveProducts = totalProducts - activeProducts;
        this.productsOutOfStock = totalProducts - productsInStock;
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
        if (this.totalProducts != null) {
            this.inactiveProducts = this.totalProducts - activeProducts;
        }
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
        if (this.totalProducts != null) {
            this.productsOutOfStock = this.totalProducts - productsInStock;
        }
    }

    public Integer getProductsLowStock() {
        return productsLowStock;
    }

    public void setProductsLowStock(Integer productsLowStock) {
        this.productsLowStock = productsLowStock;
    }

    public Integer getProductsOutOfStock() {
        return productsOutOfStock;
    }

    public void setProductsOutOfStock(Integer productsOutOfStock) {
        this.productsOutOfStock = productsOutOfStock;
    }

    public Integer getTotalUnitsInStock() {
        return totalUnitsInStock;
    }

    public void setTotalUnitsInStock(Integer totalUnitsInStock) {
        this.totalUnitsInStock = totalUnitsInStock;
    }

    public BigDecimal getTotalCatalogValue() {
        return totalCatalogValue;
    }

    public void setTotalCatalogValue(BigDecimal totalCatalogValue) {
        this.totalCatalogValue = totalCatalogValue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public Integer getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(Integer totalCategories) {
        this.totalCategories = totalCategories;
    }

    public Integer getTotalGenres() {
        return totalGenres;
    }

    public void setTotalGenres(Integer totalGenres) {
        this.totalGenres = totalGenres;
    }

    public Integer getTotalArtists() {
        return totalArtists;
    }

    public void setTotalArtists(Integer totalArtists) {
        this.totalArtists = totalArtists;
    }

    public List<CatalogProductResponseDTO> getProducts() {
        return products;
    }

    public void setProducts(List<CatalogProductResponseDTO> products) {
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
                ", totalUnitsInStock=" + totalUnitsInStock +
                ", totalCatalogValue=" + totalCatalogValue +
                ", averagePrice=" + averagePrice +
                '}';
    }
}