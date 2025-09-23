package com.songstock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ArtistDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del artista es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    private String bio;
    
    @Size(max = 50, message = "El país no puede exceder 50 caracteres")
    private String country;
    
    private Integer formedYear;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Información adicional para respuestas
    private Long albumCount;
    
    // Constructores
    public ArtistDTO() {}
    
    public ArtistDTO(String name, String bio, String country, Integer formedYear) {
        this.name = name;
        this.bio = bio;
        this.country = country;
        this.formedYear = formedYear;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Integer getFormedYear() { return formedYear; }
    public void setFormedYear(Integer formedYear) { this.formedYear = formedYear; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getAlbumCount() { return albumCount; }
    public void setAlbumCount(Long albumCount) { this.albumCount = albumCount; }
}