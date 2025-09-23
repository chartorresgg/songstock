package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título del álbum es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    // Relación Many-to-One con Artist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @NotNull(message = "El artista es obligatorio")
    private Artist artist;
    
    // Relación Many-to-One con Genre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;
    
    @Column(name = "release_year")
    private Integer releaseYear;
    
    @Size(max = 100, message = "El sello discográfico no puede exceder 100 caracteres")
    @Column(name = "label", length = 100)
    private String label;
    
    @Size(max = 50, message = "El número de catálogo no puede exceder 50 caracteres")
    @Column(name = "catalog_number", length = 50)
    private String catalogNumber;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relación One-to-Many con Products
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
    
    // Constructores
    public Album() {}
    
    public Album(String title, Artist artist, Genre genre, Integer releaseYear) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    
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
    
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    
    // Métodos de utilidad
    public void addProduct(Product product) {
        products.add(product);
        product.setAlbum(this);
    }
    
    public void removeProduct(Product product) {
        products.remove(product);
        product.setAlbum(null);
    }
    
    // Método para verificar si tiene versión en vinilo
    public boolean hasVinylVersion() {
        return products.stream()
                .anyMatch(product -> product.getProductType() == ProductType.PHYSICAL && 
                                   product.getIsActive());
    }
    
    // Método para verificar si tiene versión digital
    public boolean hasDigitalVersion() {
        return products.stream()
                .anyMatch(product -> product.getProductType() == ProductType.DIGITAL && 
                                   product.getIsActive());
    }
    
    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", genre=" + (genre != null ? genre.getName() : "null") +
                ", releaseYear=" + releaseYear +
                ", isActive=" + isActive +
                '}';
    }
}