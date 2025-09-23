package com.songstock.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class GenreDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;
    
    private String description;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    // Información adicional para respuestas
    private Long albumCount;
    
    // Constructores
    public GenreDTO() {}
    
    public GenreDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getAlbumCount() { return albumCount; }
    public void setAlbumCount(Long albumCount) { this.albumCount = albumCount; }
}