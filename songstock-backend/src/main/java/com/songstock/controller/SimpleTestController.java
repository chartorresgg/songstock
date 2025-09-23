package com.songstock.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class SimpleTestController {
    
    @GetMapping
    public String test() {
        return "GET funcionando";
    }
    
    @PostMapping
    public String testPost(@RequestBody String body) {
        return "POST funcionando: " + body;
    }
}