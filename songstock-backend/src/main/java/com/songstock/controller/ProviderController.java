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

import java.util.List;

@RestController
@RequestMapping("/providers")
@CrossOrigin(origins = "*", maxAge = 3600)
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
}