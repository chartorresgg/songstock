package com.songstock.config;

import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import com.songstock.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Iniciando DataInitializer...");
        initializeAdminUser();
    }

    

    private void initializeAdminUser() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@songstock.com");

                String rawPassword = "admin123";
                String encodedPassword = passwordEncoder.encode(rawPassword);
                logger.info("🔐 Password hash generado: {}", encodedPassword.substring(0, 20) + "...");

                admin.setPassword(encodedPassword);
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setRole(UserRole.ADMIN);
                admin.setIsActive(true);

                userRepository.save(admin);
                logger.info("✅ Usuario administrador creado exitosamente: admin/admin123");
            } else {
                logger.info("ℹ️ Usuario administrador ya existe");

                // Verificar si el password es correcto
                User existingAdmin = userRepository.findByUsername("admin").orElse(null);
                if (existingAdmin != null) {
                    logger.info("🔍 Password actual inicia con: {}",
                            existingAdmin.getPassword().substring(0, 10) + "...");

                    // Si no es un hash BCrypt válido, actualizarlo
                    if (!existingAdmin.getPassword().startsWith("$2a$") &&
                            !existingAdmin.getPassword().startsWith("$2b$") &&
                            !existingAdmin.getPassword().startsWith("$2y$")) {

                        logger.warn("⚠️ Password hash inválido detectado, actualizando...");
                        String newHash = passwordEncoder.encode("admin123");
                        existingAdmin.setPassword(newHash);
                        userRepository.save(existingAdmin);
                        logger.info("✅ Password de admin actualizado correctamente");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error inicializando usuario admin", e);
        }
    }
}