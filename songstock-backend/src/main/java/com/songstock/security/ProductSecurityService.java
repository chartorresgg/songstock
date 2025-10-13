package com.songstock.security;

import com.songstock.entity.Product;
import com.songstock.entity.Provider;
import com.songstock.entity.User;
import com.songstock.repository.ProductRepository;
import com.songstock.repository.ProviderRepository;
import com.songstock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("productSecurityService")
public class ProductSecurityService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * Verifica si el usuario autenticado es dueño del producto
     * 
     * @param username  El nombre de usuario autenticado
     * @param productId El ID del producto
     * @return true si el usuario es dueño del producto, false en caso contrario
     */
    public boolean isOwner(String username, Long productId) {
        try {
            // Buscar el producto
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return false;
            }

            // Buscar el usuario
            User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
            if (user == null) {
                return false;
            }

            // Buscar el proveedor del usuario
            Provider provider = providerRepository.findByUserId(user.getId()).orElse(null);
            if (provider == null) {
                return false;
            }

            // Verificar si el proveedor es dueño del producto
            return product.getProvider().getId().equals(provider.getId());

        } catch (Exception e) {
            return false;
        }
    }
}