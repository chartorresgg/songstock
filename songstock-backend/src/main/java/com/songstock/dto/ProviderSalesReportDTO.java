package com.songstock.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProviderSalesReportDTO {
    private Long providerId;
    private String providerBusinessName;
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long completedItems;
    private Long pendingItems;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private List<TopProductDTO> topProducts;

    public static class TopProductDTO {
        private Long productId;
        private String albumTitle;
        private String artistName;
        private Long quantitySold;
        private BigDecimal revenue;

        public TopProductDTO(Long productId, String albumTitle, String artistName, Long quantitySold,
                BigDecimal revenue) {
            this.productId = productId;
            this.albumTitle = albumTitle;
            this.artistName = artistName;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
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

        public Long getQuantitySold() {
            return quantitySold;
        }

        public void setQuantitySold(Long quantitySold) {
            this.quantitySold = quantitySold;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }
    }

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

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getCompletedItems() {
        return completedItems;
    }

    public void setCompletedItems(Long completedItems) {
        this.completedItems = completedItems;
    }

    public Long getPendingItems() {
        return pendingItems;
    }

    public void setPendingItems(Long pendingItems) {
        this.pendingItems = pendingItems;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public List<TopProductDTO> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<TopProductDTO> topProducts) {
        this.topProducts = topProducts;
    }
}