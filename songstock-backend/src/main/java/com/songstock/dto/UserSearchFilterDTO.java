package com.songstock.dto;

import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import java.time.LocalDateTime;

public class UserSearchFilterDTO {

    private String searchQuery; // Búsqueda en nombre, username, email
    private UserRole role; // Filtrar por rol
    private Boolean isActive; // Filtrar por estado activo/inactivo
    private VerificationStatus verificationStatus; // Para proveedores
    private LocalDateTime createdAfter; // Usuarios registrados después de
    private LocalDateTime createdBefore; // Usuarios registrados antes de

    // Paginación y ordenamiento
    private String sortBy = "createdAt"; // Campo para ordenar
    private String sortDirection = "desc"; // Dirección: asc, desc
    private Integer page = 0; // Página (0-based)
    private Integer size = 20; // Tamaño de página

    // Constructores
    public UserSearchFilterDTO() {
    }

    // Getters y Setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(LocalDateTime createdAfter) {
        this.createdAfter = createdAfter;
    }

    public LocalDateTime getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(LocalDateTime createdBefore) {
        this.createdBefore = createdBefore;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}