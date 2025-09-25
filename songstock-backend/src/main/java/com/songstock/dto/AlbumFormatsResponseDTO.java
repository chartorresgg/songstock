package com.songstock.dto;

import com.songstock.entity.ProductType;
import java.math.BigDecimal;
import java.util.List;

public class AlbumFormatsResponseDTO {

    private Long albumId;
    private String albumTitle;
    private String artistName;
    private Integer releaseYear;
    private List<FormatAvailabilityDTO> availableFormats;

    public static class FormatAvailabilityDTO {
        private Long productId;
        private ProductType productType;
        private BigDecimal price;
        private Integer stockQuantity;
        private Boolean isActive;

        // Campos específicos para vinilo
        private String vinylSize;
        private String vinylSpeed;
        private String conditionType;

        // Campos específicos para digital
        private String audioQuality;
        private String fileFormat;

        // Constructors
        public FormatAvailabilityDTO() {
        }

        public FormatAvailabilityDTO(Long productId, ProductType productType,
                BigDecimal price, Integer stockQuantity, Boolean isActive) {
            this.productId = productId;
            this.productType = productType;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.isActive = isActive;
        }

        // Getters and Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public ProductType getProductType() {
            return productType;
        }

        public void setProductType(ProductType productType) {
            this.productType = productType;
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

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
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

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }

        public String getAudioQuality() {
            return audioQuality;
        }

        public void setAudioQuality(String audioQuality) {
            this.audioQuality = audioQuality;
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public void setFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
        }
    }

    // Constructors
    public AlbumFormatsResponseDTO() {
    }

    public AlbumFormatsResponseDTO(Long albumId, String albumTitle, String artistName,
            Integer releaseYear, List<FormatAvailabilityDTO> availableFormats) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.releaseYear = releaseYear;
        this.availableFormats = availableFormats;
    }

    // Métodos de utilidad
    public boolean hasVinylFormat() {
        return availableFormats != null &&
                availableFormats.stream()
                        .anyMatch(format -> format.getProductType() == ProductType.PHYSICAL);
    }

    public boolean hasDigitalFormat() {
        return availableFormats != null &&
                availableFormats.stream()
                        .anyMatch(format -> format.getProductType() == ProductType.DIGITAL);
    }

    public List<FormatAvailabilityDTO> getVinylFormats() {
        if (availableFormats == null)
            return List.of();
        return availableFormats.stream()
                .filter(format -> format.getProductType() == ProductType.PHYSICAL)
                .toList();
    }

    public List<FormatAvailabilityDTO> getDigitalFormats() {
        if (availableFormats == null)
            return List.of();
        return availableFormats.stream()
                .filter(format -> format.getProductType() == ProductType.DIGITAL)
                .toList();
    }

    // Getters and Setters
    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
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

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<FormatAvailabilityDTO> getAvailableFormats() {
        return availableFormats;
    }

    public void setAvailableFormats(List<FormatAvailabilityDTO> availableFormats) {
        this.availableFormats = availableFormats;
    }
}