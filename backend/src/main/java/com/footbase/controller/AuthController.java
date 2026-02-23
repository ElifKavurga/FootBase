package com.footbase.controller;

import com.footbase.entity.Kullanici;
import com.footbase.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//Kimlik doğrulama controller'ı
//Kullanıcı girişi, kayıt ve şifre sıfırlama endpoint'lerini içerir

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Kullanıcı girişi endpoint'i
    @PostMapping("/login")
    public ResponseEntity<?> girisYap(@RequestBody Map<String, String> girisBilgileri) {
        try {
            String email = girisBilgileri.get("email");
            String sifre = girisBilgileri.get("password");

            Map<String, Object> sonuc = authService.girisYap(email, sifre);
            return ResponseEntity.ok(sonuc);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // Kullanıcı kaydı endpoint'i
    @PostMapping("/register")
    public ResponseEntity<?> kayitOl(@RequestBody Map<String, String> kayitBilgileri) {
        try {
            Kullanici kullanici = new Kullanici();
            kullanici.setKullaniciAdi(kayitBilgileri.get("kullaniciAdi"));
            kullanici.setEmail(kayitBilgileri.get("email"));
            // Şifre alanını kontrol et - hem "sifre" hem "password" kabul et
            String sifre = kayitBilgileri.get("sifre");
            if (sifre == null) {
                sifre = kayitBilgileri.get("password");
            }
            kullanici.setSifre(sifre);

            Map<String, Object> sonuc = authService.kayitOl(kullanici);
            return ResponseEntity.ok(sonuc);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // Şifre sıfırlama endpoint'i
    @PostMapping("/reset-password")
    public ResponseEntity<?> sifreSifirla(@RequestBody Map<String, String> sifreSifirlamaBilgileri) {
        try {
            String email = sifreSifirlamaBilgileri.get("email");
            String yeniSifre = sifreSifirlamaBilgileri.get("newPassword");

            authService.sifreSifirla(email, yeniSifre);
            return ResponseEntity.ok(Map.of("mesaj", "Şifre başarıyla sıfırlandı"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }
}
