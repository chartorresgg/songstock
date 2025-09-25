package com.songstock.mapper;

import com.songstock.dto.*;
import com.songstock.entity.User;
import com.songstock.entity.Provider;
import com.songstock.entity.UserRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversiones entre entidades User/Provider y DTOs administrativos
 */
@Component
public class UserManagementMapper {

    /**
     * Convertir User a UserManagementResponseDTO
     */
    public UserManagementResponseDTO toManagementResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserManagementResponseDTO dto = new UserManagementResponseDTO();

        // Campos básicos del usuario
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /**
     * Convertir User con Provider a UserManagementResponseDTO completo
     */
    public UserManagementResponseDTO toManagementResponseDTO(User user, Provider provider) {
        UserManagementResponseDTO dto = toManagementResponseDTO(user);

        if (dto != null && provider != null) {
            dto.setProviderId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setVerificationStatus(provider.getVerificationStatus());
            dto.setVerificationDate(provider.getVerificationDate());
        }

        return dto;
    }

    /**
     * Convertir lista de Users a lista de UserManagementResponseDTO
     */
    public List<UserManagementResponseDTO> toManagementResponseDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toManagementResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertir UserEditDTO a User (para actualizaciones)
     */
    public void updateUserFromEditDTO(User user, UserEditDTO editDTO) {
        if (user == null || editDTO == null) {
            return;
        }

        user.setFirstName(editDTO.getFirstName());
        user.setLastName(editDTO.getLastName());
        user.setUsername(editDTO.getUsername());
        user.setEmail(editDTO.getEmail());
        user.setPhone(editDTO.getPhone());
        user.setRole(editDTO.getRole());

        if (editDTO.getIsActive() != null) {
            user.setIsActive(editDTO.getIsActive());
        }
    }

    /**
     * Convertir ProviderManagementDTO a Provider (para actualizaciones)
     */
    public void updateProviderFromManagementDTO(Provider provider, ProviderManagementDTO managementDTO) {
        if (provider == null || managementDTO == null) {
            return;
        }

        if (managementDTO.getVerificationStatus() != null) {
            provider.setVerificationStatus(managementDTO.getVerificationStatus());
        }

        if (managementDTO.getBusinessName() != null) {
            provider.setBusinessName(managementDTO.getBusinessName());
        }

        if (managementDTO.getTaxId() != null) {
            provider.setTaxId(managementDTO.getTaxId());
        }

        if (managementDTO.getAddress() != null) {
            provider.setAddress(managementDTO.getAddress());
        }

        if (managementDTO.getCity() != null) {
            provider.setCity(managementDTO.getCity());
        }

        if (managementDTO.getState() != null) {
            provider.setState(managementDTO.getState());
        }

        if (managementDTO.getCountry() != null) {
            provider.setCountry(managementDTO.getCountry());
        }

        if (managementDTO.getPostalCode() != null) {
            provider.setPostalCode(managementDTO.getPostalCode());
        }

        if (managementDTO.getCommissionRate() != null) {
            provider.setCommissionRate(managementDTO.getCommissionRate());
        }
    }

