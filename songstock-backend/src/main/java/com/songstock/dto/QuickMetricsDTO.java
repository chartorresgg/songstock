package com.songstock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO para métricas rápidas del dashboard administrativo
 * Contiene estadísticas clave para widgets y resúmenes ejecutivos
 */
public class QuickMetricsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long totalUsers;

    @NotNull
    @Min(0)
    private Long activeUsers;

    @NotNull
    @Min(0)
    private Long inactiveUsers;

    @NotNull
    @Min(0)
    private Long pendingProviders;

    @NotNull
    @Min(0)
    private Long verifiedProviders;

    @NotNull
    @Min(0)
    private Long rejectedProviders;

    @NotNull
    @Min(0)
    private Long usersThisMonth;

    @NotNull
    @Min(0)
    private Long providersVerifiedThisMonth;

    // Métricas derivadas (calculadas)
    private Double userActivityRate; // Porcentaje de usuarios activos
    private Double providerVerificationRate; // Porcentaje de proveedores verificados

    // Constructores
    public QuickMetricsDTO() {
    }

    public QuickMetricsDTO(Long totalUsers, Long activeUsers, Long pendingProviders,
            Long verifiedProviders, Long usersThisMonth) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.pendingProviders = pendingProviders;
        this.verifiedProviders = verifiedProviders;
        this.usersThisMonth = usersThisMonth;

        // Calcular valores derivados
        calculateDerivedMetrics();
    }

    // Getters y Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
        calculateDerivedMetrics();
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
        calculateDerivedMetrics();
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
        calculateDerivedMetrics();
    }

    public Long getRejectedProviders() {
        return rejectedProviders;
    }

    public void setRejectedProviders(Long rejectedProviders) {
        this.rejectedProviders = rejectedProviders;
    }

    public Long getUsersThisMonth() {
        return usersThisMonth;
    }

    public void setUsersThisMonth(Long usersThisMonth) {
        this.usersThisMonth = usersThisMonth;
    }

    public Long getProvidersVerifiedThisMonth() {
        return providersVerifiedThisMonth;
    }

    public void setProvidersVerifiedThisMonth(Long providersVerifiedThisMonth) {
        this.providersVerifiedThisMonth = providersVerifiedThisMonth;
    }

    public Double getUserActivityRate() {
        return userActivityRate;
    }

    public void setUserActivityRate(Double userActivityRate) {
        this.userActivityRate = userActivityRate;
    }

    public Double getProviderVerificationRate() {
        return providerVerificationRate;
    }

    public void setProviderVerificationRate(Double providerVerificationRate) {
        this.providerVerificationRate = providerVerificationRate;
    }

    // Métodos de utilidad

    /**
     * Calcula métricas derivadas automáticamente
     */
    private void calculateDerivedMetrics() {
        // Calcular tasa de actividad de usuarios
        if (totalUsers != null && totalUsers > 0 && activeUsers != null) {
            this.userActivityRate = (activeUsers.doubleValue() / totalUsers.doubleValue()) * 100;
        } else {
            this.userActivityRate = 0.0;
        }

        // Calcular tasa de verificación de proveedores
        Long totalProviders = getTotalProviders();
        if (totalProviders > 0 && verifiedProviders != null) {
            this.providerVerificationRate = (verifiedProviders.doubleValue() / totalProviders.doubleValue()) * 100;
        } else {
            this.providerVerificationRate = 0.0;
        }
    }

    /**
     * Obtiene el total de proveedores (verificados + pendientes + rechazados)
     */
    public Long getTotalProviders() {
        long total = 0;
        if (verifiedProviders != null)
            total += verifiedProviders;
        if (pendingProviders != null)
            total += pendingProviders;
        if (rejectedProviders != null)
            total += rejectedProviders;
        return total;
    }

    /**
     * Verifica si hay alertas que requieren atención
     */
    public boolean hasAlerts() {
        return (pendingProviders != null && pendingProviders > 0) ||
                (userActivityRate != null && userActivityRate < 50.0);
    }

    /**
     * Obtiene el número de alertas activas
     */
    public int getAlertCount() {
        int alerts = 0;

        if (pendingProviders != null && pendingProviders > 0) {
            alerts++;
        }

        if (userActivityRate != null && userActivityRate < 50.0) {
            alerts++;
        }

        if (providerVerificationRate != null && providerVerificationRate < 70.0) {
            alerts++;
        }

        return alerts;
    }

    /**
     * Obtiene crecimiento de usuarios este mes vs total
     */
    public Double getMonthlyGrowthRate() {
        if (totalUsers != null && totalUsers > 0 && usersThisMonth != null) {
            return (usersThisMonth.doubleValue() / totalUsers.doubleValue()) * 100;
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "QuickMetricsDTO{" +
                "totalUsers=" + totalUsers +
                ", activeUsers=" + activeUsers +
                ", pendingProviders=" + pendingProviders +
                ", verifiedProviders=" + verifiedProviders +
                ", usersThisMonth=" + usersThisMonth +
                ", userActivityRate=" + userActivityRate +
                ", providerVerificationRate=" + providerVerificationRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        QuickMetricsDTO that = (QuickMetricsDTO) o;

        return totalUsers != null ? totalUsers.equals(that.totalUsers)
                : that.totalUsers == null &&
                        activeUsers != null ? activeUsers.equals(that.activeUsers)
                                : that.activeUsers == null &&
                                        pendingProviders != null
                                                ? pendingProviders.equals(that.pendingProviders)
                                                : that.pendingProviders == null &&
                                                        verifiedProviders != null
                                                                ? verifiedProviders.equals(that.verifiedProviders)
                                                                : that.verifiedProviders == null &&
                                                                        usersThisMonth != null
                                                                                ? usersThisMonth
                                                                                        .equals(that.usersThisMonth)
                                                                                : that.usersThisMonth == null;
    }

    @Override
    public int hashCode() {
        int result = totalUsers != null ? totalUsers.hashCode() : 0;
        result = 31 * result + (activeUsers != null ? activeUsers.hashCode() : 0);
        result = 31 * result + (pendingProviders != null ? pendingProviders.hashCode() : 0);
        result = 31 * result + (verifiedProviders != null ? verifiedProviders.hashCode() : 0);
        result = 31 * result + (usersThisMonth != null ? usersThisMonth.hashCode() : 0);
        return result;
    }
}