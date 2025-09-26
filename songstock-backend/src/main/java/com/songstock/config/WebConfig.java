package com.songstock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para el proyecto usando WebMvcConfigurer.
 * Esto complementa o reemplaza el filtro CORS según la necesidad.
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    /**
     * Configura los orígenes, métodos y cabeceras permitidas para CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS mappings");

        registry.addMapping("/**") // Aplica a todas las rutas
                .allowedOriginPatterns("*") // Permite cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD") // Métodos permitidos
                .allowedHeaders("*") // Cualquier cabecera
                .allowCredentials(true) // Permitir credenciales
                .maxAge(3600); // Tiempo máximo de cacheo de preflight
    }
}
