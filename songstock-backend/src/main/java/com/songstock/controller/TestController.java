package com.songstock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping
    public Map<String, Object> test() {
        logger.info("Test endpoint called");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Controller is working");
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Song Stock API!";
    }
}