package com.songstock.dto;

/**
 * DTO para estadísticas de usuarios en el dashboard
 */
public class UserStatsDTO {
    private Long totalUsers;
    private Long totalAdmins;
    private Long totalProviders;
    private Long totalCustomers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long pendingProviders;
    private Long verifiedProviders;
    private Long rejectedProviders;
    private Long newUsersThisMonth;
    private Long newUsersThisWeek;

    // Constructor vacío
    public UserStatsDTO() {
    }

    // Constructor completo
    public UserStatsDTO(Long totalUsers, Long totalAdmins, Long totalProviders, Long totalCustomers,
            Long activeUsers, Long inactiveUsers, Long pendingProviders, Long verifiedProviders,
            Long rejectedProviders, Long newUsersThisMonth, Long newUsersThisWeek) {
        this.totalUsers = totalUsers;
        this.totalAdmins = totalAdmins;
        this.totalProviders = totalProviders;
        this.totalCustomers = totalCustomers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.pendingProviders = pendingProviders;
        this.verifiedProviders = verifiedProviders;
        this.rejectedProviders = rejectedProviders;
        this.newUsersThisMonth = newUsersThisMonth;
        this.newUsersThisWeek = newUsersThisWeek;
    }

    // Getters y Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(Long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public Long getTotalProviders() {
        return totalProviders;
    }

    public void setTotalProviders(Long totalProviders) {
        this.totalProviders = totalProviders;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(Long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public Long getPendingProviders() {
        return pendingProviders;
    }

    public void setPendingProviders(Long pendingProviders) {
        this.pendingProviders = pendingProviders;
    }

    public Long getVerifiedProviders() {
        return verifiedProviders;
    }

    public void setVerifiedProviders(Long verifiedProviders) {
        this.verifiedProviders = verifiedProviders;
    }

    public Long getRejectedProviders() {
        return rejectedProviders;
    }

    public void setRejectedProviders(Long rejectedProviders) {
        this.rejectedProviders = rejectedProviders;
    }

    public Long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }

    public void setNewUsersThisMonth(Long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public Long getNewUsersThisWeek() {
        return newUsersThisWeek;
    }

    public void setNewUsersThisWeek(Long newUsersThisWeek) {
        this.newUsersThisWeek = newUsersThisWeek;
    }
}