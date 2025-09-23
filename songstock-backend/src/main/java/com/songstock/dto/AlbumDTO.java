package com.songstock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AlbumDTO {
    
    private Long id;
    
    @NotBlank(message = "El título del álbum es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;
    
    @NotNull(message = "El artista es obligatorio")
    private Long artistId;
    
    private String artistName; // Para respuestas
    
    private Long genreId;
    
    private String genreName; // Para respuestas
    
    private Integer releaseYear;
    
    @Size(max = 100, message = "El sello discográfico no puede exceder 100 caracteres")
    private String label;
    
    @Size(max = 50, message = "El número de catálogo no puede exceder 50 caracteres")
    private String catalogNumber;
    
    private String description;
    
    private Integer durationMinutes;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Información adicional para respuestas
    private Long productCount;
    private Boolean hasVinylVersion;
    private Boolean hasDigitalVersion;
    
    // Constructores
    public AlbumDTO() {}
    
    public AlbumDTO(String title, Long artistId, Long genreId, Integer releaseYear) {
        this.title = title;
        this.artistId = artistId;
        this.genreId = genreId;
        this.releaseYear = releaseYear;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Long getArtistId() { return artistId; }
    public void setArtistId(Long artistId) { this.artistId = artistId; }
    
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    public Long getGenreId() { return genreId; }
    public void setGenreId(Long genreId) { this.genreId = genreId; }
    
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public String getCatalogNumber() { return catalogNumber; }
    public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getProductCount() { return productCount; }
    public void setProductCount(Long productCount) { this.productCount = productCount; }
    
    public Boolean getHasVinylVersion() { return hasVinylVersion; }
    public void setHasVinylVersion(Boolean hasVinylVersion) { this.hasVinylVersion = hasVinylVersion; }
    
    public Boolean getHasDigitalVersion() { return hasDigitalVersion; }
    public void setHasDigitalVersion(Boolean hasDigitalVersion) { this.hasDigitalVersion = hasDigitalVersion; }
}