package com.songstock.dto;

import com.songstock.entity.UserRole;

/**
 * DTO para filtros de búsqueda de usuarios
 */
public class UserFilterDTO {
    private UserRole role;
    private Boolean isActive;
    private String search; // Búsqueda por username, email, nombre
    private String verificationStatus; // PENDING, VERIFIED, REJECTED (para proveedores)
    private String sortBy; // username, email, createdAt, etc.
    private String sortDirection; // ASC, DESC
    private Integer page;
    private Integer size;

    // Constructor vacío
    public UserFilterDTO() {
        this.page = 0;
        this.size = 10;
        this.sortBy = "createdAt";
        this.sortDirection = "DESC";
    }

    // Getters y Setters
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
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