package com.songstock.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro personalizado para manejar configuraciones de CORS (Cross-Origin
 * Resource Sharing).
 * Permite que el frontend (por ejemplo React, Angular, etc.) pueda comunicarse
 * con el backend
 * sin restricciones de dominio en desarrollo.
 */
@Component
@Order(1) // Se ejecuta antes que otros filtros
public class CorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    /**
     * Método principal que intercepta todas las peticiones HTTP
     * y agrega los encabezados de CORS necesarios.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        logger.info("CORS Filter - Origin: {}, Method: {}, URI: {}",
                origin, request.getMethod(), request.getRequestURI());

        // 🔹 Permitir peticiones desde localhost (útil en desarrollo con React,
        // Angular, etc.)
        if (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (origin == null || "null".equals(origin)) {
            // 🔹 Si la petición proviene de un archivo local (ej. file://)
            response.setHeader("Access-Control-Allow-Origin", "*");
        } else {
            // 🔹 Para otros orígenes (en desarrollo, se permite cualquiera)
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // 🔹 Métodos permitidos
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");

        // 🔹 Encabezados permitidos
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Content-Type, X-Requested-With, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");

        // 🔹 Encabezados expuestos
        response.setHeader("Access-Control-Expose-Headers",
                "Access-Control-Allow-Origin, Access-Control-Allow-Credentials");

        // 🔹 Permitir credenciales (cookies, headers de autenticación)
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 🔹 Tiempo máximo que se guardará en caché la respuesta CORS
        response.setHeader("Access-Control-Max-Age", "3600");

        // 🔹 Manejo de preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("CORS Filter - Handling OPTIONS request");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        logger.info("CORS Filter - Continuing chain for {} request", request.getMethod());
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("CORS Filter initialized");
    }

    @Override
    public void destroy() {
        logger.info("CORS Filter destroyed");
    }
}
