package com.footbase.controller;

import com.footbase.entity.Bildirim;
import com.footbase.security.JwtUtil;
import com.footbase.service.BildirimServisi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class BildirimController {

    @Autowired
    private BildirimServisi bildirimServisi;

    @Autowired
    private JwtUtil jwtUtil;

    // JWT token'dan kullanıcı ID'sini alır
    private Long getKullaniciIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getKullaniciIdFromToken(token);
        }
        return null;
    }

    // Kullanıcının tüm bildirimlerini getirir
    @GetMapping
    public ResponseEntity<?> tumBildirimleriGetir(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.kullaniciBildirimleriGetir(kullaniciId);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Okunmamış bildirimleri getirir
    @GetMapping("/unread")
    public ResponseEntity<?> okunmamisBildirimleriGetir(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.okunmamisBildirimleriGetir(kullaniciId);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Okunmamış bildirim sayısını döndürür
    @GetMapping("/unread/count")
    public ResponseEntity<?> okunmamisSayisi(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            Long sayi = bildirimServisi.okunmamisBildirimSayisi(kullaniciId);
            return ResponseEntity.ok(Map.of("count", sayi));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Son N bildirimi getirir
    @GetMapping("/recent")
    public ResponseEntity<?> sonBildirimler(HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            List<Bildirim> bildirimler = bildirimServisi.sonBildirimleriGetir(kullaniciId, limit);
            return ResponseEntity.ok(bildirimler);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Bildirimi okundu olarak işaretler
    @PutMapping("/{id}/read")
    public ResponseEntity<?> bildirimOkundu(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            bildirimServisi.bildirimOkunduIsaretle(id);
            return ResponseEntity.ok(Map.of("mesaj", "Bildirim okundu olarak işaretlendi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Tüm bildirimleri okundu işaretle
    @PutMapping("/read-all")
    public ResponseEntity<?> tumunuOkundu(HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            int guncellenenSayi = bildirimServisi.tumBildirimleriOkunduIsaretle(kullaniciId);
            return ResponseEntity.ok(Map.of("mesaj", guncellenenSayi + " bildirim okundu olarak işaretlendi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }

    // Bildirimi siler
    @DeleteMapping("/{id}")
    public ResponseEntity<?> bildirimSil(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long kullaniciId = getKullaniciIdFromToken(request);
            if (kullaniciId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }
            bildirimServisi.bildirimSil(id);
            return ResponseEntity.ok(Map.of("mesaj", "Bildirim silindi"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("hata", e.getMessage()));
        }
    }
}
