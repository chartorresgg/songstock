package com.songstock.dto;

import java.util.List;

/**
 * DTO específico para la Historia de Usuario:
 * "Como comprador, quiero ver si un disco MP3 tiene versión en vinilo para decidir qué formato comprar"
 */
public class AlbumFormatsDTO {
    
    private Long albumId;
    private String albumTitle;
    private String artistName;
    private String genreName;
    private Integer releaseYear;
    
    // Formatos disponibles
    private List<ProductDTO> digitalVersions;
    private List<ProductDTO> vinylVersions;
    
    // Flags de disponibilidad
    private boolean hasDigitalVersion;
    private boolean hasVinylVersion;
    private boolean hasBothFormats;
    
    // Información adicional
    private ProductDTO recommendedDigital; // Versión digital más económica o mejor calificada
    private ProductDTO recommendedVinyl;   // Versión vinilo más económica o mejor calificada
    
    // Constructores
    public AlbumFormatsDTO() {}
    
    public AlbumFormatsDTO(Long albumId, String albumTitle, String artistName) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
    }
    
    // Getters y Setters
    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
    
    public String getAlbumTitle() { return albumTitle; }
    public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }
    
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public List<ProductDTO> getDigitalVersions() { return digitalVersions; }
    public void setDigitalVersions(List<ProductDTO> digitalVersions) { 
        this.digitalVersions = digitalVersions;
        this.hasDigitalVersion = digitalVersions != null && !digitalVersions.isEmpty();
        updateBothFormatsFlag();
    }
    
    public List<ProductDTO> getVinylVersions() { return vinylVersions; }
    public void setVinylVersions(List<ProductDTO> vinylVersions) { 
        this.vinylVersions = vinylVersions;
        this.hasVinylVersion = vinylVersions != null && !vinylVersions.isEmpty();
        updateBothFormatsFlag();
    }
    
    public boolean isHasDigitalVersion() { return hasDigitalVersion; }
    public void setHasDigitalVersion(boolean hasDigitalVersion) { this.hasDigitalVersion = hasDigitalVersion; }
    
    public boolean isHasVinylVersion() { return hasVinylVersion; }
    public void setHasVinylVersion(boolean hasVinylVersion) { this.hasVinylVersion = hasVinylVersion; }
    
    public boolean isHasBothFormats() { return hasBothFormats; }
    public void setHasBothFormats(boolean hasBothFormats) { this.hasBothFormats = hasBothFormats; }
    
    public ProductDTO getRecommendedDigital() { return recommendedDigital; }
    public void setRecommendedDigital(ProductDTO recommendedDigital) { this.recommendedDigital = recommendedDigital; }
    
    public ProductDTO getRecommendedVinyl() { return recommendedVinyl; }
    public void setRecommendedVinyl(ProductDTO recommendedVinyl) { this.recommendedVinyl = recommendedVinyl; }
    
    // Métodos de utilidad
    private void updateBothFormatsFlag() {
        this.hasBothFormats = this.hasDigitalVersion && this.hasVinylVersion;
    }
    
    /**
     * Obtiene un mensaje descriptivo sobre los formatos disponibles
     */
    public String getAvailabilityMessage() {
        if (hasBothFormats) {
            return "Este álbum está disponible tanto en formato digital como en vinilo.";
        } else if (hasDigitalVersion && !hasVinylVersion) {
            return "Este álbum está disponible solo en formato digital.";
        } else if (hasVinylVersion && !hasDigitalVersion) {
            return "Este álbum está disponible solo en formato vinilo.";
        } else {
            return "Este álbum no está disponible actualmente.";
        }
    }
    
    /**
     * Obtiene el conteo total de formatos disponibles
     */
    public int getTotalFormatsCount() {
        int count = 0;
        if (digitalVersions != null) count += digitalVersions.size();
        if (vinylVersions != null) count += vinylVersions.size();
        return count;
    }
}