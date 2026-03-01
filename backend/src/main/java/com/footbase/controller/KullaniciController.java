package com.footbase.controller;

import com.footbase.service.KullaniciService;
import com.footbase.security.JwtUtil;
import com.footbase.entity.Kullanici;
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

    @PutMapping("/me")
    public ResponseEntity<?> mevcutKullaniciyiGuncelle(@RequestBody Map<String, String> guncellemeBilgileri,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.getKullaniciEmailFromToken(token);

            Kullanici kullanici = new Kullanici();
            kullanici.setKullaniciAdi(guncellemeBilgileri.get("kullaniciAdi"));
            kullanici.setEmail(guncellemeBilgileri.get("email"));
            kullanici.setAd(guncellemeBilgileri.get("ad"));
            kullanici.setSoyad(guncellemeBilgileri.get("soyad"));
            kullanici.setProfilResmi(guncellemeBilgileri.get("profilResmi"));

            return ResponseEntity.ok(kullaniciService.kullaniciGuncelleByEmail(email, kullanici));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", "Kullanıcı güncellenemedi: " + e.getMessage()));
        }
    }

    @GetMapping("/me/history")
    public ResponseEntity<?> mevcutKullaniciGecmisi(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.getKullaniciEmailFromToken(token);
            return ResponseEntity.ok(kullaniciService.kullaniciGecmisiGetirByEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", "Kullanıcı geçmişi alınamadı: " + e.getMessage()));
        }
    }

}
