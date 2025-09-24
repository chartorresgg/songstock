package com.songstock.dto;

import com.songstock.entity.ProductType;
import com.songstock.entity.ConditionType;
import java.math.BigDecimal;

/**
 * DTO para filtrar productos del catálogo
 * Permite búsquedas y filtros avanzados en el catálogo
 */
public class CatalogFilterDTO {

    private String searchQuery; // Búsqueda por nombre de álbum o artista
    private Long categoryId;
    private Long genreId;
    private Long artistId;
    private ProductType productType;
    private ConditionType conditionType;

    // Filtros de precio
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Filtros de año
    private Integer minYear;
    private Integer maxYear;

    // Filtros de stock
    private Boolean inStockOnly = false; // Solo productos con stock
    private Boolean featuredOnly = false; // Solo productos destacados
    private Boolean activeOnly = true; // Solo productos activos (por defecto)

    // Ordenamiento
    private String sortBy = "createdAt"; // campo por el cual ordenar
    private String sortDirection = "desc"; // asc o desc

    // Constructor vacío
    public CatalogFilterDTO() {
    }

    // Constructor con búsqueda básica
    public CatalogFilterDTO(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    // Getters y Setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinYear() {
        return minYear;
    }

    public void setMinYear(Integer minYear) {
        this.minYear = minYear;
    }

    public Integer getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(Integer maxYear) {
        this.maxYear = maxYear;
    }

    public Boolean getInStockOnly() {
        return inStockOnly;
    }

    public void setInStockOnly(Boolean inStockOnly) {
        this.inStockOnly = inStockOnly;
    }

    public Boolean getFeaturedOnly() {
        return featuredOnly;
    }

    public void setFeaturedOnly(Boolean featuredOnly) {
        this.featuredOnly = featuredOnly;
    }

    public Boolean getActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(Boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "CatalogFilterDTO{" +
                "searchQuery='" + searchQuery + '\'' +
                ", categoryId=" + categoryId +
                ", genreId=" + genreId +
                ", productType=" + productType +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", inStockOnly=" + inStockOnly +
                ", featuredOnly=" + featuredOnly +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}