    /**
     * Convertir UserEditDTO a User básico (para creación)
     */
    public User toUser(UserEditDTO editDTO) {
        if (editDTO == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(editDTO.getFirstName());
        user.setLastName(editDTO.getLastName());
        user.setUsername(editDTO.getUsername());
        user.setEmail(editDTO.getEmail());
        user.setPhone(editDTO.getPhone());
        user.setRole(editDTO.getRole());
        user.setIsActive(editDTO.getIsActive() != null ? editDTO.getIsActive() : true);

        return user;
    }

    /**
     * Convertir Object[] (resultado de query) a UserManagementResponseDTO
     * Útil para queries que retornan User + Provider + estadísticas
     */
    public UserManagementResponseDTO toManagementResponseDTO(Object[] queryResult) {
        if (queryResult == null || queryResult.length < 1) {
            return null;
        }

        User user = (User) queryResult[0];
        UserManagementResponseDTO dto = toManagementResponseDTO(user);

        // Si hay información del proveedor
        if (queryResult.length > 1 && queryResult[1] != null) {
            Provider provider = (Provider) queryResult[1];
            dto.setProviderId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setVerificationStatus(provider.getVerificationStatus());
            dto.setVerificationDate(provider.getVerificationDate());
        }

        // Si hay estadísticas adicionales
        if (queryResult.length > 2 && queryResult[2] != null) {
            Long productCount = (Long) queryResult[2];
            dto.setTotalProducts(productCount.intValue());
        }

        return dto;
    }

    /**
     * Crear UserSearchFilterDTO con valores por defecto
     */
    public UserSearchFilterDTO createDefaultFilter() {
        UserSearchFilterDTO filter = new UserSearchFilterDTO();
        filter.setSortBy("createdAt");
        filter.setSortDirection("desc");
        filter.setPage(0);
        filter.setSize(20);
        return filter;
    }

    /**
     * Convertir filtros de request parameters a UserSearchFilterDTO
     */
    public UserSearchFilterDTO toFilterDTO(String searchQuery,
            String role,
            Boolean isActive,
            String verificationStatus,
            String sortBy,
            String sortDirection,
            Integer page,
            Integer size) {
        UserSearchFilterDTO filter = new UserSearchFilterDTO();

        filter.setSearchQuery(searchQuery);

        // Convertir role string a enum
        if (role != null && !role.isEmpty()) {
            try {
                filter.setRole(UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Si el rol no es válido, se ignora
            }
        }

        filter.setIsActive(isActive);

        // Convertir verification status string a enum
        if (verificationStatus != null && !verificationStatus.isEmpty()) {
            try {
                filter.setVerificationStatus(
                        com.songstock.entity.VerificationStatus.valueOf(verificationStatus.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Si el status no es válido, se ignora
            }
        }

        filter.setSortBy(sortBy != null ? sortBy : "createdAt");
        filter.setSortDirection(sortDirection != null ? sortDirection : "desc");
        filter.setPage(page != null ? page : 0);
        filter.setSize(size != null ? size : 20);

        return filter;
    }

    /**
     * Crear DTO de resumen de usuario (para listas)
     */
    public UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserSummaryDTO(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getIsActive(),
                user.getCreatedAt());
    }

    /**
     * Convertir estadísticas de query a AdminDashboardDTO
     */
    public AdminDashboardDTO toAdminDashboardDTO(Object[] userStats, Object[] providerStats) {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // Estadísticas de usuarios
        if (userStats != null && userStats.length >= 6) {
            dashboard.setTotalUsers((Long) userStats[0]);
            dashboard.setTotalAdmins((Long) userStats[1]);
            dashboard.setTotalProviders((Long) userStats[2]);
            dashboard.setTotalCustomers((Long) userStats[3]);
            dashboard.setActiveUsers((Long) userStats[4]);
            dashboard.setInactiveUsers((Long) userStats[5]);
        }

        // Estadísticas de proveedores
        if (providerStats != null && providerStats.length >= 3) {
            dashboard.setVerifiedProviders((Long) providerStats[0]);
            dashboard.setPendingProviders((Long) providerStats[1]);
            dashboard.setRejectedProviders((Long) providerStats[2]);
        }

        return dashboard;
    }

    /**
     * Validar campos requeridos en UserEditDTO
     */
    public boolean isValidEditDTO(UserEditDTO editDTO) {
        if (editDTO == null) {
            return false;
        }

        return editDTO.getFirstName() != null && !editDTO.getFirstName().trim().isEmpty() &&
                editDTO.getLastName() != null && !editDTO.getLastName().trim().isEmpty() &&
                editDTO.getUsername() != null && !editDTO.getUsername().trim().isEmpty() &&
                editDTO.getEmail() != null && !editDTO.getEmail().trim().isEmpty() &&
                editDTO.getRole() != null;
    }

    /**
     * Sanitizar campos de texto (remover espacios extra, etc.)
     */
    public void sanitizeEditDTO(UserEditDTO editDTO) {
        if (editDTO == null) {
            return;
        }

        if (editDTO.getFirstName() != null) {
            editDTO.setFirstName(editDTO.getFirstName().trim());
        }

        if (editDTO.getLastName() != null) {
            editDTO.setLastName(editDTO.getLastName().trim());
        }

        if (editDTO.getUsername() != null) {
            editDTO.setUsername(editDTO.getUsername().trim().toLowerCase());
        }

        if (editDTO.getEmail() != null) {
            editDTO.setEmail(editDTO.getEmail().trim().toLowerCase());
        }

        if (editDTO.getPhone() != null) {
            editDTO.setPhone(editDTO.getPhone().trim());
        }
    }
}

/**
 * DTO auxiliar para resúmenes de usuarios
 */
class UserSummaryDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private Boolean isActive;
    private java.time.LocalDateTime createdAt;

    public UserSummaryDTO(Long id, String fullName, String username, String email,
            String role, Boolean isActive, java.time.LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}