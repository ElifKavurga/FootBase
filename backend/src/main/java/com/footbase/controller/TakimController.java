package com.footbase.controller;

import com.footbase.entity.Takim;
import com.footbase.repository.MacTakimlariRepository;
import com.footbase.repository.OyuncuRepository;
import com.footbase.service.TakimService;
import com.footbase.service.MacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "http://localhost:3000")
public class TakimController {

    @Autowired
    private TakimService takimService;

    @Autowired
    private MacService macService;

    @Autowired
    private OyuncuRepository oyuncuRepository;

    @Autowired
    private MacTakimlariRepository macTakimlariRepository;

    @GetMapping
    public ResponseEntity<List<Takim>> tumTakimlariGetir() {
        return ResponseEntity.ok(takimService.tumTakimlariGetir());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> takimGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(takimService.takimGetir(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<?> takimOyunculariniGetir(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(oyuncuRepository.findByTakimId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<?> takimMaclariniGetir(@PathVariable Long id) {
        try {
            System.out.println("========== TAKIM MAÇLARI GETİRİLİYOR ==========");
            System.out.println("Takım ID: " + id);

            // Takımın oynadığı tüm maçları getir (mac_takimlari tablosundan)
            var macTakimlari = macTakimlariRepository.findByTakimId(id);
            System.out.println("Bulunan mac_takimlari kayıt sayısı: " + macTakimlari.size());

            var maclar = macTakimlari.stream()
                    .map(mt -> {
                        var mac = mt.getMac();
                        // Her maç için takım bilgilerini ve durumunu doldur
                        if (mac != null) {
                            try {
                                // MacService'deki populateMacData metodunu kullan
                                macService.populateMacDataPublic(mac);

                                // Maçın durumunu da set et
                                String durum = macService.getLatestDurum(mac.getId());
                                mac.setOnayDurumu(durum != null ? durum : "YAYINDA");

                                System.out.println("Maç " + mac.getId() + ": " +
                                        (mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "null") +
                                        " vs " +
                                        (mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "null"));
                            } catch (Exception e) {
                                System.err.println(
                                        "Maç " + mac.getId() + " için veri doldurulurken hata: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        return mac;
                    })
                    .filter(mac -> mac != null)
                    .distinct()
                    .toList();

            System.out.println("Döndürülen maç sayısı: " + maclar.size());
            System.out.println("========== TAKIM MAÇLARI GETİRME TAMAMLANDI ==========");

            return ResponseEntity.ok(maclar);
        } catch (Exception e) {
            System.err.println("Takım maçları getirilirken hata: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> takimIstatistikleriniGetir(@PathVariable Long id) {
        try {
            // Veritabanı fonksiyonunu kullanarak toplam maç sayısını al
            int toplamMac = macTakimlariRepository.findByTakimId(id).size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("toplam_mac", toplamMac);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("hata", e.getMessage()));
        }
    }
}