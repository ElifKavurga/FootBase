package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.OyuncuYorumlari;
import com.footbase.entity.Yorum;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.OyuncuYorumlariRepository;
import com.footbase.repository.YorumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KullaniciService {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private YorumRepository yorumRepository;

    @Autowired
    private OyuncuYorumlariRepository oyuncuYorumlariRepository;

    public Map<String, Object> kullaniciGetir(Long id) {
        Kullanici kullanici = kullaniciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return kullaniciBilgileriniMaple(kullanici);
    }

    public Map<String, Object> kullaniciGetirByEmail(String email) {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return kullaniciBilgileriniMaple(kullanici);
    }

    public Map<String, Object> kullaniciGuncelle(Long id, Kullanici kullanici) {
        Kullanici mevcutKullanici = kullaniciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (kullanici.getAd() != null) {
            mevcutKullanici.setAd(kullanici.getAd());
        }
        if (kullanici.getSoyad() != null) {
            mevcutKullanici.setSoyad(kullanici.getSoyad());
        }
        if (kullanici.getKullaniciAdi() != null) {
            // Kullanıcı adı değişiyorsa kontrol et
            if (!mevcutKullanici.getKullaniciAdi().equals(kullanici.getKullaniciAdi())
                    && kullaniciRepository.existsByKullaniciAdi(kullanici.getKullaniciAdi())) {
                throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
            }
            mevcutKullanici.setKullaniciAdi(kullanici.getKullaniciAdi());
        }
        if (kullanici.getProfilResmi() != null) {
            mevcutKullanici.setProfilResmi(kullanici.getProfilResmi());
        }

        Kullanici guncellenmisKullanici = kullaniciRepository.save(mevcutKullanici);
        return kullaniciBilgileriniMaple(guncellenmisKullanici);
    }

    private Map<String, Object> kullaniciBilgileriniMaple(Kullanici kullanici) {
        Map<String, Object> kullaniciMap = new HashMap<>();
        kullaniciMap.put("id", kullanici.getId());
        kullaniciMap.put("email", kullanici.getEmail());
        kullaniciMap.put("kullaniciAdi", kullanici.getKullaniciAdi());
        kullaniciMap.put("displayName", kullanici.getKullaniciAdi()); // Frontend için
        kullaniciMap.put("rol", kullanici.getRol());
        kullaniciMap.put("admin",
                kullanici.getAdmin() != null ? kullanici.getAdmin() : "ADMIN".equals(kullanici.getRol()));
        kullaniciMap.put("ad", kullanici.getAd() != null ? kullanici.getAd() : "");
        kullaniciMap.put("soyad", kullanici.getSoyad() != null ? kullanici.getSoyad() : "");
        kullaniciMap.put("profilResmi", kullanici.getProfilResmi() != null ? kullanici.getProfilResmi() : "");
        kullaniciMap.put("followersCount", 0); // Veritabanında takipçi tablosu yok
        kullaniciMap.put("followingCount", 0); // Veritabanında takip tablosu yok

        // Kullanıcının yorumlarını getir (maç yorumları)
        List<Yorum> macYorumlari = yorumRepository.findByKullaniciOrderByYorumTarihiDesc(kullanici);
        List<Map<String, Object>> recentComments = macYorumlari.stream()
                .limit(10) // Son 10 yorum
                .map(yorum -> {
                    Map<String, Object> yorumMap = new HashMap<>();
                    yorumMap.put("commentId", yorum.getId());
                    yorumMap.put("message", yorum.getMesaj());
                    yorumMap.put("createdAt", yorum.getYorumTarihi());
                    if (yorum.getMac() != null) {
                        yorumMap.put("matchId", yorum.getMac().getId());
                        // Maç başlığını oluştur
                        String matchTitle = "";
                        if (yorum.getMac().getEvSahibiTakim() != null && yorum.getMac().getDeplasmanTakim() != null) {
                            matchTitle = yorum.getMac().getEvSahibiTakim().getAd() + " vs "
                                    + yorum.getMac().getDeplasmanTakim().getAd();
                        }
                        yorumMap.put("matchTitle", matchTitle);
                    }
                    return yorumMap;
                })
                .collect(Collectors.toList());

        // Kullanıcının oyuncu yorumlarını da ekle
        List<OyuncuYorumlari> oyuncuYorumlari = oyuncuYorumlariRepository
                .findByKullaniciOrderByOlusturmaTarihiDesc(kullanici);
        List<Map<String, Object>> oyuncuYorumListesi = oyuncuYorumlari.stream()
                .limit(10) // Son 10 oyuncu yorumu
                .map(oyYorum -> {
                    Map<String, Object> yorumMap = new HashMap<>();
                    yorumMap.put("commentId", "oyuncu_" + oyYorum.getId());
                    yorumMap.put("message", oyYorum.getIcerik());
                    yorumMap.put("createdAt", oyYorum.getOlusturmaTarihi());
                    if (oyYorum.getOyuncu() != null) {
                        yorumMap.put("playerId", oyYorum.getOyuncu().getId());
                        String playerName = (oyYorum.getOyuncu().getAd() != null ? oyYorum.getOyuncu().getAd() : "") +
                                " " + (oyYorum.getOyuncu().getSoyad() != null ? oyYorum.getOyuncu().getSoyad() : "");
                        yorumMap.put("matchTitle", "Oyuncu: " + playerName.trim()); // Frontend'de matchTitle olarak
                                                                                    // gösterilecek
                    }
                    return yorumMap;
                })
                .collect(Collectors.toList());

        // Tüm yorumları birleştir ve tarihe göre sırala
        recentComments.addAll(oyuncuYorumListesi);
        recentComments.sort((a, b) -> {
            java.time.LocalDateTime dateA = (java.time.LocalDateTime) a.get("createdAt");
            java.time.LocalDateTime dateB = (java.time.LocalDateTime) b.get("createdAt");
            if (dateA == null && dateB == null)
                return 0;
            if (dateA == null)
                return 1;
            if (dateB == null)
                return -1;
            return dateB.compareTo(dateA); // Azalan sırada
        });

        // Son 10 yorumu al
        kullaniciMap.put("recentComments", recentComments.stream().limit(10).collect(Collectors.toList()));

        return kullaniciMap;
    }
}
