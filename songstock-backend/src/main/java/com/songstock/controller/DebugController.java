package com.songstock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DebugController {
    
    @Autowired
    @Qualifier("requestMappingHandlerMapping")  // ← Especificar cuál bean usar
    private RequestMappingHandlerMapping handlerMapping;
    
    @GetMapping("/debug/mappings")
    public Map<String, String> getMappings() {
        return handlerMapping.getHandlerMethods()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                entry -> entry.getValue().toString()
            ));
    }
}