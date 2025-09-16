package com.songstock.service;

import com.songstock.dto.ProviderRegistrationDTO;
import com.songstock.entity.Provider;
import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import com.songstock.entity.VerificationStatus;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final UserService userService;

    @Autowired
    public ProviderService(ProviderRepository providerRepository, UserService userService) {
        this.providerRepository = providerRepository;
        this.userService = userService;
    }

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

    public Provider registerProvider(ProviderRegistrationDTO providerDTO) {
        // Crear usuario primero
        User user = userService.createUser(new com.songstock.dto.UserRegistrationDTO(
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
        provider.setVerificationStatus(VerificationStatus.PENDING);

        return providerRepository.save(provider);
    }

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

    public Provider verifyProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setVerificationStatus(VerificationStatus.VERIFIED);
        provider.setVerificationDate(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    public Provider rejectProvider(Long id) {
        Provider provider = getProviderById(id);
        provider.setVerificationStatus(VerificationStatus.REJECTED);
        provider.setVerificationDate(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }
        providerRepository.deleteById(id);
    }
}