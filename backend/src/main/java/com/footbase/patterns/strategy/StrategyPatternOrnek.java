package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StrategyPatternOrnek implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StrategyPatternOrnek.class);

    @Autowired
    private DegerlendirmeContext context;

    @Autowired
    private DegerlendirmeStratejiFactory factory;

    @Override
    public void run(String... args) {
        // Sadece Strategy Pattern aktifse calistir
        if (Boolean.parseBoolean(System.getProperty("patterns.strategy.example", "false"))) {
            ornekleriGoster();
        }
    }

    public void ornekleriGoster() {
        logger.info("STRATEGY PATTERN KULLANIM ORNEKLERI");

        ornek1_TemelKullanim();
        ornek2_DirektHesaplama();
        ornek3_CokluDegerlendirme();
        ornek4_MacSenaryo();

        logger.info("Strategy Pattern ornekleri tamamlandi.");
    }

    private void ornek1_TemelKullanim() {
        logger.info("ORNEK 1: Temel Kullanim");

        // Admin degerlendirmesi
        context.stratejiSec("ADMIN");
        double adminPuan = context.puanHesapla(5);
        logger.info("Sonuc: {} puan", adminPuan);

        // Editor degerlendirmesi
        context.stratejiSec("EDITOR");
        double editorPuan = context.puanHesapla(5);
        logger.info("Sonuc: {} puan", editorPuan);

        // Normal kullanici degerlendirmesi
        context.stratejiSec("USER");
        double normalPuan = context.puanHesapla(5);
        logger.info("Sonuc: {} puan", normalPuan);
    }

    private void ornek2_DirektHesaplama() {
        logger.info("ORNEK 2: Direkt Hesaplama");

        double puan1 = context.hesapla("ADMIN", 4);
        logger.info("Admin 4 yildiz = {} puan", puan1);

        double puan2 = context.hesapla("EDITOR", 3);
        logger.info("Editor 3 yildiz = {} puan", puan2);

        double puan3 = context.hesapla("USER", 5);
        logger.info("Normal 5 yildiz = {} puan", puan3);
    }

    private void ornek3_CokluDegerlendirme() {
        logger.info("ORNEK 3: Coklu Degerlendirme");

        List<DegerlendirmeContext.Degerlendirme> degerlendirmeler = Arrays.asList(
                new DegerlendirmeContext.Degerlendirme("ADMIN", 5), // 15 puan
                new DegerlendirmeContext.Degerlendirme("ADMIN", 4), // 12 puan
                new DegerlendirmeContext.Degerlendirme("EDITOR", 5), // 10 puan
                new DegerlendirmeContext.Degerlendirme("EDITOR", 3), // 6 puan
                new DegerlendirmeContext.Degerlendirme("USER", 5), // 5 puan
                new DegerlendirmeContext.Degerlendirme("USER", 4), // 4 puan
                new DegerlendirmeContext.Degerlendirme("USER", 3) // 3 puan
        );

        double toplamPuan = context.toplamPuanHesapla(degerlendirmeler);
        double ortalama = context.ortalamaPuanHesapla(degerlendirmeler);

        logger.info("Toplam Puan: {}", toplamPuan);
        logger.info("Ortalama: {}/5.0", String.format("%.2f", ortalama));
    }

    private void ornek4_MacSenaryo() {
        logger.info("ORNEK 4: Gercek Mac Senaryosu");
        logger.info("Mac: Galatasaray vs Fenerbahce");

        // Degerlendirmeler
        List<DegerlendirmeContext.Degerlendirme> macDegerlendirmeleri = Arrays.asList(
                // Admin degerlendirmeleri
                new DegerlendirmeContext.Degerlendirme("ADMIN", 5),
                new DegerlendirmeContext.Degerlendirme("ADMIN", 5),

                // Editor degerlendirmeleri
                new DegerlendirmeContext.Degerlendirme("EDITOR", 4),
                new DegerlendirmeContext.Degerlendirme("EDITOR", 5),
                new DegerlendirmeContext.Degerlendirme("EDITOR", 4),

                // Normal kullanici degerlendirmeleri
                new DegerlendirmeContext.Degerlendirme("USER", 5),
                new DegerlendirmeContext.Degerlendirme("USER", 4),
                new DegerlendirmeContext.Degerlendirme("USER", 5),
                new DegerlendirmeContext.Degerlendirme("USER", 3),
                new DegerlendirmeContext.Degerlendirme("USER", 4)
        );

        double toplam = context.toplamPuanHesapla(macDegerlendirmeleri);
        double ortalama = context.ortalamaPuanHesapla(macDegerlendirmeleri);

        logger.info("Mac Degerlendirme Sonucu:");
        logger.info("   Toplam Puan: {}", toplam);
        logger.info("   Ortalama: {}/5.0", String.format("%.2f", ortalama));
        logger.info("   Siniflandirma: {}", siniflandir(ortalama));
    }

    private String siniflandir(double ortalama) {
        if (ortalama >= 4.5) {
            return "Mukemmel";
        }
        if (ortalama >= 3.5) {
            return "Cok Iyi";
        }
        if (ortalama >= 2.5) {
            return "Iyi";
        }
        if (ortalama >= 1.5) {
            return "Orta";
        }
        return "Zayif";
    }

    @SuppressWarnings("unused")
    private void ornekFactory() {
        logger.info("Factory ile Kullanim");

        // Factory'den strateji al
        DegerlendirmeStrateji strateji = factory.getStrateji("ADMIN");

        // Strateji bilgilerini goster
        logger.info("Strateji: {}", strateji.getStratejAdi());
        logger.info("Agirlik: {}x", strateji.getAgirlik());
        logger.info("Aciklama: {}", strateji.getAciklama());

        // Hesaplama yap
        double puan = strateji.puanHesapla(5);
        logger.info("Puan: {}", puan);
    }
}
