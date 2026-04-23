package com.shareu.callee.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class HealthController {

    @GetMapping
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "callee"
        );
    }
}
