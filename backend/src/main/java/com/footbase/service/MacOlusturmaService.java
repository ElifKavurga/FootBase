package com.footbase.service;

import com.footbase.entity.*;
import com.footbase.patterns.builder.MacBuilderInterface;
import com.footbase.patterns.builder.MacDirector;
import com.footbase.patterns.builder.StandardMacBuilder;
import com.footbase.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class MacOlusturmaService {

    private static final Logger logger = LoggerFactory.getLogger(MacOlusturmaService.class);

    private final MacDirector director;
    private final TakimRepository takimRepository;
    private final StadyumRepository stadyumRepository;
    private final HakemRepository hakemRepository;
    private final LigRepository ligRepository;
    private final OrganizasyonRepository organizasyonRepository;

    @Autowired
    public MacOlusturmaService(
            TakimRepository takimRepository,
            StadyumRepository stadyumRepository,
            HakemRepository hakemRepository,
            LigRepository ligRepository,
            OrganizasyonRepository organizasyonRepository) {

        this.takimRepository = takimRepository;
        this.stadyumRepository = stadyumRepository;
        this.hakemRepository = hakemRepository;
        this.ligRepository = ligRepository;
        this.organizasyonRepository = organizasyonRepository;

        // Builder Pattern: Director'ı başlat
        MacBuilderInterface builder = new StandardMacBuilder();
        this.director = new MacDirector(builder);

        logger.info("MacOlusturmaService başlatıldı (Builder Pattern ile)");
    }

    public Mac editorHizliMacOlustur(Long evSahibiId, Long deplasmanId, LocalDate tarih, LocalTime saat) {
        logger.info("Editor hızlı maç oluşturuluyor (Builder Pattern)");

        Takim evSahibi = takimRepository.findById(evSahibiId)
                .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı: " + evSahibiId));

        Takim deplasman = takimRepository.findById(deplasmanId)
                .orElseThrow(() -> new RuntimeException("Deplasman takımı bulunamadı: " + deplasmanId));

        // Builder Pattern: Director ile basit lig maçı oluştur
        Mac mac = director.yaratLigMaci(evSahibi, deplasman, tarih, saat);

        logger.info("Hızlı maç oluşturuldu: {} vs {}", evSahibi.getAd(), deplasman.getAd());
        return mac;
    }

    public Mac adminDetayliMacOlustur(
            Long evSahibiId,
            Long deplasmanId,
            LocalDate tarih,
            LocalTime saat,
            Long stadyumId,
            Long hakemId,
            Long ligId) {

        logger.info("Admin detaylı maç oluşturuluyor (Builder Pattern)");

        // Takımları getir
        Takim evSahibi = takimRepository.findById(evSahibiId)
                .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));

        Takim deplasman = takimRepository.findById(deplasmanId)
                .orElseThrow(() -> new RuntimeException("Deplasman takımı bulunamadı"));

        // Opsiyonel alanları getir
        Stadyum stadyum = stadyumId != null ? stadyumRepository.findById(stadyumId).orElse(null) : null;

        Hakem hakem = hakemId != null ? hakemRepository.findById(hakemId).orElse(null) : null;

        Lig lig = ligId != null ? ligRepository.findById(ligId).orElse(null) : null;

        // Builder Pattern: Director ile detaylı lig maçı oluştur
        Mac mac = director.yaratDetayliLigMaci(
                evSahibi, deplasman, tarih, saat,
                stadyum, hakem, lig);

        logger.info("Detaylı maç oluşturuldu: {} vs {}", evSahibi.getAd(), deplasman.getAd());
        return mac;
    }

    public Mac derbiMaciOlustur(Long evSahibiId, Long deplasmanId, LocalDate tarih, LocalTime saat) {
        logger.info("Derbi maçı oluşturuluyor (Builder Pattern)");

        Takim evSahibi = takimRepository.findById(evSahibiId)
                .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));

        Takim deplasman = takimRepository.findById(deplasmanId)
                .orElseThrow(() -> new RuntimeException("Deplasman takımı bulunamadı"));

        // Ev sahibi stadyumu otomatik getir (eğer mevcutsa)
        Stadyum stadyum = evSahibi.getStadyum();

        // En iyi hakemi seç (varsayılan: ilk hakem)
        Hakem hakem = hakemRepository.findAll().stream().findFirst().orElse(null);

        // Builder Pattern: Director ile derbi maçı oluştur (otomatik not eklenir)
        Mac mac = director.yaratDerbiMaci(evSahibi, deplasman, tarih, saat, stadyum, hakem);

        logger.info("Derbi maçı oluşturuldu: {} vs {} (Otomatik güvenlik notu eklendi)",
                evSahibi.getAd(), deplasman.getAd());
        return mac;
    }

    public Mac sampiyonlukMaciOlustur(
            Long evSahibiId,
            Long deplasmanId,
            LocalDate tarih,
            LocalTime saat,
            Long ligId) {

        logger.info("Şampiyonluk maçı oluşturuluyor (Builder Pattern)");

        Takim evSahibi = takimRepository.findById(evSahibiId)
                .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));

        Takim deplasman = takimRepository.findById(deplasmanId)
                .orElseThrow(() -> new RuntimeException("Deplasman takımı bulunamadı"));

        Stadyum stadyum = evSahibi.getStadyum();
        Hakem hakem = hakemRepository.findAll().stream().findFirst().orElse(null);

        Lig lig = ligRepository.findById(ligId)
                .orElseThrow(() -> new RuntimeException("Lig bulunamadı"));

        // Builder Pattern: Director ile şampiyonluk maçı oluştur (otomatik not eklenir)
        Mac mac = director.yaratSampiyonlukMaci(
                evSahibi, deplasman, tarih, saat,
                stadyum, hakem, lig);

        logger.info("Şampiyonluk maçı oluşturuldu: {} vs {} (Otomatik önem notu eklendi)",
                evSahibi.getAd(), deplasman.getAd());
        return mac;
    }

    public Mac tamKapsamliMacOlustur(
            Long evSahibiId,
            Long deplasmanId,
            LocalDate tarih,
            LocalTime saat,
            Long stadyumId,
            Long hakemId,
            Long ligId,
            Long organizasyonId,
            String not) {

        logger.info("Tam kapsamlı maç oluşturuluyor (Builder Pattern)");

        Takim evSahibi = takimRepository.findById(evSahibiId).orElseThrow();
        Takim deplasman = takimRepository.findById(deplasmanId).orElseThrow();
        Stadyum stadyum = stadyumRepository.findById(stadyumId).orElse(null);
        Hakem hakem = hakemRepository.findById(hakemId).orElse(null);
        Lig lig = ligRepository.findById(ligId).orElse(null);
        Organizasyon organizasyon = organizasyonRepository.findById(organizasyonId).orElse(null);

        // Builder Pattern: Director ile tam kapsamlı maç oluştur
        Mac mac = director.yaratTamKapsamliMac(
                evSahibi, deplasman, tarih, saat,
                stadyum, hakem, lig, organizasyon, not);

        logger.info("Tam kapsamlı maç oluşturuldu: {} vs {}",
                evSahibi.getAd(), deplasman.getAd());
        return mac;
    }
}
