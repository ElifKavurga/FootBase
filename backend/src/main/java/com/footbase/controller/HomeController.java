package com.footbase.controller;

import com.footbase.entity.Yorum;
import com.footbase.service.MacService;
import com.footbase.service.OyuncuService;
import com.footbase.service.TakimService;
import com.footbase.service.YorumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {

    @Autowired
    private MacService macService;

    @Autowired
    private OyuncuService oyuncuService;

    @Autowired
    private TakimService takimService;

    @Autowired
    private YorumService yorumService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> anaSayfaBilgileriniGetir() {
        try {
            Map<String, Object> veriler = new HashMap<>();

            // Maçları frontend'in beklediği formata dönüştür
            List<Map<String, Object>> gelecekMaclar = java.util.Collections.emptyList();
            List<Map<String, Object>> gecmisMaclar = java.util.Collections.emptyList();

            try {
                gelecekMaclar = macService.gelecekMaclariGetir().stream()
                        .filter(mac -> mac != null)
                        .map(this::macToMap)
                        .filter(macMap -> macMap != null)
                        .collect(java.util.stream.Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                // Hata durumunda boş liste kullan
            }

            try {
                gecmisMaclar = macService.gecmisMaclariGetir().stream()
                        .filter(mac -> mac != null)
                        .map(this::macToMap)
                        .filter(macMap -> macMap != null)
                        .collect(java.util.stream.Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                // Hata durumunda boş liste kullan
            }

            // Frontend'in beklediği alan adları
            veriler.put("upcomingMatches", gelecekMaclar);
            veriler.put("matches", gelecekMaclar); // Alternatif alan adı
            veriler.put("pastMatches", gecmisMaclar);
            veriler.put("gelecekMaclar", gelecekMaclar); // Türkçe alan adı (geriye dönük uyumluluk)
            veriler.put("gecmisMaclar", gecmisMaclar); // Türkçe alan adı (geriye dönük uyumluluk)

            // Gerçek sayıları hesapla
            long playerCount = 0;
            long teamCount = 0;
            try {
                playerCount = oyuncuService.tumOyunculariGetir().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                teamCount = takimService.tumTakimlariGetir().size();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Son yorumları getir (en fazla 10 tane)
            List<Map<String, Object>> yorumListesi = java.util.Collections.emptyList();
            try {
                List<Yorum> sonYorumlar = yorumService.sonYorumlariGetir(10);
                yorumListesi = sonYorumlar.stream()
                        .filter(yorum -> yorum != null)
                        .map(yorum -> {
                            try {
                                Map<String, Object> yorumMap = new HashMap<>();
                                yorumMap.put("id", yorum.getId());
                                yorumMap.put("message", yorum.getMesaj());
                                yorumMap.put("mesaj", yorum.getMesaj());
                                yorumMap.put("author",
                                        yorum.getKullanici() != null ? yorum.getKullanici().getKullaniciAdi() : null);
                                yorumMap.put("createdAt", yorum.getYorumTarihi());
                                yorumMap.put("yorumTarihi", yorum.getYorumTarihi());
                                if (yorum.getMac() != null) {
                                    yorumMap.put("macId", yorum.getMac().getId());
                                    String macBilgisi = "";
                                    try {
                                        if (yorum.getMac().getEvSahibiTakim() != null
                                                && yorum.getMac().getDeplasmanTakim() != null) {
                                            macBilgisi = yorum.getMac().getEvSahibiTakim().getAd() + " vs "
                                                    + yorum.getMac().getDeplasmanTakim().getAd();
                                        }
                                    } catch (Exception e) {
                                        // Takım bilgileri alınamazsa boş string kullan
                                    }
                                    yorumMap.put("macBilgisi", macBilgisi);
                                }
                                return yorumMap;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(yorumMap -> yorumMap != null)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                // Hata durumunda boş liste kullan
            }

            // Diğer alanlar (frontend'in beklediği)
            veriler.put("comments", yorumListesi);
            veriler.put("playerCount", playerCount);
            veriler.put("teamCount", teamCount);
            veriler.put("topRatedPlayer", null); // Oyuncu puanlaması veritabanında yok, şimdilik null

            return ResponseEntity.ok(veriler);
        } catch (Exception e) {
            e.printStackTrace();
            // Genel hata durumunda boş veri döndür
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("upcomingMatches", java.util.Collections.emptyList());
            errorResponse.put("matches", java.util.Collections.emptyList());
            errorResponse.put("pastMatches", java.util.Collections.emptyList());
            errorResponse.put("gelecekMaclar", java.util.Collections.emptyList());
            errorResponse.put("gecmisMaclar", java.util.Collections.emptyList());
            errorResponse.put("comments", java.util.Collections.emptyList());
            errorResponse.put("playerCount", 0);
            errorResponse.put("teamCount", 0);
            errorResponse.put("topRatedPlayer", null);
            return ResponseEntity.ok(errorResponse);
        }
    }

    private Map<String, Object> macToMap(com.footbase.entity.Mac mac) {
        if (mac == null) {
            return null;
        }

        try {
            Map<String, Object> macMap = new HashMap<>();
            macMap.put("id", mac.getId());

            // Ev sahibi takım bilgileri
            String homeTeamName = null;
            String homeTeamLogo = null;
            String stadium = null;
            try {
                if (mac.getEvSahibiTakim() != null) {
                    homeTeamName = mac.getEvSahibiTakim().getAd();
                    homeTeamLogo = mac.getEvSahibiTakim().getLogo();
                    // Stadyum bilgisini önce Mac'ten, yoksa ev sahibi takımdan al
                    if (mac.getStadyum() != null) {
                        stadium = mac.getStadyum().getStadyumAdi();
                    } else if (mac.getEvSahibiTakim().getStadyum() != null) {
                        stadium = mac.getEvSahibiTakim().getStadyum().getStadyumAdi();
                    }
                }
            } catch (Exception e) {
                // Takım bilgileri alınamazsa null kullan
            }
            macMap.put("homeTeam", homeTeamName);
            macMap.put("homeTeamLogo", homeTeamLogo);
            macMap.put("stadium", stadium);

            // Deplasman takım bilgileri
            String awayTeamName = null;
            String awayTeamLogo = null;
            try {
                if (mac.getDeplasmanTakim() != null) {
                    awayTeamName = mac.getDeplasmanTakim().getAd();
                    awayTeamLogo = mac.getDeplasmanTakim().getLogo();
                }
            } catch (Exception e) {
                // Takım bilgileri alınamazsa null kullan
            }
            macMap.put("awayTeam", awayTeamName);
            macMap.put("awayTeamLogo", awayTeamLogo);

            macMap.put("homeScore", mac.getEvSahibiSkor());
            macMap.put("awayScore", mac.getDeplasmanSkor());
            macMap.put("date", mac.getTarih() != null ? mac.getTarih().toString() : null);
            macMap.put("time", mac.getSaat() != null ? mac.getSaat().toString() : null);
            macMap.put("status", mac.getDurum() != null ? mac.getDurum() : "PLANLI");

            // Frontend'in beklediği kickoffAt (tarih + saat birleşik)
            if (mac.getTarih() != null && mac.getSaat() != null) {
                try {
                    java.time.LocalDateTime kickoffAt = java.time.LocalDateTime.of(mac.getTarih(), mac.getSaat());
                    macMap.put("kickoffAt", kickoffAt.toString());
                } catch (Exception e) {
                    macMap.put("kickoffAt", null);
                }
            } else {
                macMap.put("kickoffAt", null);
            }

            return macMap;
        } catch (Exception e) {
            e.printStackTrace();
            // Hata durumunda minimum bilgi ile map döndür
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("id", mac.getId());
            errorMap.put("homeTeam", null);
            errorMap.put("awayTeam", null);
            errorMap.put("status", "ERROR");
            return errorMap;
        }
    }
}
