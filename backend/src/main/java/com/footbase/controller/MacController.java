package com.footbase.controller;

import com.footbase.entity.Mac;
import com.footbase.patterns.facade.MacIstatistikFacade;
import com.footbase.repository.KullaniciRepository;
import com.footbase.security.JwtUtil;
import com.footbase.service.MacService;
import com.footbase.service.YorumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:3000")
public class MacController {

    @Autowired
    private MacService macService;

    @Autowired
    private YorumService yorumService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private MacIstatistikFacade macIstatistikFacade;

    @GetMapping
    public ResponseEntity<List<Mac>> tumMaclariGetir() {
        return ResponseEntity.ok(macService.tumMaclariGetir());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> macGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macService.macGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/detayli")
    public ResponseEntity<?> macDetaylariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macIstatistikFacade.macDetaylariniGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> macYorumlariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(yorumService.macYorumlariniGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> yorumEkle(@PathVariable Long id, @RequestBody Map<String, String> yorumBilgileri,
            HttpServletRequest request) {
        try {
            String mesaj = yorumBilgileri.get("message");
            if (mesaj == null || mesaj.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Yorum mesajı gereklidir"));
            }

            // Kullanıcı ID'sini JWT token'dan al
            Long kullaniciId = null;
            try {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    String email = jwtUtil.getKullaniciEmailFromToken(token);
                    kullaniciId = kullaniciRepository.findByEmail(email)
                            .map(k -> k.getId())
                            .orElse(null);
                }
            } catch (Exception e) {
                // Token yoksa veya geçersizse devam et
            }

            // Kullanıcı ID'si yoksa hata döndür
            if (kullaniciId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            return ResponseEntity.ok(yorumService.yorumOlustur(id, kullaniciId, mesaj.trim()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<?> macTakimlariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macIstatistikFacade.macTakimlariniGetir(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<?> macOlaylariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macIstatistikFacade.macOlaylariniGetir(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<?> macMedyasiniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macIstatistikFacade.macMedyasiniGetir(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<?> macDurumGecmisiniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macIstatistikFacade.macDurumGecmisiniGetir(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

}
