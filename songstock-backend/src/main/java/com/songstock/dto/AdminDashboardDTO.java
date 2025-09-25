package com.songstock.dto;

public class AdminDashboardDTO {

    // Estadísticas de usuarios
    private Long totalUsers;
    private Long totalAdmins;
    private Long totalProviders;
    private Long totalCustomers;
    private Long activeUsers;
    private Long inactiveUsers;

    // Estadísticas de proveedores
    private Long verifiedProviders;
    private Long pendingProviders;
    private Long rejectedProviders;

    // Estadísticas de productos
    private Long totalProducts;
    private Long activeProducts;
    private Long digitalProducts;
    private Long physicalProducts;

    // Estadísticas recientes
    private Long usersRegisteredThisMonth;
    private Long providersVerifiedThisMonth;
    private Long productsCreatedThisMonth;

    // Constructores
    public AdminDashboardDTO() {
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

    public Long getVerifiedProviders() {
        return verifiedProviders;
    }

    public void setVerifiedProviders(Long verifiedProviders) {
        this.verifiedProviders = verifiedProviders;
    }

    public Long getPendingProviders() {
        return pendingProviders;
    }

    public void setPendingProviders(Long pendingProviders) {
        this.pendingProviders = pendingProviders;
    }

    public Long getRejectedProviders() {
        return rejectedProviders;
    }

    public void setRejectedProviders(Long rejectedProviders) {
        this.rejectedProviders = rejectedProviders;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(Long activeProducts) {
        this.activeProducts = activeProducts;
    }

    public Long getDigitalProducts() {
        return digitalProducts;
    }

    public void setDigitalProducts(Long digitalProducts) {
        this.digitalProducts = digitalProducts;
    }

    public Long getPhysicalProducts() {
        return physicalProducts;
    }

    public void setPhysicalProducts(Long physicalProducts) {
        this.physicalProducts = physicalProducts;
    }

    public Long getUsersRegisteredThisMonth() {
        return usersRegisteredThisMonth;
    }

    public void setUsersRegisteredThisMonth(Long usersRegisteredThisMonth) {
        this.usersRegisteredThisMonth = usersRegisteredThisMonth;
    }

    public Long getProvidersVerifiedThisMonth() {
        return providersVerifiedThisMonth;
    }

    public void setProvidersVerifiedThisMonth(Long providersVerifiedThisMonth) {
        this.providersVerifiedThisMonth = providersVerifiedThisMonth;
    }

    public Long getProductsCreatedThisMonth() {
        return productsCreatedThisMonth;
    }

    public void setProductsCreatedThisMonth(Long productsCreatedThisMonth) {
        this.productsCreatedThisMonth = productsCreatedThisMonth;
    }

    // Métodos de utilidad
    public double getProviderVerificationRate() {
        if (totalProviders == 0)
            return 0.0;
        return (verifiedProviders.doubleValue() / totalProviders.doubleValue()) * 100;
    }

    public double getUserActivityRate() {
        if (totalUsers == 0)
            return 0.0;
        return (activeUsers.doubleValue() / totalUsers.doubleValue()) * 100;
    }
}