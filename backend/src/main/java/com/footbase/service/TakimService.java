package com.footbase.service;

import com.footbase.entity.Takim;
import com.footbase.repository.TakimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TakimService {

    @Autowired
    private TakimRepository takimRepository;

    public List<Takim> tumTakimlariGetir() {
        return takimRepository.findAll();
    }

    public Takim takimGetir(Long id) {
        return takimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));
    }

    public Takim takimOlustur(Takim takim) {
        if (takimRepository.existsByAd(takim.getAd())) {
            throw new RuntimeException("Bu takım adı zaten kullanılıyor");
        }
        return takimRepository.save(takim);
    }

    public Takim takimGuncelle(Long id, Takim takim) {
        Takim mevcutTakim = takimGetir(id);

        if (takim.getAd() != null) {
            // Takım adı değişiyorsa kontrol et
            if (!mevcutTakim.getAd().equals(takim.getAd()) && takimRepository.existsByAd(takim.getAd())) {
                throw new RuntimeException("Bu takım adı zaten kullanılıyor");
            }
            mevcutTakim.setAd(takim.getAd());
        }
        if (takim.getLogo() != null) {
            mevcutTakim.setLogo(takim.getLogo());
        }
        if (takim.getKurulusYili() != null) {
            mevcutTakim.setKurulusYili(takim.getKurulusYili());
        }
        if (takim.getStadyum() != null) {
            mevcutTakim.setStadyum(takim.getStadyum());
        }
        if (takim.getLig() != null && takim.getLig().getId() != null) {
            mevcutTakim.setLig(takim.getLig());
        }
        if (takim.getTeknikDirektor() != null && takim.getTeknikDirektor().getId() != null) {
            mevcutTakim.setTeknikDirektor(takim.getTeknikDirektor());
        }

        return takimRepository.save(mevcutTakim);
    }

    public void takimSil(Long id) {
        Takim takim = takimGetir(id);
        takimRepository.delete(takim);
    }
}
