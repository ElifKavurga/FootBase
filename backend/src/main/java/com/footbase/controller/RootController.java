package com.footbase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class RootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "FootBase API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("api", "/api");
        response.put("endpoints", Map.of(
                "home", "/api/home",
                "matches", "/api/matches",
                "players", "/api/players",
                "teams", "/api/teams",
                "auth", "/api/auth"));
        return ResponseEntity.ok(response);
    }
}
