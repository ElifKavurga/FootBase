package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DegerlendirmeContext {

    private static final Logger logger = LoggerFactory.getLogger(DegerlendirmeContext.class);

    @Autowired
    private AdminDegerlendirmeStrateji adminStrateji;

    @Autowired
    private EditorDegerlendirmeStrateji editorStrateji;

    @Autowired
    private NormalKullaniciDegerlendirmeStrateji normalKullaniciStrateji;

    private DegerlendirmeStrateji aktifStrateji;

    public DegerlendirmeContext() {
        logger.info("DegerlendirmeContext olusturuldu (Strategy Pattern)");
    }

    public void stratejiSec(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("Rol bos olamaz!");
            throw new IllegalArgumentException("Rol bos olamaz!");
        }

        String normalizedRol = rol.toUpperCase().trim();

        aktifStrateji = switch (normalizedRol) {
            case "ADMIN", "YONETICI" -> {
                logger.info("Admin stratejisi secildi (3x agirlik)");
                yield adminStrateji;
            }
            case "EDITOR", "EDITR", "EDIT\u00D6R" -> {
                logger.info("Editor stratejisi secildi (2x agirlik)");
                yield editorStrateji;
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("Normal kullanici stratejisi secildi (1x agirlik)");
                yield normalKullaniciStrateji;
            }
            default -> {
                logger.error("Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Gecersiz rol: " + rol);
            }
        };

        logger.debug("Strateji degistirildi: {}", aktifStrateji.getStratejAdi());
    }

    public double puanHesapla(int yildizSayisi) {
        if (aktifStrateji == null) {
            logger.error("Strateji secilmedi! Once stratejiSec() cagrin.");
            throw new IllegalStateException("Strateji secilmedi!");
        }

        return aktifStrateji.puanHesapla(yildizSayisi);
    }

    public double hesapla(String rol, int yildizSayisi) {
        stratejiSec(rol);
        return puanHesapla(yildizSayisi);
    }

    public String getAktifStrateji() {
        if (aktifStrateji == null) {
            return "Strateji secilmedi";
        }

        return String.format("%s (Agirlik: %.1fx)", aktifStrateji.getStratejAdi(), aktifStrateji.getAgirlik());
    }

    public double getAgirlik() {
        if (aktifStrateji == null) {
            throw new IllegalStateException("Strateji secilmedi!");
        }

        return aktifStrateji.getAgirlik();
    }

    public double toplamPuanHesapla(java.util.List<Degerlendirme> degerlendirmeler) {
        logger.info("Toplam {} degerlendirme hesaplaniyor...", degerlendirmeler.size());

        double toplam = 0.0;
        int adminSayisi = 0;
        int editorSayisi = 0;
        int normalSayisi = 0;

        for (Degerlendirme deg : degerlendirmeler) {
            double puan = hesapla(deg.getRol(), deg.getYildizSayisi());
            toplam += puan;

            // Istatistik
            String rol = deg.getRol().toUpperCase();
            if (rol.equals("ADMIN") || rol.equals("YONETICI")) {
                adminSayisi++;
            } else if (rol.equals("EDITOR") || rol.equals("EDITR") || rol.equals("EDIT\u00D6R")) {
                editorSayisi++;
            } else {
                normalSayisi++;
            }
        }

        logger.info("Degerlendirme istatistikleri:");
        logger.info("   Admin: {} degerlendirme (3x agirlik)", adminSayisi);
        logger.info("   Editor: {} degerlendirme (2x agirlik)", editorSayisi);
        logger.info("   Normal: {} degerlendirme (1x agirlik)", normalSayisi);
        logger.info("   Toplam Puan: {}", toplam);

        return toplam;
    }

    public double ortalamaPuanHesapla(java.util.List<Degerlendirme> degerlendirmeler) {
        if (degerlendirmeler == null || degerlendirmeler.isEmpty()) {
            logger.warn("Degerlendirme listesi bos!");
            return 0.0;
        }

        double toplamPuan = toplamPuanHesapla(degerlendirmeler);

        // Agirliklari topla
        double toplamAgirlik = 0.0;
        for (Degerlendirme deg : degerlendirmeler) {
            stratejiSec(deg.getRol());
            toplamAgirlik += getAgirlik();
        }

        // Normalize et (0-5 arasi)
        double ortalama = (toplamPuan / toplamAgirlik);

        logger.info("Ortalama Puan: {}/5.0", String.format("%.2f", ortalama));
        return ortalama;
    }

    public static class Degerlendirme {
        private String rol;
        private int yildizSayisi;

        public Degerlendirme(String rol, int yildizSayisi) {
            this.rol = rol;
            this.yildizSayisi = yildizSayisi;
        }

        public String getRol() {
            return rol;
        }

        public int getYildizSayisi() {
            return yildizSayisi;
        }
    }
}
