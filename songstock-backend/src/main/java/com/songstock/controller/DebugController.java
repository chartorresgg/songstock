package com.songstock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para depuración.
 * Expone un endpoint que lista todos los mapeos registrados en el sistema.
 * Útil para debugging y ver qué endpoints están disponibles.
 */
@RestController
public class DebugController {

    // Se inyecta el bean específico que gestiona los mappings de requests en Spring
    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * Devuelve todos los endpoints registrados en la aplicación.
     * 
     * @return Map con clave = RequestMapping (ruta + método),
     *         valor = HandlerMethod (método Java que atiende la petición).
     */
    @GetMapping("/debug/mappings")
    public Map<String, String> getMappings() {
        return handlerMapping.getHandlerMethods()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().toString()));
    }
}
