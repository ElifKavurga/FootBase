package com.footbase.controller;

import com.footbase.entity.Mac;
import com.footbase.entity.Takim;
import com.footbase.entity.Oyuncu;
import com.footbase.patterns.command.dto.MacSonlandirDTO;
import com.footbase.patterns.command.dto.SkorGirisiDTO;
import com.footbase.patterns.command.service.MacCommandService;
import com.footbase.security.JwtUtil;
import com.footbase.service.MacService;
import com.footbase.service.TakimService;
import com.footbase.service.OyuncuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Admin controller'ı
 * Admin işlemleri endpoint'lerini içerir
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private MacService macService;

    @Autowired
    private TakimService takimService;

    @Autowired
    private OyuncuService oyuncuService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MacCommandService macCommandService;

    private Long getKullaniciIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getKullaniciIdFromToken(token);
        }
        return null;
    }

    // ========== MAÇ İŞLEMLERİ ==========

    // Yeni maç oluşturur
    @PostMapping("/matches")
    public ResponseEntity<?> macOlustur(@RequestBody Mac mac) {
        try {
            return ResponseEntity.ok(macService.macOlustur(mac));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // Maç bilgilerini günceller
    @PutMapping("/matches/{id}")
    public ResponseEntity<?> macGuncelle(@PathVariable Long id, @RequestBody Mac mac) {
        try {
            return ResponseEntity.ok(macService.macGuncelle(id, mac));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // ========== TAKIM İŞLEMLERİ ==========
    // Yeni takım oluşturur
    @PostMapping("/teams")
    public ResponseEntity<?> takimOlustur(@RequestBody Takim takim) {
        try {
            return ResponseEntity.ok(takimService.takimOlustur(takim));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // Takım bilgilerini günceller
    @PutMapping("/teams/{id}")
    public ResponseEntity<?> takimGuncelle(@PathVariable Long id, @RequestBody Takim takim) {
        try {
            return ResponseEntity.ok(takimService.takimGuncelle(id, takim));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // ========== OYUNCU İŞLEMLERİ ==========

    // Yeni oyuncu oluşturur
    @PostMapping("/players")
    public ResponseEntity<?> oyuncuOlustur(@RequestBody Oyuncu oyuncu) {
        try {
            return ResponseEntity.ok(oyuncuService.oyuncuOlustur(oyuncu));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // Oyuncu bilgilerini günceller
    @PutMapping("/players/{id}")
    public ResponseEntity<?> oyuncuGuncelle(@PathVariable Long id, @RequestBody Oyuncu oyuncu) {
        try {
            return ResponseEntity.ok(oyuncuService.oyuncuGuncelle(id, oyuncu));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    // ========== MAÇ ONAY İŞLEMLERİ ==========

    // Onay bekleyen maçları getirir (admin'in editörlerinin eklediği)
    @GetMapping("/matches/pending")
    public ResponseEntity<?> onayBekleyenMaclariGetir(HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Admin " + adminId + " için onay bekleyen maçlar isteniyor");
            List<Mac> onayBekleyenMaclar = macService.adminOnayBekleyenMaclariGetir(adminId);
            System.out.println("Dönen maç sayısı: " + (onayBekleyenMaclar != null ? onayBekleyenMaclar.size() : 0));
            return ResponseEntity
                    .ok(onayBekleyenMaclar != null ? onayBekleyenMaclar : java.util.Collections.emptyList());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "Maçlar getirilirken bir hata oluştu: " + e.getMessage()));
        }
    }

    // Maçı onaylar
    @PostMapping("/matches/{macId}/approve")
    public ResponseEntity<?> macOnayla(@PathVariable Long macId, HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            Mac onaylananMac = macService.macOnayla(macId, adminId);
            return ResponseEntity.ok(onaylananMac);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "Maç onaylanırken bir hata oluştu: " + e.getMessage()));
        }
    }

    // Maçı reddeder
    @PostMapping("/matches/{macId}/reject")
    public ResponseEntity<?> macReddet(@PathVariable Long macId, HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            Mac reddedilenMac = macService.macReddet(macId, adminId);
            return ResponseEntity.ok(reddedilenMac);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "Maç reddedilirken bir hata oluştu: " + e.getMessage()));
        }
    }

    // ========== MAÇ SKOR VE SONLANDIRMA İŞLEMLERİ (COMMAND PATTERN) ==========

    // Onaylanan maça skor girişi yapar
    @PostMapping("/matches/score")
    public ResponseEntity<?> macSkorGirisi(@RequestBody SkorGirisiDTO skorGirisiDTO, HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Admin " + adminId + " skor girişi yapıyor: " + skorGirisiDTO);
            Map<String, Object> sonuc = macCommandService.skorGirisiYap(skorGirisiDTO, adminId);

            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "Skor girişi yapılırken bir hata oluştu: " + e.getMessage()));
        }
    }

    // Maçı sonlandırır (skorları girer ve durumu günceller)
    @PostMapping("/matches/finish")
    public ResponseEntity<?> macSonlandir(@RequestBody MacSonlandirDTO macSonlandirDTO, HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Admin " + adminId + " maç sonlandırıyor: " + macSonlandirDTO);
            Map<String, Object> sonuc = macCommandService.macSonlandir(macSonlandirDTO, adminId);

            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "Maç sonlandırılırken bir hata oluştu: " + e.getMessage()));
        }
    }

    // Son işlemi geri alır (undo)
    @PostMapping("/matches/undo")
    public ResponseEntity<?> sonIslemGeriAl(HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Admin " + adminId + " son işlemi geri alıyor...");
            Map<String, Object> sonuc = macCommandService.sonIslemGeriAl(adminId);

            if ((Boolean) sonuc.get("basarili")) {
                return ResponseEntity.ok(sonuc);
            } else {
                return ResponseEntity.badRequest().body(sonuc);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "İşlem geri alınırken bir hata oluştu: " + e.getMessage()));
        }
    }

    // İşlem geçmişini getirir
    @GetMapping("/matches/history")
    public ResponseEntity<?> islemGecmisiGetir(HttpServletRequest request) {
        try {
            Long adminId = getKullaniciIdFromToken(request);
            if (adminId == null) {
                return ResponseEntity.status(401).body(Map.of("hata", "Giriş yapmanız gerekiyor"));
            }

            System.out.println("Admin " + adminId + " işlem geçmişini getiriyor...");
            Map<String, Object> sonuc = macCommandService.islemGecmisiGetir();
            return ResponseEntity.ok(sonuc);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("hata", "İşlem geçmişi getirilirken bir hata oluştu: " + e.getMessage()));
        }
    }
}