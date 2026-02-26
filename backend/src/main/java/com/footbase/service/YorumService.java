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
        Mac mac = macRepository.findById(macId)
                .orElseThrow(() -> new RuntimeException("Mac bulunamadi"));
        return yorumRepository.findByMacOrderByYorumTarihiDesc(mac);
    }

    public Yorum yorumOlustur(Long macId, Long kullaniciId, String mesaj) {
        Mac mac = macRepository.findById(macId)
                .orElseThrow(() -> new RuntimeException("Mac bulunamadi"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));

        Yorum yorum = new Yorum();
        yorum.setMac(mac);
        yorum.setKullanici(kullanici);
        yorum.setMesaj(mesaj);
        return yorumRepository.save(yorum);
    }

    public Yorum yorumGuncelle(Long yorumId, Long kullaniciId, String mesaj) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadi"));

        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu guncelleme yetkiniz yok");
        }

        yorum.setMesaj(mesaj);
        return yorumRepository.save(yorum);
    }

    public void yorumSil(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findById(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadi"));

        if (!yorum.getKullanici().getId().equals(kullaniciId)) {
            throw new RuntimeException("Bu yorumu silme yetkiniz yok");
        }

        yorumRepository.delete(yorum);
    }

    public boolean yorumBegen(Long yorumId, Long kullaniciId) {
        Yorum yorum = yorumRepository.findByIdWithLikes(yorumId)
                .orElseThrow(() -> new RuntimeException("Yorum bulunamadi"));
        Kullanici kullanici = kullaniciRepository.findById(kullaniciId)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));

        boolean begenildi = yorum.getBegenenKullanicilar().contains(kullanici);
        if (begenildi) {
            yorum.getBegenenKullanicilar().remove(kullanici);
        } else {
            yorum.getBegenenKullanicilar().add(kullanici);
        }

        yorumRepository.save(yorum);
        return !begenildi;
    }

    public List<Yorum> sonYorumlariGetir(int limit) {
        return yorumRepository.findTopByOrderByYorumTarihiDesc(PageRequest.of(0, limit));
    }
}
