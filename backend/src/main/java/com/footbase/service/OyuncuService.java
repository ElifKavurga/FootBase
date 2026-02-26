package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Oyuncu;
import com.footbase.entity.OyuncuPuanlari;
import com.footbase.entity.OyuncuYorumlari;
import com.footbase.entity.Takim;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.OyuncuPuanlariRepository;
import com.footbase.repository.OyuncuRepository;
import com.footbase.repository.OyuncuYorumlariRepository;
import com.footbase.repository.TakimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OyuncuService {

    @Autowired
    private OyuncuRepository oyuncuRepository;

    @Autowired
    private TakimRepository takimRepository;

    @Autowired
    private OyuncuYorumlariRepository oyuncuYorumlariRepository;

    @Autowired
    private OyuncuPuanlariRepository oyuncuPuanlariRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    public List<Oyuncu> tumOyunculariGetir() {
        return oyuncuRepository.findAll();
    }

    public Oyuncu oyuncuGetir(Long id) {
        return oyuncuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oyuncu bulunamadi"));
    }

    public Map<String, Object> oyuncuDetaylariniGetir(Long id) {
        Oyuncu oyuncu = oyuncuGetir(id);
        Map<String, Object> skorOzeti = oyuncuSkorunuGetir(id);
        double ortalamaPuan = (double) skorOzeti.get("score");
        int puanSayisi = (int) skorOzeti.get("ratingCount");

        Map<String, Object> detay = new HashMap<>();
        detay.put("id", oyuncu.getId());
        detay.put("ad", oyuncu.getAd());
        detay.put("soyad", oyuncu.getSoyad());
        detay.put("fullName", oyuncu.getAd() + " " + oyuncu.getSoyad());
        detay.put("pozisyon", oyuncu.getPozisyon());
        detay.put("position", oyuncu.getPozisyon());
        detay.put("dogumTarihi", oyuncu.getDogumTarihi());
        detay.put("milliyet", oyuncu.getMilliyet());
        detay.put("fotograf", oyuncu.getFotograf());
        detay.put("imageUrl", oyuncu.getFotograf());
        detay.put("averageRating", ortalamaPuan);
        detay.put("ortalamaPuan", ortalamaPuan);
        detay.put("ratingCount", puanSayisi);

        if (oyuncu.getTakim() != null) {
            Map<String, Object> takimMap = new HashMap<>();
            takimMap.put("id", oyuncu.getTakim().getId());
            takimMap.put("ad", oyuncu.getTakim().getAd());
            takimMap.put("logo", oyuncu.getTakim().getLogo());
            detay.put("takim", takimMap);
            detay.put("team", oyuncu.getTakim().getAd());
        }

        return detay;
    }

    public List<Oyuncu> takimaGoreOyunculariGetir(Long takimId) {
        return oyuncuRepository.findByTakimId(takimId);
    }

    public List<Oyuncu> pozisyonaGoreOyunculariGetir(String pozisyon) {
        return oyuncuRepository.findByPozisyon(pozisyon);
    }

    public Oyuncu oyuncuOlustur(Oyuncu oyuncu) {
        if (oyuncu.getTakim() != null && oyuncu.getTakim().getId() != null) {
            Takim takim = takimRepository.findById(oyuncu.getTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Takim bulunamadi"));
            oyuncu.setTakim(takim);
        }

        return oyuncuRepository.save(oyuncu);
    }

    public Oyuncu oyuncuGuncelle(Long id, Oyuncu oyuncu) {
        Oyuncu mevcutOyuncu = oyuncuGetir(id);

        if (oyuncu.getAd() != null) {
            mevcutOyuncu.setAd(oyuncu.getAd());
        }
        if (oyuncu.getSoyad() != null) {
            mevcutOyuncu.setSoyad(oyuncu.getSoyad());
        }
        if (oyuncu.getPozisyon() != null) {
            mevcutOyuncu.setPozisyon(oyuncu.getPozisyon());
        }
        if (oyuncu.getDogumTarihi() != null) {
            mevcutOyuncu.setDogumTarihi(oyuncu.getDogumTarihi());
        }
        if (oyuncu.getMilliyet() != null) {
            mevcutOyuncu.setMilliyet(oyuncu.getMilliyet());
        }
        if (oyuncu.getFotograf() != null) {
            mevcutOyuncu.setFotograf(oyuncu.getFotograf());
        }
        if (oyuncu.getTakim() != null && oyuncu.getTakim().getId() != null) {
            Takim takim = takimRepository.findById(oyuncu.getTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Takim bulunamadi"));
            mevcutOyuncu.setTakim(takim);
        }

        return oyuncuRepository.save(mevcutOyuncu);
    }

    public void oyuncuSil(Long id) {
        Oyuncu oyuncu = oyuncuGetir(id);
        oyuncuRepository.delete(oyuncu);
    }

    public List<Map<String, Object>> oyuncuYorumlariniGetir(Long oyuncuId) {
        oyuncuGetir(oyuncuId);
        List<OyuncuYorumlari> yorumlar = oyuncuYorumlariRepository.findByOyuncuIdOrderByOlusturmaTarihiDesc(oyuncuId);
        return yorumlar.stream().map(yorum -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", yorum.getId());
            map.put("comment", yorum.getIcerik());
            map.put("icerik", yorum.getIcerik());
            map.put("score", puanCikar(yorum.getIcerik()));
            map.put("author", yorum.getKullanici() != null ? yorum.getKullanici().getKullaniciAdi() : null);
            map.put("kullaniciId", yorum.getKullanici() != null ? yorum.getKullanici().getId() : null);
            map.put("olusturmaTarihi", yorum.getOlusturmaTarihi());
            return map;
        }).collect(Collectors.toList());
    }

    public OyuncuYorumlari oyuncuYorumEkle(Long oyuncuId, Long kullaniciId, Integer score, String comment) {
        Oyuncu oyuncu = oyuncuGetir(oyuncuId);
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));

        if (score != null && (score < 1 || score > 10)) {
            throw new RuntimeException("Puan 1-10 arasinda olmali");
        }

        String icerik = formatDegerlendirmeIcerigi(score, comment);
        if (icerik.isEmpty()) {
            throw new RuntimeException("Yorum icerigi gereklidir");
        }

        OyuncuYorumlari yorum = new OyuncuYorumlari();
        yorum.setOyuncu(oyuncu);
        yorum.setKullanici(kullanici);
        yorum.setIcerik(icerik);

        OyuncuYorumlari kaydedilen = oyuncuYorumlariRepository.save(yorum);
        oyuncuPuaniYenile(oyuncuId);
        return kaydedilen;
    }

    public OyuncuYorumlari oyuncuYorumGuncelle(Long oyuncuId, Long yorumId, Long kullaniciId, Integer score, String comment) {
        oyuncuGetir(oyuncuId);
        OyuncuYorumlari yorum = oyuncuYorumlariRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Degerlendirme bulunamadi"));

        if (!yorum.getOyuncu().getId().equals(oyuncuId)) {
            throw new RuntimeException("Degerlendirme bu oyuncuya ait degil");
        }
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu degerlendirmeyi guncelleme yetkiniz yok");
        }
        if (score != null && (score < 1 || score > 10)) {
            throw new RuntimeException("Puan 1-10 arasinda olmali");
        }

        String icerik = formatDegerlendirmeIcerigi(score, comment);
        if (icerik.isEmpty()) {
            throw new RuntimeException("Yorum icerigi gereklidir");
        }

        yorum.setIcerik(icerik);
        OyuncuYorumlari kaydedilen = oyuncuYorumlariRepository.save(yorum);
        oyuncuPuaniYenile(oyuncuId);
        return kaydedilen;
    }

    public void oyuncuYorumSil(Long oyuncuId, Long yorumId, Long kullaniciId) {
        oyuncuGetir(oyuncuId);
        OyuncuYorumlari yorum = oyuncuYorumlariRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Degerlendirme bulunamadi"));

        if (!yorum.getOyuncu().getId().equals(oyuncuId)) {
            throw new RuntimeException("Degerlendirme bu oyuncuya ait degil");
        }
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu degerlendirmeyi silme yetkiniz yok");
        }

        oyuncuYorumlariRepository.delete(yorum);
        oyuncuPuaniYenile(oyuncuId);
    }

    public Map<String, Object> oyuncuSkorunuGetir(Long oyuncuId) {
        oyuncuGetir(oyuncuId);
        List<OyuncuYorumlari> yorumlar = oyuncuYorumlariRepository.findByOyuncuIdOrderByOlusturmaTarihiDesc(oyuncuId);

        List<Integer> puanlar = yorumlar.stream()
                .map(yorum -> puanCikar(yorum.getIcerik()))
                .filter(Objects::nonNull)
                .map(Double::intValue)
                .collect(Collectors.toList());

        double ortalama = puanlar.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        Map<String, Object> sonuc = new HashMap<>();
        sonuc.put("oyuncuId", oyuncuId);
        sonuc.put("score", ortalama);
        sonuc.put("puan", ortalama);
        sonuc.put("ratingCount", puanlar.size());
        return sonuc;
    }

    private String formatDegerlendirmeIcerigi(Integer score, String comment) {
        String temizYorum = comment != null ? comment.trim() : "";
        if (score != null) {
            return String.format("[%d/10] %s", score, temizYorum).trim();
        }
        return temizYorum;
    }

    private Double puanCikar(String icerik) {
        if (icerik == null || !icerik.startsWith("[") || !icerik.contains("/10]")) {
            return null;
        }
        try {
            String puanStr = icerik.substring(1, icerik.indexOf("/10]"));
            return Double.parseDouble(puanStr);
        } catch (Exception e) {
            return null;
        }
    }

    private void oyuncuPuaniYenile(Long oyuncuId) {
        Map<String, Object> skorOzeti = oyuncuSkorunuGetir(oyuncuId);
        double score = (double) skorOzeti.get("score");

        OyuncuPuanlari oyuncuPuani = oyuncuPuanlariRepository.findByOyuncuId(oyuncuId).orElseGet(() -> {
            OyuncuPuanlari yeni = new OyuncuPuanlari();
            yeni.setOyuncuId(oyuncuId);
            return yeni;
        });

        oyuncuPuani.setPuan(BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP));
        oyuncuPuanlariRepository.save(oyuncuPuani);
    }
}
