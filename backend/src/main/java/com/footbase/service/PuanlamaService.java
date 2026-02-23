package com.footbase.service;

import com.footbase.entity.Puanlama;
import com.footbase.entity.Kullanici;
import com.footbase.repository.PuanlamaRepository;
import com.footbase.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PuanlamaService {

    @Autowired
    private PuanlamaRepository puanlamaRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private com.footbase.repository.MacRepository macRepository;

    public List<Puanlama> macPuanlamalariniGetir(Long macId) {
        return puanlamaRepository.findByMacId(macId);
    }

    public Puanlama macPuanla(Long macId, Long kullaniciId, Integer puan, Integer agirlik) {
        // Puan kontrolü
        if (puan < 0 || puan > 100) {
            throw new RuntimeException("Puan 0-100 arasında olmalıdır");
        }

        // Ağırlık kontrolü
        if (agirlik == null || agirlik < 1 || agirlik > 3) {
            agirlik = 1; // Varsayılan ağırlık
        }

        com.footbase.entity.Mac mac = macRepository.findById(macId)
                .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Daha önce puanlama yapılmış mı kontrol et
        Puanlama mevcutPuanlama = puanlamaRepository
                .findByKullaniciIdAndMacId(kullaniciId, macId)
                .orElse(null);

        if (mevcutPuanlama != null) {
            // Mevcut puanlamayı güncelle
            mevcutPuanlama.setPuan(puan);
            mevcutPuanlama.setAgirlik(agirlik);
            return puanlamaRepository.save(mevcutPuanlama);
        } else {
            // Yeni puanlama oluştur
            Puanlama yeniPuanlama = new Puanlama();
            yeniPuanlama.setMac(mac);
            yeniPuanlama.setKullanici(kullanici);
            yeniPuanlama.setPuan(puan);
            yeniPuanlama.setAgirlik(agirlik);
            return puanlamaRepository.save(yeniPuanlama);
        }
    }

    public Map<String, Object> macOrtalamaPuaniniGetir(Long macId) {
        Double ortalamaPuan = puanlamaRepository.findOrtalamaPuanByMacId(macId);
        Long puanlamaSayisi = puanlamaRepository.countByMacId(macId);

        Map<String, Object> sonuc = new HashMap<>();
        sonuc.put("ortalamaPuan", ortalamaPuan != null ? ortalamaPuan : 0.0);
        sonuc.put("puanlamaSayisi", puanlamaSayisi);

        return sonuc;
    }
}
