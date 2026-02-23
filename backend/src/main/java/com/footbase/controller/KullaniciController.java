package com.footbase.controller;

import com.footbase.service.KullaniciService;
import com.footbase.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class KullaniciController {

    @Autowired
    private KullaniciService kullaniciService;

    @Autowired
    private JwtUtil jwtUtil;

    // Mevcut kullanıcının bilgilerini getirir
    @GetMapping("/me")
    public ResponseEntity<?> mevcutKullaniciyiGetir(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getKullaniciEmailFromToken(token);
            return ResponseEntity.ok(kullaniciService.kullaniciGetirByEmail(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", "Kullanıcı bilgileri alınamadı: " + e.getMessage()));
        }
    }

    // ID'ye göre kullanıcı getirir
    @GetMapping("/{id}")
    public ResponseEntity<?> kullaniciGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(kullaniciService.kullaniciGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

}
