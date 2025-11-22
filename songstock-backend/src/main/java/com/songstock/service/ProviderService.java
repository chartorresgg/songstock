package com.songstock.service;

import com.songstock.dto.ProviderRegistrationDTO;
import com.songstock.dto.UserRegistrationDTO;
import com.songstock.dto.ProviderInvitationDTO;
import com.songstock.dto.CompleteRegistrationDTO;
import com.songstock.dto.AuthResponseDTO;
import com.songstock.dto.ProviderSalesReportDTO;
import com.songstock.dto.ProviderSalesReportDTO.TopProductDTO;
import com.songstock.dto.LoginRequestDTO;
import com.songstock.entity.OrderItem;
import com.songstock.entity.OrderItemStatus;
import com.songstock.repository.OrderItemRepository;
import com.songstock.entity.Provider;
import com.songstock.dto.ProviderListDTO;
import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import com.songstock.entity.ProviderInvitation;
import com.songstock.entity.InvitationStatus;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.exception.ResourceAlreadyExistsException;
import com.songstock.repository.ProviderRepository;
import com.songstock.repository.ProviderInvitationRepository;
import com.songstock.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.math.BigDecimal;

@Service
@Transactional
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final UserService userService;
    private final ProviderInvitationRepository invitationRepository;
    private final AuthService authService;

    @Autowired
    public ProviderService(ProviderRepository providerRepository,
            UserService userService,
            ProviderInvitationRepository invitationRepository,
            AuthService authService) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.invitationRepository = invitationRepository;
        this.authService = authService;
    }

    @Autowired
    private OrderItemRepository orderItemRepository;

    // ================= MÉTODOS DE CONSULTA =================

    @Transactional(readOnly = true)
    public List<ProviderListDTO> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream()
                .map(this::toProviderListDTO)
                .collect(Collectors.toList());
    }

    private ProviderListDTO toProviderListDTO(Provider provider) {
        ProviderListDTO dto = new ProviderListDTO();
        dto.setId(provider.getId());
        dto.setBusinessName(provider.getBusinessName());
        dto.setTaxId(provider.getTaxId());
        dto.setCity(provider.getCity());
        dto.setState(provider.getState());
        dto.setCountry(provider.getCountry());
        dto.setVerificationStatus(provider.getVerificationStatus());
        dto.setCommissionRate(provider.getCommissionRate());
        dto.setCreatedAt(provider.getCreatedAt());
        dto.setUpdatedAt(provider.getUpdatedAt());

        User user = provider.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Provider getProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado para usuario ID: " + userId));
    }

    @Transactional(readOnly = true)
    public List<Provider> getProvidersByStatus(VerificationStatus status) {
        return providerRepository.findByVerificationStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Provider> getVerifiedProviders() {
        return providerRepository.findByVerificationStatus(VerificationStatus.VERIFIED);
    }

    @Transactional(readOnly = true)
    public List<Provider> getPendingProviders() {
        return providerRepository.findByVerificationStatus(VerificationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public Long getVerifiedProvidersCount() {
        return providerRepository.countVerifiedProviders();
    }

    // ================= MÉTODOS DE REGISTRO TRADICIONAL =================

    /**
     * Auto-registro de proveedor (queda PENDING hasta aprobación de admin)
     */
    public Provider registerProvider(ProviderRegistrationDTO providerDTO) {
        // Crear usuario primero
        User user = userService.createUser(new UserRegistrationDTO(
                providerDTO.getUsername(),
                providerDTO.getEmail(),
                providerDTO.getPassword(),
                providerDTO.getFirstName(),
                providerDTO.getLastName(),
                UserRole.PROVIDER));

        // Crear proveedor
        Provider provider = new Provider();
        provider.setUser(user);
        provider.setBusinessName(providerDTO.getBusinessName());
        provider.setTaxId(providerDTO.getTaxId());
        provider.setAddress(providerDTO.getAddress());
        provider.setCity(providerDTO.getCity());
        provider.setState(providerDTO.getState());
        provider.setPostalCode(providerDTO.getPostalCode());
        provider.setCountry(providerDTO.getCountry());
        provider.setVerificationStatus(VerificationStatus.VERIFIED);
        provider.setVerificationDate(LocalDateTime.now());

        return providerRepository.save(provider);
    }

    // ================= MÉTODOS DE REGISTRO POR ADMIN =================

    /**
     * Admin registra proveedor directamente (estado VERIFIED inmediato)
     */
    public Provider registerProviderByAdmin(ProviderRegistrationDTO providerDTO) {
        // Crear usuario directamente
        User user = userService.createUser(new UserRegistrationDTO(
                providerDTO.getUsername(),
                providerDTO.getEmail(),
                providerDTO.getPassword(),
                providerDTO.getFirstName(),
                providerDTO.getLastName(),
                UserRole.PROVIDER));

        // Crear proveedor
        Provider provider = new Provider();
        provider.setUser(user);
        provider.setBusinessName(providerDTO.getBusinessName());
        provider.setTaxId(providerDTO.getTaxId());
        provider.setAddress(providerDTO.getAddress());
        provider.setCity(providerDTO.getCity());
        provider.setState(providerDTO.getState());
        provider.setPostalCode(providerDTO.getPostalCode());
        provider.setCountry(providerDTO.getCountry());

        // Admin registra directamente como VERIFIED
        provider.setVerificationStatus(VerificationStatus.VERIFIED);
        provider.setVerificationDate(LocalDateTime.now());

        return providerRepository.save(provider);
    }

    // ================= MÉTODOS DE INVITACIÓN =================

    /**
     * Admin envía invitación a proveedor potencial
     */
    public void inviteProvider(ProviderInvitationDTO invitationDTO) {
        // Verificar que el email no esté ya registrado
        if (userService.existsByEmail(invitationDTO.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Ya existe un usuario con este email: " + invitationDTO.getEmail());
        }

        // Verificar que no haya invitación pendiente
        if (invitationRepository.existsByEmail(invitationDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Ya existe una invitación pendiente para este email");
        }

        // Obtener admin actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl adminDetails = (UserDetailsImpl) auth.getPrincipal();

        // Generar token único
        String invitationToken = UUID.randomUUID().toString();

        // Crear invitación
        ProviderInvitation invitation = new ProviderInvitation(
                invitationDTO.getEmail(),
                invitationDTO.getBusinessName(),
                invitationDTO.getFirstName(),
                invitationDTO.getLastName(),
                adminDetails.getId(),
                invitationToken,
                LocalDateTime.now().plusDays(7) // Expira en 7 días
        );

        invitation.setPhone(invitationDTO.getPhone());
        invitation.setMessage(invitationDTO.getMessage());

        invitationRepository.save(invitation);

        // TODO: Enviar email con el link de invitación
        // sendInvitationEmail(invitation);

        System.out.println("🔗 Link de invitación: http://localhost:3000/complete-registration/" + invitationToken);
    }

    /**
     * Proveedor completa su registro usando el token de invitación
     */
    public AuthResponseDTO completeRegistration(String token, CompleteRegistrationDTO registrationDTO,
            HttpServletRequest request) {
        // Buscar invitación
        ProviderInvitation invitation = invitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));

        // Verificar que no haya expirado
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new ResourceNotFoundException("La invitación ha expirado");
        }

        // Verificar que esté pendiente
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new ResourceAlreadyExistsException("Esta invitación ya fue utilizada");
        }

        // Crear usuario
        User user = userService.createUser(new UserRegistrationDTO(
                registrationDTO.getUsername(),
                invitation.getEmail(),
                registrationDTO.getPassword(),
                invitation.getFirstName(),
                invitation.getLastName(),
                UserRole.PROVIDER));

        // Crear proveedor
        Provider provider = new Provider();
        provider.setUser(user);
        provider.setBusinessName(invitation.getBusinessName());
        provider.setTaxId(registrationDTO.getTaxId());
        provider.setAddress(registrationDTO.getAddress());
        provider.setCity(registrationDTO.getCity());
        provider.setState(registrationDTO.getState());
        provider.setPostalCode(registrationDTO.getPostalCode());
        provider.setCountry(registrationDTO.getCountry());
        provider.setVerificationStatus(VerificationStatus.VERIFIED); // Pre-aprobado por admin
        provider.setVerificationDate(LocalDateTime.now());

        providerRepository.save(provider);

        // Marcar invitación como completada
        invitation.setStatus(InvitationStatus.COMPLETED);
        invitation.setCompletedBy(user.getId());
        invitation.setCompletedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        // Autenticar automáticamente al nuevo proveedor
        LoginRequestDTO loginRequest = new LoginRequestDTO(user.getUsername(), registrationDTO.getPassword());
        return authService.authenticateUser(loginRequest, request);
    }

    /**
     * Obtener invitación por token (para validar en frontend)
     */
    @Transactional(readOnly = true)
    public ProviderInvitation getInvitationByToken(String token) {
        ProviderInvitation invitation = invitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));

        // Verificar expiración
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now()) &&
                invitation.getStatus() == InvitationStatus.PENDING) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
        }

        return invitation;
    }

    /**
     * Listar todas las invitaciones
     */
    @Transactional(readOnly = true)
    public List<ProviderInvitation> getProviderInvitations() {
        return invitationRepository.findAll();
    }

    /**
     * Cancelar invitación
     */
    public void cancelInvitation(Long invitationId) {
        ProviderInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));

        if (invitation.getStatus() == InvitationStatus.PENDING) {
            invitation.setStatus(InvitationStatus.CANCELLED);
            invitationRepository.save(invitation);
        } else {
            throw new IllegalStateException("Solo se pueden cancelar invitaciones pendientes");
        }
    }

    // ================= MÉTODOS DE GESTIÓN =================

    /**
     * Actualizar información del proveedor
     */
    public Provider updateProvider(Long id, ProviderRegistrationDTO providerDTO) {
        Provider existingProvider = getProviderById(id);

        existingProvider.setBusinessName(providerDTO.getBusinessName());
        existingProvider.setTaxId(providerDTO.getTaxId());
        existingProvider.setAddress(providerDTO.getAddress());
        existingProvider.setCity(providerDTO.getCity());
        existingProvider.setState(providerDTO.getState());
        existingProvider.setPostalCode(providerDTO.getPostalCode());
        existingProvider.setCountry(providerDTO.getCountry());

        return providerRepository.save(existingProvider);
    }

    /**
     * Verificar proveedor (cambiar estado a VERIFIED)
     */
    public Provider verifyProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setVerificationStatus(VerificationStatus.VERIFIED);
        provider.setVerificationDate(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    /**
     * Rechazar proveedor (cambiar estado a REJECTED)
     */
    public Provider rejectProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setVerificationStatus(VerificationStatus.REJECTED);
        provider.setVerificationDate(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    /**
     * Eliminar proveedor
     */
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }
        providerRepository.deleteById(id);
    }

    // ================= MÉTODOS DE UTILIDAD =================

    /**
     * Limpiar invitaciones expiradas (tarea programada)
     */
    public void cleanupExpiredInvitations() {
        List<ProviderInvitation> expired = invitationRepository.findExpiredInvitations(LocalDateTime.now());
        expired.forEach(invitation -> {
            invitation.setStatus(InvitationStatus.EXPIRED);
        });
        invitationRepository.saveAll(expired);
    }

    /**
     * Verificar si un email tiene invitación pendiente
     */
    @Transactional(readOnly = true)
    public boolean hasePendingInvitation(String email) {
        return invitationRepository.existsByEmail(email);
    }

    /**
     * Obtener estadísticas de proveedores
     */
    @Transactional(readOnly = true)
    public ProviderStats getProviderStats() {
        long totalProviders = providerRepository.count();
        long verifiedProviders = getVerifiedProvidersCount();
        long pendingProviders = providerRepository.findByVerificationStatus(VerificationStatus.PENDING).size();
        long rejectedProviders = providerRepository.findByVerificationStatus(VerificationStatus.REJECTED).size();
        long pendingInvitations = invitationRepository.findByStatus(InvitationStatus.PENDING).size();

        return new ProviderStats(totalProviders, verifiedProviders, pendingProviders,
                rejectedProviders, pendingInvitations);
    }

    // ================= CLASE INTERNA PARA ESTADÍSTICAS =================
    public static class ProviderStats {
        private final long totalProviders;
        private final long verifiedProviders;
        private final long pendingProviders;
        private final long rejectedProviders;
        private final long pendingInvitations;

        public ProviderStats(long totalProviders, long verifiedProviders, long pendingProviders,
                long rejectedProviders, long pendingInvitations) {
            this.totalProviders = totalProviders;
            this.verifiedProviders = verifiedProviders;
            this.pendingProviders = pendingProviders;
            this.rejectedProviders = rejectedProviders;
            this.pendingInvitations = pendingInvitations;
        }

        // Getters
        public long getTotalProviders() {
            return totalProviders;
        }

        public long getVerifiedProviders() {
            return verifiedProviders;
        }

        public long getPendingProviders() {
            return pendingProviders;
        }

        public long getRejectedProviders() {
            return rejectedProviders;
        }

        public long getPendingInvitations() {
            return pendingInvitations;
        }

        public double getVerificationRate() {
            return totalProviders > 0 ? (double) verifiedProviders / totalProviders * 100 : 0;
        }
    }

    /**
     * Obtener reporte de ventas de un proveedor
     */
    @Transactional(readOnly = true)
    public ProviderSalesReportDTO getSalesReport(Long providerId) {
        Provider provider = getProviderById(providerId);

        ProviderSalesReportDTO report = new ProviderSalesReportDTO();
        report.setProviderId(providerId);
        report.setProviderBusinessName(provider.getBusinessName());

        // Obtener métricas generales
        BigDecimal totalSales = orderItemRepository.getTotalSalesByProvider(providerId);
        BigDecimal totalRevenue = orderItemRepository.getTotalRevenueByProvider(providerId);
        Long totalOrders = orderItemRepository.countOrdersByProvider(providerId);
        Long completedItems = orderItemRepository.countCompletedItemsByProvider(providerId);
        Long pendingItems = orderItemRepository.countPendingItemsByProvider(providerId);

        report.setTotalSales(totalSales != null ? totalSales : java.math.BigDecimal.ZERO);
        report.setTotalRevenue(totalRevenue != null ? totalRevenue : java.math.BigDecimal.ZERO);
        report.setTotalOrders(totalOrders != null ? totalOrders : 0L);
        report.setCompletedItems(completedItems != null ? completedItems : 0L);
        report.setPendingItems(pendingItems != null ? pendingItems : 0L);

        // Calcular promedio de orden
        if (totalOrders != null && totalOrders > 0 && totalRevenue != null) {
            java.math.BigDecimal avgOrderValue = totalRevenue.divide(
                    java.math.BigDecimal.valueOf(totalOrders),
                    2,
                    RoundingMode.HALF_UP);
            report.setAverageOrderValue(avgOrderValue);
        } else {
            report.setAverageOrderValue(java.math.BigDecimal.ZERO);
        }

        // Calcular top productos
        List<OrderItem> allItems = orderItemRepository.findByProviderId(providerId);
        java.util.Map<Long, ProductSalesData> productSalesMap = new HashMap<>();

        for (OrderItem item : allItems) {
            if (item.getStatus() == OrderItemStatus.SHIPPED || item.getStatus() == OrderItemStatus.DELIVERED) {
                Long productId = item.getProduct().getId();
                ProductSalesData data = productSalesMap.getOrDefault(
                        productId,
                        new ProductSalesData(
                                productId,
                                item.getProduct().getAlbum().getTitle(),
                                item.getProduct().getAlbum().getArtist().getName()));
                data.addSale(item.getQuantity(), item.getSubtotal());
                productSalesMap.put(productId, data);
            }
        }

        List<TopProductDTO> topProducts = productSalesMap.values().stream()
                .sorted(Comparator.comparing(ProductSalesData::getRevenue).reversed())
                .limit(5)
                .map(data -> new TopProductDTO(
                        data.productId,
                        data.albumTitle,
                        data.artistName,
                        data.quantitySold,
                        data.revenue))
                .collect(Collectors.toList());

        report.setTopProducts(topProducts);

        return report;
    }

    // Clase auxiliar para cálculo de top productos
    private static class ProductSalesData {
        Long productId;
        String albumTitle;
        String artistName;
        Long quantitySold = 0L;
        java.math.BigDecimal revenue = java.math.BigDecimal.ZERO;

        ProductSalesData(Long productId, String albumTitle, String artistName) {
            this.productId = productId;
            this.albumTitle = albumTitle;
            this.artistName = artistName;
        }

        void addSale(Integer quantity, java.math.BigDecimal subtotal) {
            this.quantitySold += quantity;
            this.revenue = this.revenue.add(subtotal);
        }

        Long getQuantitySold() {
            return quantitySold;
        }

        java.math.BigDecimal getRevenue() {
            return revenue;
        }
    }

}