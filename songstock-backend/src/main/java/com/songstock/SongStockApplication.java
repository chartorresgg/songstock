package com.songstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.songstock")
@EnableJpaRepositories(basePackages = "com.songstock.repository")
@EnableTransactionManagement
public class SongStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongStockApplication.class, args);
        System.out.println("🎵 Song Stock API iniciada exitosamente!");
        System.out.println("📚 Documentación disponible en: http://localhost:8080/api/v1");
        System.out.println("🔐 Endpoints de autenticación: http://localhost:8080/api/v1/auth");
    }

}
