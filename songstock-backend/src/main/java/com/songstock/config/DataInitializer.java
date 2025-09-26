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

/**
 * Inicializador de datos que crea automáticamente un usuario administrador
 * al arrancar la aplicación, en caso de que no exista.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método que se ejecuta al inicio de la aplicación.
     * Llama a la inicialización del usuario admin.
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Iniciando DataInitializer...");
        initializeAdminUser();
    }

    /**
     * Inicializa el usuario administrador por defecto.
     * - Si no existe, lo crea con credenciales admin/admin123
     * - Si existe, verifica que la contraseña esté correctamente encriptada
     */
    private void initializeAdminUser() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                // 🔹 Crear usuario admin por defecto
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
                // 🔹 Usuario admin ya existe
                logger.info("ℹ️ Usuario administrador ya existe");

                // Verificar estado de la contraseña
                User existingAdmin = userRepository.findByUsername("admin").orElse(null);
                if (existingAdmin != null) {
                    logger.info("🔍 Password actual inicia con: {}",
                            existingAdmin.getPassword().substring(0, 10) + "...");

                    // Si la contraseña no es un hash BCrypt válido, regenerarla
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
