package com.songstock.controller;

import com.songstock.dto.*;
import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import com.songstock.service.AdminUserService;
import com.songstock.util.ApiResponse;
import com.songstock.mapper.UserManagementMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin User Management", description = "Gestión administrativa de usuarios, compradores y proveedores")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserManagementMapper userManagementMapper;

    // ========== GESTIÓN GENERAL DE USUARIOS ==========

    /**
     * ENDPOINT PRINCIPAL: Obtener todos los usuarios con filtros
     * GET /api/v1/admin/users
     */
    @GetMapping
    @Operation(summary = "Listar usuarios con filtros", description = "Obtiene todos los usuarios con filtros avanzados, paginación y ordenamiento")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<Page<UserManagementResponseDTO>>> getAllUsers(
            @Parameter(description = "Búsqueda en nombre, username, email, business name") @RequestParam(required = false) String searchQuery,

            @Parameter(description = "Filtrar por rol: ADMIN, PROVIDER, CUSTOMER") @RequestParam(required = false) String role,

            @Parameter(description = "Filtrar por estado activo") @RequestParam(required = false) Boolean isActive,

            @Parameter(description = "Filtrar por estado de verificación de proveedores") @RequestParam(required = false) String verificationStatus,

            @Parameter(description = "Usuarios creados después de esta fecha") @RequestParam(required = false) LocalDateTime createdAfter,

            @Parameter(description = "Usuarios creados antes de esta fecha") @RequestParam(required = false) LocalDateTime createdBefore,

            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Dirección del ordenamiento: asc, desc") @RequestParam(defaultValue = "desc") String sortDirection,

            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") Integer size) {

        logger.info("REST request para obtener usuarios - query: {}, role: {}, isActive: {}",
                searchQuery, role, isActive);

        try {
            // Crear filtro DTO
            UserSearchFilterDTO filterDTO = userManagementMapper.toFilterDTO(
                    searchQuery, role, isActive, verificationStatus,
                    sortBy, sortDirection, page, size);

            filterDTO.setCreatedAfter(createdAfter);
            filterDTO.setCreatedBefore(createdBefore);

            // Obtener usuarios filtrados
            Page<UserManagementResponseDTO> users = adminUserService.getAllUsers(filterDTO);

            String message = users.isEmpty()
                    ? "No se encontraron usuarios con los filtros especificados"
                    : String.format("Se encontraron %d usuario(s) en total", users.getTotalElements());

            return ResponseEntity.ok(ApiResponse.success(message, users));

        } catch (Exception e) {
            logger.error("Error al obtener usuarios con filtros", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuarios: " + e.getMessage(), null));
        }
    }

    /**
     * Obtener usuario específico por ID
     * GET /api/v1/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene información completa de un usuario específico incluyendo datos de proveedor")
    public ResponseEntity<ApiResponse<UserManagementResponseDTO>> getUserById(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId) {

        logger.info("REST request para obtener usuario por ID: {}", userId);

        try {
            UserManagementResponseDTO user = adminUserService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("Usuario obtenido exitosamente", user));

        } catch (Exception e) {
            logger.error("Error al obtener usuario ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuario: " + e.getMessage(), null));
        }
    }

    /**
     * Editar usuario existente
     * PUT /api/v1/admin/users/{userId}
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Editar usuario", description = "Actualiza la información de un usuario existente")
    public ResponseEntity<ApiResponse<UserManagementResponseDTO>> updateUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId,

            @Parameter(description = "Datos actualizados del usuario", required = true) @Valid @RequestBody UserEditDTO editDTO) {

        logger.info("REST request para actualizar usuario ID: {} con username: {}", userId, editDTO.getUsername());

        try {
            // Sanitizar datos de entrada
            userManagementMapper.sanitizeEditDTO(editDTO);

            // Actualizar usuario
            UserManagementResponseDTO updatedUser = adminUserService.updateUser(userId, editDTO);

            return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", updatedUser));

        } catch (Exception e) {
            logger.error("Error al actualizar usuario ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar usuario: " + e.getMessage(), null));
        }
    }

    /**
     * Activar/Desactivar usuario
     * PATCH /api/v1/admin/users/{userId}/toggle-status
     */
    @PatchMapping("/{userId}/toggle-status")
    @Operation(summary = "Activar/Desactivar usuario", description = "Cambia el estado activo/inactivo de un usuario")
    public ResponseEntity<ApiResponse<UserManagementResponseDTO>> toggleUserStatus(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId,

            @Parameter(description = "Razón del cambio") @RequestParam(required = false) String reason) {

        logger.info("REST request para cambiar estado del usuario ID: {}", userId);

        try {
            UserManagementResponseDTO updatedUser = adminUserService.toggleUserStatus(userId, reason);

            String message = updatedUser.getIsActive()
                    ? "Usuario activado exitosamente"
                    : "Usuario desactivado exitosamente";

            return ResponseEntity.ok(ApiResponse.success(message, updatedUser));

        } catch (Exception e) {
            logger.error("Error al cambiar estado del usuario ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al cambiar estado: " + e.getMessage(), null));
        }
    }

    /**
     * Eliminar usuario (soft delete)
     * DELETE /api/v1/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId,

            @Parameter(description = "Razón de la eliminación") @RequestParam(required = false) String reason) {

        logger.info("REST request para eliminar usuario ID: {}", userId);

        try {
            adminUserService.deleteUser(userId, reason);
            return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente", null));

        } catch (Exception e) {
            logger.error("Error al eliminar usuario ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al eliminar usuario: " + e.getMessage(), null));
        }
    }

    // ========== GESTIÓN ESPECÍFICA DE PROVEEDORES ==========

    /**
     * Obtener proveedores pendientes de verificación
     * GET /api/v1/admin/users/providers/pending
     */
    @GetMapping("/providers/pending")
    @Operation(summary = "Proveedores pendientes", description = "Obtiene la lista de proveedores pendientes de verificación")
    public ResponseEntity<ApiResponse<List<UserManagementResponseDTO>>> getPendingProviders() {

        logger.info("REST request para obtener proveedores pendientes de verificación");

        try {
            List<UserManagementResponseDTO> pendingProviders = adminUserService.getPendingProviders();

            String message = pendingProviders.isEmpty()
                    ? "No hay proveedores pendientes de verificación"
                    : String.format("Se encontraron %d proveedor(es) pendiente(s)", pendingProviders.size());

            return ResponseEntity.ok(ApiResponse.success(message, pendingProviders));

        } catch (Exception e) {
            logger.error("Error al obtener proveedores pendientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener proveedores pendientes: " + e.getMessage(), null));
        }
    }

    /**
     * Actualizar verificación de proveedor
     * PUT /api/v1/admin/users/{userId}/provider/verification
     */
    @PutMapping("/{userId}/provider/verification")
    @Operation(summary = "Actualizar verificación de proveedor", description = "Cambia el estado de verificación de un proveedor")
    public ResponseEntity<ApiResponse<UserManagementResponseDTO>> updateProviderVerification(
            @Parameter(description = "ID del usuario proveedor", required = true) @PathVariable Long userId,

            @Parameter(description = "Datos de gestión del proveedor", required = true) @Valid @RequestBody ProviderManagementDTO managementDTO) {

        logger.info("REST request para actualizar verificación del proveedor - Usuario ID: {}, Estado: {}",
                userId, managementDTO.getVerificationStatus());

        try {
            UserManagementResponseDTO updatedUser = adminUserService.updateProviderVerification(userId, managementDTO);

            String message = String.format("Verificación de proveedor actualizada a: %s",
                    managementDTO.getVerificationStatus());

            return ResponseEntity.ok(ApiResponse.success(message, updatedUser));

        } catch (Exception e) {
            logger.error("Error al actualizar verificación del proveedor - Usuario ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar verificación: " + e.getMessage(), null));
        }
    }

    // ========== BÚSQUEDAS Y CONSULTAS ==========

    /**
     * Buscar usuarios con texto libre
     * GET /api/v1/admin/users/search
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios", description = "Búsqueda de texto libre en usuarios y proveedores")
    public ResponseEntity<ApiResponse<List<UserManagementResponseDTO>>> searchUsers(
            @Parameter(description = "Término de búsqueda", required = true) @RequestParam String query) {

        logger.info("REST request para buscar usuarios con query: {}", query);

        try {
            List<UserManagementResponseDTO> users = adminUserService.searchUsers(query);

            String message = users.isEmpty()
                    ? "No se encontraron usuarios para la búsqueda: " + query
                    : String.format("Se encontraron %d usuario(s)", users.size());

            return ResponseEntity.ok(ApiResponse.success(message, users));

        } catch (Exception e) {
            logger.error("Error al buscar usuarios con query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en búsqueda: " + e.getMessage(), null));
        }
    }

    /**
     * Obtener usuarios registrados recientemente
     * GET /api/v1/admin/users/recent
     */
    @GetMapping("/recent")
    @Operation(summary = "Usuarios recientes", description = "Obtiene usuarios registrados en los últimos N días")
    public ResponseEntity<ApiResponse<List<UserManagementResponseDTO>>> getRecentUsers(
            @Parameter(description = "Número de días hacia atrás") @RequestParam(defaultValue = "7") int days) {

        logger.info("REST request para obtener usuarios registrados en los últimos {} días", days);

        try {
            List<UserManagementResponseDTO> users = adminUserService.getRecentUsers(days);

            String message = users.isEmpty()
                    ? String.format("No hay usuarios registrados en los últimos %d días", days)
                    : String.format("Se encontraron %d usuario(s) reciente(s)", users.size());

            return ResponseEntity.ok(ApiResponse.success(message, users));

        } catch (Exception e) {
            logger.error("Error al obtener usuarios recientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuarios recientes: " + e.getMessage(), null));
        }
    }

    /**
     * Obtener top proveedores
     * GET /api/v1/admin/users/providers/top
     */
    @GetMapping("/providers/top")
    @Operation(summary = "Top proveedores", description = "Obtiene los proveedores con mayor número de productos")
    public ResponseEntity<ApiResponse<List<UserManagementResponseDTO>>> getTopProviders(
            @Parameter(description = "Número de proveedores a retornar") @RequestParam(defaultValue = "10") int limit) {

        logger.info("REST request para obtener top {} proveedores", limit);

        try {
            List<UserManagementResponseDTO> topProviders = adminUserService.getTopProviders(limit);

            String message = topProviders.isEmpty()
                    ? "No se encontraron proveedores con productos"
                    : String.format("Top %d proveedores obtenidos", topProviders.size());

            return ResponseEntity.ok(ApiResponse.success(message, topProviders));

        } catch (Exception e) {
            logger.error("Error al obtener top proveedores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener top proveedores: " + e.getMessage(), null));
        }
    }

    // ========== DASHBOARD Y ESTADÍSTICAS ==========

    /**
     * ENDPOINT PARA DASHBOARD: Obtener estadísticas administrativas
     * GET /api/v1/admin/users/dashboard/statistics
     */
    @GetMapping("/dashboard/statistics")
    @Operation(summary = "Estadísticas del dashboard", description = "Obtiene todas las estadísticas para el dashboard administrativo")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboardStatistics() {

        logger.info("REST request para obtener estadísticas del dashboard administrativo");

        try {
            AdminDashboardDTO dashboard = adminUserService.getDashboardStatistics();
            return ResponseEntity.ok(ApiResponse.success("Estadísticas generadas exitosamente", dashboard));

        } catch (Exception e) {
            logger.error("Error al generar estadísticas del dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al generar estadísticas: " + e.getMessage(), null));
        }
    }

    // ========== ENDPOINTS DE UTILIDAD ==========

    @GetMapping("/by-role/{role}")
    @Operation(summary = "Usuarios por rol", description = "Obtiene usuarios filtrados por rol específico")
    public ResponseEntity<ApiResponse<List<UserManagementResponseDTO>>> getUsersByRole(
            @Parameter(description = "Rol del usuario: ADMIN, PROVIDER, CUSTOMER", required = true) @PathVariable String role) {

        logger.info("REST request para obtener usuarios por rol: {}", role);

        try {
            // Crear filtro para rol específico
            UserSearchFilterDTO filterDTO = userManagementMapper.createDefaultFilter();
            try {
                filterDTO.setRole(UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Rol inválido: " + role + ". Roles válidos: ADMIN, PROVIDER, CUSTOMER",
                                null));
            }

            Page<UserManagementResponseDTO> users = adminUserService.getAllUsers(filterDTO);

            String message = users.isEmpty()
                    ? "No se encontraron usuarios con rol: " + role
                    : String.format("Se encontraron %d usuario(s) con rol %s", users.getTotalElements(), role);

            return ResponseEntity.ok(ApiResponse.success(message, users.getContent()));

        } catch (Exception e) {
            logger.error("Error al obtener usuarios por rol: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener usuarios por rol: " + e.getMessage(), null));
        }
    }

    /**
     * Endpoint para obtener métricas rápidas
     * GET /api/v1/admin/users/metrics/quick
     */
    @GetMapping("/metrics/quick")
    @Operation(summary = "Métricas rápidas", description = "Obtiene métricas básicas de usuarios para widgets del dashboard")
    public ResponseEntity<ApiResponse<QuickMetricsDTO>> getQuickMetrics() {

        logger.info("REST request para obtener métricas rápidas");

        try {
            AdminDashboardDTO dashboard = adminUserService.getDashboardStatistics();

            // Crear DTO simplificado con métricas clave
            QuickMetricsDTO metrics = new QuickMetricsDTO();
            metrics.setTotalUsers(dashboard.getTotalUsers());
            metrics.setActiveUsers(dashboard.getActiveUsers());
            metrics.setPendingProviders(dashboard.getPendingProviders());
            metrics.setVerifiedProviders(dashboard.getVerifiedProviders());
            metrics.setUsersThisMonth(dashboard.getUsersRegisteredThisMonth());

            return ResponseEntity.ok(ApiResponse.success("Métricas rápidas obtenidas", metrics));

        } catch (Exception e) {
            logger.error("Error al obtener métricas rápidas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener métricas: " + e.getMessage(), null));
        }
    }
}