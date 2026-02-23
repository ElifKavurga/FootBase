package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminDegerlendirmeStrateji implements DegerlendirmeStrateji {

    private static final Logger logger = LoggerFactory.getLogger(AdminDegerlendirmeStrateji.class);
    private static final double AGIRLIK = 3.0;

    @Override
    public double puanHesapla(int yildizSayisi) {
        if (yildizSayisi < 1 || yildizSayisi > 5) {
            logger.error("Gecersiz yildiz sayisi: {} (1-5 arasi olmali)", yildizSayisi);
            throw new IllegalArgumentException("Yildiz sayisi 1-5 arasinda olmali!");
        }

        double puan = yildizSayisi * AGIRLIK;
        logger.info("Admin degerlendirme: {} yildiz x {} = {} puan", yildizSayisi, AGIRLIK, puan);
        return puan;
    }

    @Override
    public double getAgirlik() {
        return AGIRLIK;
    }

    @Override
    public String getStratejAdi() {
        return "ADMIN_STRATEJISI";
    }

    @Override
    public String getAciklama() {
        return "Admin degerlendirmeleri 3 kat agirliklidir";
    }

    @Override
    public String toString() {
        return String.format("AdminDegerlendirmeStrateji{agirlik=%.1fx}", AGIRLIK);
    }
}
