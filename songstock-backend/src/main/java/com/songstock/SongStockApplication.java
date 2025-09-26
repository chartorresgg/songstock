package com.songstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase principal de arranque de la aplicación SongStock.
 * 
 * - @SpringBootApplication: Marca la aplicación como una aplicación Spring
 * Boot,
 * habilitando configuración automática, escaneo de componentes y beans.
 * - @EnableJpaRepositories: Habilita la detección de repositorios JPA en el
 * paquete indicado.
 * - @EnableTransactionManagement: Permite el manejo de transacciones en la capa
 * de persistencia.
 */
@SpringBootApplication(scanBasePackages = "com.songstock")
@EnableJpaRepositories(basePackages = "com.songstock.repository")
@EnableTransactionManagement
public class SongStockApplication {

    /**
     * Método main que inicia la aplicación Spring Boot.
     * Lanza el contexto de Spring, configura todos los beans
     * y arranca el servidor embebido (Tomcat por defecto).
     *
     * @param args argumentos de línea de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(SongStockApplication.class, args);

        // Mensajes en consola para confirmar el arranque correcto
        System.out.println("🎵 Song Stock API iniciada exitosamente!");
        System.out.println("📚 Documentación disponible en: http://localhost:8080/api/v1");
        System.out.println("🔐 Endpoints de autenticación: http://localhost:8080/api/v1/auth");
    }
}
