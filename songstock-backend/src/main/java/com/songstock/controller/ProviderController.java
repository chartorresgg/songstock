package com.songstock.controller;

import com.songstock.dto.ApiResponse;
import com.songstock.dto.ProviderRegistrationDTO;
import com.songstock.entity.Provider;
import com.songstock.entity.VerificationStatus;
import com.songstock.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.songstock.dto.ProviderInvitationDTO;
import com.songstock.dto.CompleteRegistrationDTO;
import com.songstock.dto.AuthResponseDTO;
import com.songstock.entity.ProviderInvitation;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Provider>>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        return ResponseEntity.ok(ApiResponse.success("Proveedores obtenidos exitosamente", providers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @providerService.getProviderById(#id).user.id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Provider>> getProviderById(@PathVariable Long id) {
        Provider provider = providerService.getProviderById(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor encontrado", provider));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Provider>> getProviderByUserId(@PathVariable Long userId) {
        Provider provider = providerService.getProviderByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Proveedor encontrado", provider));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Provider>>> getProvidersByStatus(@PathVariable VerificationStatus status) {
        List<Provider> providers = providerService.getProvidersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Proveedores obtenidos exitosamente", providers));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<Provider>>> getVerifiedProviders() {
        List<Provider> providers = providerService.getVerifiedProviders();
        return ResponseEntity.ok(ApiResponse.success("Proveedores verificados obtenidos exitosamente", providers));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Provider>>> getPendingProviders() {
        List<Provider> providers = providerService.getPendingProviders();
        return ResponseEntity.ok(ApiResponse.success("Proveedores pendientes obtenidos exitosamente", providers));
    }

    @GetMapping("/count/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getVerifiedProvidersCount() {
        Long count = providerService.getVerifiedProvidersCount();
        return ResponseEntity.ok(ApiResponse.success("Conteo de proveedores verificados", count));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @providerService.getProviderById(#id).user.id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Provider>> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody ProviderRegistrationDTO providerDTO) {
        Provider updatedProvider = providerService.updateProvider(id, providerDTO);
        return ResponseEntity.ok(ApiResponse.success("Proveedor actualizado exitosamente", updatedProvider));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Provider>> verifyProvider(@PathVariable Long id) {
        Provider verifiedProvider = providerService.verifyProvider(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor verificado exitosamente", verifiedProvider));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Provider>> rejectProvider(@PathVariable Long id) {
        Provider rejectedProvider = providerService.rejectProvider(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor rechazado exitosamente", rejectedProvider));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor eliminado exitosamente"));
    }

    @PostMapping("/register-by-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Provider>> registerProviderByAdmin(
            @Valid @RequestBody ProviderRegistrationDTO providerDTO) {

        Provider createdProvider = providerService.registerProviderByAdmin(providerDTO);
        return ResponseEntity.ok(ApiResponse.success(
                "Proveedor registrado por administrador exitosamente",
                createdProvider));
    }

    @PostMapping("/invite-provider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> inviteProvider(
            @Valid @RequestBody ProviderInvitationDTO invitationDTO) {

        providerService.inviteProvider(invitationDTO);
        return ResponseEntity.ok(ApiResponse.success(
                "Invitación enviada al proveedor exitosamente"));
    }

    @GetMapping("/invitations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProviderInvitation>>> getProviderInvitations() {
        List<ProviderInvitation> invitations = providerService.getProviderInvitations();
        return ResponseEntity.ok(ApiResponse.success(
                "Invitaciones obtenidas exitosamente",
                invitations));
    }

    @PostMapping("/complete-registration/{token}")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> completeRegistration(
            @PathVariable String token,
            @Valid @RequestBody CompleteRegistrationDTO registrationDTO,
            HttpServletRequest request) {

        AuthResponseDTO authResponse = providerService.completeRegistration(token, registrationDTO, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Registro completado exitosamente",
                authResponse));
    }

    @DeleteMapping("/invitations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cancelInvitation(@PathVariable Long id) {
        providerService.cancelInvitation(id);
        return ResponseEntity.ok(ApiResponse.success("Invitación cancelada exitosamente"));
    }

    @GetMapping("/invitations/{token}")
    public ResponseEntity<ApiResponse<ProviderInvitation>> getInvitationByToken(@PathVariable String token) {
        ProviderInvitation invitation = providerService.getInvitationByToken(token);
        return ResponseEntity.ok(ApiResponse.success("Invitación encontrada", invitation));
    }
}