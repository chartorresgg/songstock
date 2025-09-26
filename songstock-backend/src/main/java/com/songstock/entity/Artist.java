package com.songstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un artista musical.
 * 
 * - Contiene datos como nombre, biografía, país y año de formación.
 * - Se relaciona con múltiples álbumes.
 */
@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único

    @NotBlank(message = "El nombre del artista es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio; // Biografía del artista

    @Size(max = 50, message = "El país no puede exceder 50 caracteres")
    @Column(name = "country", length = 50)
    private String country; // País de origen

    @Column(name = "formed_year")
    private Integer formedYear; // Año de formación

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Estado de actividad

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Fecha de creación

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Fecha de última actualización

    // Relación One-to-Many con Album
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Album> albums = new ArrayList<>();

    // ================= Constructores =================
    public Artist() {
    }

    public Artist(String name, String bio, String country, Integer formedYear) {
        this.name = name;
        this.bio = bio;
        this.country = country;
        this.formedYear = formedYear;
    }

    // ================= Getters y Setters =================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getFormedYear() {
        return formedYear;
    }

    public void setFormedYear(Integer formedYear) {
        this.formedYear = formedYear;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    // ================= Métodos de utilidad =================

    /** Agrega un álbum al artista y mantiene la relación bidireccional */
    public void addAlbum(Album album) {
        albums.add(album);
        album.setArtist(this);
    }

    /** Elimina un álbum del artista y actualiza la relación bidireccional */
    public void removeAlbum(Album album) {
        albums.remove(album);
        album.setArtist(null);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", formedYear=" + formedYear +
                ", isActive=" + isActive +
                '}';
    }
}
