package com.songstock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para manejar las peticiones OPTIONS (preflight) de CORS.
 * Permite solicitudes desde frontend (React, Angular, etc.) hacia este backend.
 */
@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000", "null" }, // Frontends permitidos
                methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
                                RequestMethod.OPTIONS }, allowedHeaders = "*", allowCredentials = "true")
public class CorsController {

        /**
         * Maneja solicitudes HTTP OPTIONS (preflight) para CORS.
         * Responde con los encabezados necesarios para permitir la comunicación.
         */
        @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
        public ResponseEntity<Void> handleOptions() {
                return ResponseEntity.ok()
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                                .header("Access-Control-Allow-Headers",
                                                "Authorization, Content-Type, X-Requested-With, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers")
                                .header("Access-Control-Max-Age", "3600")
                                .build();
        }
}
