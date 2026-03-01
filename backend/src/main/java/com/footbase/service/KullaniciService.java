package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.OyuncuYorumlari;
import com.footbase.entity.Yorum;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.OyuncuYorumlariRepository;
import com.footbase.repository.YorumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
        return kullaniciBilgileriniMaple(kullanici);
    }

    public Map<String, Object> kullaniciGetirByEmail(String email) {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
        return kullaniciBilgileriniMaple(kullanici);
    }

    public Map<String, Object> kullaniciGuncelle(Long id, Kullanici kullanici) {
        Kullanici mevcutKullanici = kullaniciRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
        return kullaniciyiGuncelle(mevcutKullanici, kullanici);
    }

    public Map<String, Object> kullaniciGuncelleByEmail(String email, Kullanici kullanici) {
        Kullanici mevcutKullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
        return kullaniciyiGuncelle(mevcutKullanici, kullanici);
    }

    public List<Map<String, Object>> kullaniciGecmisiGetirByEmail(String email) {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
        return kullaniciGecmisiMaple(kullanici);
    }

    private Map<String, Object> kullaniciyiGuncelle(Kullanici mevcutKullanici, Kullanici kullanici) {
        if (kullanici.getEmail() != null && !kullanici.getEmail().isBlank()) {
            String yeniEmail = kullanici.getEmail().trim();
            if (!mevcutKullanici.getEmail().equalsIgnoreCase(yeniEmail) && kullaniciRepository.existsByEmail(yeniEmail)) {
                throw new RuntimeException("Bu e-posta zaten kullaniliyor");
            }
            mevcutKullanici.setEmail(yeniEmail);
        }

        if (kullanici.getKullaniciAdi() != null && !kullanici.getKullaniciAdi().isBlank()) {
            String yeniKullaniciAdi = kullanici.getKullaniciAdi().trim();
            if (!mevcutKullanici.getKullaniciAdi().equalsIgnoreCase(yeniKullaniciAdi)
                    && kullaniciRepository.existsByKullaniciAdi(yeniKullaniciAdi)) {
                throw new RuntimeException("Bu kullanici adi zaten kullaniliyor");
            }
            mevcutKullanici.setKullaniciAdi(yeniKullaniciAdi);
        }

        if (kullanici.getAd() != null) {
            mevcutKullanici.setAd(kullanici.getAd());
        }
        if (kullanici.getSoyad() != null) {
            mevcutKullanici.setSoyad(kullanici.getSoyad());
        }
        if (kullanici.getProfilResmi() != null) {
            mevcutKullanici.setProfilResmi(kullanici.getProfilResmi());
        }

        Kullanici guncellenmis = kullaniciRepository.save(mevcutKullanici);
        return kullaniciBilgileriniMaple(guncellenmis);
    }

    private Map<String, Object> kullaniciBilgileriniMaple(Kullanici kullanici) {
        Map<String, Object> kullaniciMap = new HashMap<>();
        kullaniciMap.put("id", kullanici.getId());
        kullaniciMap.put("email", kullanici.getEmail());
        kullaniciMap.put("kullaniciAdi", kullanici.getKullaniciAdi());
        kullaniciMap.put("displayName", kullanici.getKullaniciAdi());
        kullaniciMap.put("rol", kullanici.getRol());
        kullaniciMap.put("admin",
                kullanici.getAdmin() != null ? kullanici.getAdmin() : "ADMIN".equals(kullanici.getRol()));
        kullaniciMap.put("ad", kullanici.getAd() != null ? kullanici.getAd() : "");
        kullaniciMap.put("soyad", kullanici.getSoyad() != null ? kullanici.getSoyad() : "");
        kullaniciMap.put("profilResmi", kullanici.getProfilResmi() != null ? kullanici.getProfilResmi() : "");
        kullaniciMap.put("followersCount", 0);
        kullaniciMap.put("followingCount", 0);
        kullaniciMap.put("recentComments", kullaniciGecmisiMaple(kullanici).stream().limit(10).collect(Collectors.toList()));
        return kullaniciMap;
    }

    private List<Map<String, Object>> kullaniciGecmisiMaple(Kullanici kullanici) {
        List<Yorum> macYorumlari = yorumRepository.findByKullaniciOrderByYorumTarihiDesc(kullanici);
        List<Map<String, Object>> gecmis = macYorumlari.stream()
                .map(yorum -> {
                    Map<String, Object> yorumMap = new HashMap<>();
                    yorumMap.put("commentId", yorum.getId());
                    yorumMap.put("type", "MAC_YORUMU");
                    yorumMap.put("message", yorum.getMesaj());
                    yorumMap.put("createdAt", yorum.getYorumTarihi());
                    if (yorum.getMac() != null) {
                        yorumMap.put("matchId", yorum.getMac().getId());
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

        List<OyuncuYorumlari> oyuncuYorumlari = oyuncuYorumlariRepository
                .findByKullaniciOrderByOlusturmaTarihiDesc(kullanici);
        List<Map<String, Object>> oyuncuYorumListesi = oyuncuYorumlari.stream()
                .map(oyYorum -> {
                    Map<String, Object> yorumMap = new HashMap<>();
                    yorumMap.put("commentId", "oyuncu_" + oyYorum.getId());
                    yorumMap.put("type", "OYUNCU_PUANLAMASI");
                    yorumMap.put("message", oyYorum.getIcerik());
                    yorumMap.put("createdAt", oyYorum.getOlusturmaTarihi());
                    if (oyYorum.getOyuncu() != null) {
                        yorumMap.put("playerId", oyYorum.getOyuncu().getId());
                        String playerName = (oyYorum.getOyuncu().getAd() != null ? oyYorum.getOyuncu().getAd() : "") +
                                " " + (oyYorum.getOyuncu().getSoyad() != null ? oyYorum.getOyuncu().getSoyad() : "");
                        yorumMap.put("matchTitle", "Oyuncu: " + playerName.trim());
                    }
                    return yorumMap;
                })
                .collect(Collectors.toList());

        gecmis.addAll(oyuncuYorumListesi);
        gecmis.sort((a, b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("createdAt");
            LocalDateTime dateB = (LocalDateTime) b.get("createdAt");
            if (dateA == null && dateB == null) {
                return 0;
            }
            if (dateA == null) {
                return 1;
            }
            if (dateB == null) {
                return -1;
            }
            return dateB.compareTo(dateA);
        });
        return gecmis;
    }
}
