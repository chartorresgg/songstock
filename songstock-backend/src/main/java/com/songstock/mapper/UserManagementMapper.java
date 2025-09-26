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
 * Mapper responsable de realizar conversiones entre:
 * - Entidades (User, Provider)
 * - DTOs administrativos usados en la capa de servicios/controladores.
 */
@Component
public class UserManagementMapper {

    /**
     * Convierte un objeto User en un UserManagementResponseDTO.
     * Contiene información básica del usuario.
     *
     * @param user entidad User
     * @return UserManagementResponseDTO con datos mapeados
     */
    public UserManagementResponseDTO toManagementResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserManagementResponseDTO dto = new UserManagementResponseDTO();

        // Mapeo de campos básicos del usuario
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
     * Convierte un User y su Provider asociado en un UserManagementResponseDTO
     * completo.
     *
     * @param user     entidad User
     * @param provider entidad Provider asociada al usuario
     * @return UserManagementResponseDTO con información de usuario + proveedor
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
     * Convierte una lista de entidades User en una lista de
     * UserManagementResponseDTO.
     *
     * @param users lista de entidades User
     * @return lista de DTOs convertidos
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
     * Actualiza un objeto User existente con los valores recibidos en UserEditDTO.
     * Se usa típicamente en operaciones de actualización.
     *
     * @param user    entidad User a actualizar
     * @param editDTO DTO con nuevos valores
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
     * Actualiza un objeto Provider existente con los valores de
     * ProviderManagementDTO.
     *
     * @param provider      entidad Provider a actualizar
     * @param managementDTO DTO con datos administrativos de provider
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
     * Convierte un UserEditDTO en una entidad User (usado en creación).
     *
     * @param editDTO DTO con información del usuario
     * @return entidad User
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
     * Convierte un arreglo de objetos (resultado de query compleja) en
     * UserManagementResponseDTO.
     * El arreglo suele contener: User, Provider y estadísticas asociadas.
     *
     * @param queryResult resultado de query en forma de Object[]
     * @return DTO con datos de usuario y proveedor
     */
    public UserManagementResponseDTO toManagementResponseDTO(Object[] queryResult) {
        if (queryResult == null || queryResult.length < 1) {
            return null;
        }

        User user = (User) queryResult[0];
        UserManagementResponseDTO dto = toManagementResponseDTO(user);

        // Si incluye información de Provider
        if (queryResult.length > 1 && queryResult[1] != null) {
            Provider provider = (Provider) queryResult[1];
            dto.setProviderId(provider.getId());
            dto.setBusinessName(provider.getBusinessName());
            dto.setVerificationStatus(provider.getVerificationStatus());
            dto.setVerificationDate(provider.getVerificationDate());
        }

        // Si incluye estadísticas de productos
        if (queryResult.length > 2 && queryResult[2] != null) {
            Long productCount = (Long) queryResult[2];
            dto.setTotalProducts(productCount.intValue());
        }

        return dto;
    }

    /**
     * Crea un filtro de búsqueda de usuarios con valores por defecto.
     *
     * @return filtro con configuración inicial
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
     * Convierte parámetros de request en un objeto UserSearchFilterDTO.
     *
     * @param searchQuery        texto de búsqueda
     * @param role               rol (string -> enum)
     * @param isActive           estado activo/inactivo
     * @param verificationStatus estado de verificación (string -> enum)
     * @param sortBy             campo de ordenamiento
     * @param sortDirection      dirección de ordenamiento (asc/desc)
     * @param page               número de página
     * @param size               tamaño de página
     * @return filtro de búsqueda
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

        // Validar y convertir role a enum
        if (role != null && !role.isEmpty()) {
            try {
                filter.setRole(UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Rol inválido → se ignora
            }
        }

        filter.setIsActive(isActive);

        // Validar y convertir verificationStatus a enum
        if (verificationStatus != null && !verificationStatus.isEmpty()) {
            try {
                filter.setVerificationStatus(
                        com.songstock.entity.VerificationStatus.valueOf(verificationStatus.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Estado inválido → se ignora
            }
        }

        filter.setSortBy(sortBy != null ? sortBy : "createdAt");
        filter.setSortDirection(sortDirection != null ? sortDirection : "desc");
        filter.setPage(page != null ? page : 0);
        filter.setSize(size != null ? size : 20);

        return filter;
    }

    /**
     * Convierte un User a un DTO de resumen con información básica.
     *
     * @param user entidad User
     * @return DTO con resumen de datos
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
     * Convierte estadísticas crudas de queries en un AdminDashboardDTO.
     *
     * @param userStats     estadísticas de usuarios
     * @param providerStats estadísticas de proveedores
     * @return DTO con resumen del dashboard administrativo
     */
    public AdminDashboardDTO toAdminDashboardDTO(Object[] userStats, Object[] providerStats) {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // Datos de usuarios
        if (userStats != null && userStats.length >= 6) {
            dashboard.setTotalUsers((Long) userStats[0]);
            dashboard.setTotalAdmins((Long) userStats[1]);
            dashboard.setTotalProviders((Long) userStats[2]);
            dashboard.setTotalCustomers((Long) userStats[3]);
            dashboard.setActiveUsers((Long) userStats[4]);
            dashboard.setInactiveUsers((Long) userStats[5]);
        }

        // Datos de proveedores
        if (providerStats != null && providerStats.length >= 3) {
            dashboard.setVerifiedProviders((Long) providerStats[0]);
            dashboard.setPendingProviders((Long) providerStats[1]);
            dashboard.setRejectedProviders((Long) providerStats[2]);
        }

        return dashboard;
    }

    /**
     * Valida si un DTO de edición de usuario tiene los campos requeridos.
     *
     * @param editDTO DTO a validar
     * @return true si es válido, false en caso contrario
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
     * Normaliza valores de texto de un UserEditDTO
     * (ejemplo: eliminar espacios, pasar a minúsculas).
     *
     * @param editDTO DTO a sanitizar
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
 * DTO auxiliar para representar un resumen de usuario
 * en listados o reportes básicos.
 */
class UserSummaryDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private Boolean isActive;
    private java.time.LocalDateTime createdAt;

    /**
     * Constructor con todos los parámetros.
     */
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

    // Getters y setters ----------------------

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
