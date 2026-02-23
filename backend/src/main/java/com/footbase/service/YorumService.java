package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.entity.Yorum;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.MacRepository;
import com.footbase.repository.YorumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YorumService {

    @Autowired
    private YorumRepository yorumRepository;

    @Autowired
    private MacRepository macRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    public List<Yorum> macYorumlariniGetir(Long macId) {
        return yorumRepository.findByMacOrderByYorumTarihiDesc(
                macRepository.findById(macId)
                        .orElseThrow(() -> new RuntimeException("Maç bulunamadı")));
    }

    public Yorum yorumOlustur(Long macId, Long kullaniciId, String mesaj) {
        Mac mac = macRepository.findById(macId)
                .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Yorum yorum = new Yorum();
        yorum.setMac(mac);
        yorum.setKullanici(kullanici);
        yorum.setMesaj(mesaj);

        return yorumRepository.save(yorum);
    }

    public Yorum yorumGuncelle(Long yorumId, Long kullaniciId, String mesaj) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));

        // Yorum sahibi kontrolü
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu güncelleme yetkiniz yok");
        }

        yorum.setMesaj(mesaj);
        return yorumRepository.save(yorum);
    }

    public void yorumSil(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));

        // Yorum sahibi kontrolü
        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu silme yetkiniz yok");
        }

        yorumRepository.delete(yorum);
    }

    public boolean yorumBegen(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Beğeni durumunu kontrol et
        boolean begenildi = yorum.getBegenenKullanicilar().contains(kullanici);

        if (begenildi) {
            // Beğeniyi kaldır
            yorum.getBegenenKullanicilar().remove(kullanici);
        } else {
            // Beğen
            yorum.getBegenenKullanicilar().add(kullanici);
        }

        yorumRepository.save(yorum);
        return !begenildi; // Yeni durum
    }

    public List<Yorum> sonYorumlariGetir(int limit) {
        return yorumRepository.findTopByOrderByYorumTarihiDesc(PageRequest.of(0, limit));
    }
}
