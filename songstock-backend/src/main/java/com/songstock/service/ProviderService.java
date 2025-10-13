package com.songstock.service;

import com.songstock.dto.ProviderRegistrationDTO;
import com.songstock.dto.UserRegistrationDTO;
import com.songstock.dto.ProviderInvitationDTO;
import com.songstock.dto.CompleteRegistrationDTO;
import com.songstock.dto.AuthResponseDTO;
import com.songstock.dto.LoginRequestDTO;
import com.songstock.entity.Provider;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    // ================= MÉTODOS DE CONSULTA =================

    @Transactional(readOnly = true)
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
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
}