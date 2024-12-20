package com.sib.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
 
    @GetMapping("/health")
    public String health() {
        return "health";
    }
}
