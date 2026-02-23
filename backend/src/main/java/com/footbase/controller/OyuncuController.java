package com.footbase.controller;

import com.footbase.entity.Oyuncu;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.MacOyuncuOlaylariRepository;
import com.footbase.repository.OyuncuMedyaRepository;
import com.footbase.repository.OyuncuPuanlariRepository;
import com.footbase.repository.OyuncuYorumlariRepository;
import com.footbase.security.JwtUtil;
import com.footbase.service.OyuncuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "http://localhost:3000")
public class OyuncuController {

    @Autowired
    private OyuncuService oyuncuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private OyuncuMedyaRepository oyuncuMedyaRepository;

    @Autowired
    private MacOyuncuOlaylariRepository macOyuncuOlaylariRepository;

    @Autowired
    private OyuncuPuanlariRepository oyuncuPuanlariRepository;

    @Autowired
    private OyuncuYorumlariRepository oyuncuYorumlariRepository;

    @GetMapping
    public ResponseEntity<List<Oyuncu>> tumOyunculariGetir() {
        return ResponseEntity.ok(oyuncuService.tumOyunculariGetir());
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<?> oyuncuMedyasiniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(oyuncuMedyaRepository.findByOyuncuIdWithDetails(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> oyuncuIstatistikleriniGetir(@PathVariable Long id) {
        try {
            int toplamGol = 0;
            int toplamSariKart = 0;
            int toplamKirmiziKart = 0;

            for (var event : macOyuncuOlaylariRepository.findByOyuncuId(id)) {
                String olayTuru = event.getOlayTuru();
                if ("GOL".equals(olayTuru)) {
                    toplamGol++;
                } else if ("SARI_KART".equals(olayTuru)) {
                    toplamSariKart++;
                } else if ("KIRMIZI_KART".equals(olayTuru)) {
                    toplamKirmiziKart++;
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("toplam_gol", toplamGol);
            stats.put("toplam_sari_kart", toplamSariKart);
            stats.put("toplam_kirmizi_kart", toplamKirmiziKart);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> oyuncuYorumlariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(oyuncuYorumlariRepository.findByOyuncuIdOrderByOlusturmaTarihiDesc(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<?> oyuncuPuaniniGetir(@PathVariable Long id) {
        try {
            return oyuncuPuanlariRepository.findByOyuncuIdWithDetails(id)
                    .map(puan -> ResponseEntity.ok((Object) puan))
                    .orElse(ResponseEntity.ok((Object) Map.of("puan", 0.0)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> oyuncuPuanlamalariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(oyuncuService.oyuncuYorumlariniGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> oyuncuGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(oyuncuService.oyuncuDetaylariniGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @PostMapping("/{id}/ratings")
    public ResponseEntity<?> oyuncuPuanla(@PathVariable Long id, @RequestBody Map<String, Object> puanlamaBilgileri,
            HttpServletRequest request) {
        try {
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

            Integer score = null;
            if (puanlamaBilgileri.containsKey("score")) {
                Object scoreObj = puanlamaBilgileri.get("score");
                if (scoreObj instanceof Number) {
                    score = ((Number) scoreObj).intValue();
                }
            }

            String comment = (String) puanlamaBilgileri.getOrDefault("comment", "");

            com.footbase.entity.OyuncuYorumlari yorum = oyuncuService.oyuncuYorumEkle(id, kullaniciId, score, comment);

            // Map formatında döndür
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", yorum.getId());
            result.put("comment", yorum.getIcerik());
            result.put("icerik", yorum.getIcerik());
            result.put("olusturmaTarihi", yorum.getOlusturmaTarihi());

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }
}