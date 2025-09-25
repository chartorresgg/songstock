package com.songstock.dto;

import com.songstock.dto.ProductDTO;
import java.math.BigDecimal;

/**
 * DTO adicional para comparación de formatos
 */
public class AlbumFormatComparisonDTO {
    private Long albumId;
    private String albumTitle;
    private String artistName;

    private boolean hasDigitalVersion;
    private boolean hasVinylVersion;
    private boolean hasBothFormats;

    private ProductDTO bestDigitalOption;
    private ProductDTO bestVinylOption;
    private BigDecimal priceDifference;

    // Getters y Setters
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

    public boolean isHasDigitalVersion() {
        return hasDigitalVersion;
    }

    public void setHasDigitalVersion(boolean hasDigitalVersion) {
        this.hasDigitalVersion = hasDigitalVersion;
    }

    public boolean isHasVinylVersion() {
        return hasVinylVersion;
    }

    public void setHasVinylVersion(boolean hasVinylVersion) {
        this.hasVinylVersion = hasVinylVersion;
    }

    public boolean isHasBothFormats() {
        return hasBothFormats;
    }

    public void setHasBothFormats(boolean hasBothFormats) {
        this.hasBothFormats = hasBothFormats;
    }

    public ProductDTO getBestDigitalOption() {
        return bestDigitalOption;
    }

    public void setBestDigitalOption(ProductDTO bestDigitalOption) {
        this.bestDigitalOption = bestDigitalOption;
    }

    public ProductDTO getBestVinylOption() {
        return bestVinylOption;
    }

    public void setBestVinylOption(ProductDTO bestVinylOption) {
        this.bestVinylOption = bestVinylOption;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }
}