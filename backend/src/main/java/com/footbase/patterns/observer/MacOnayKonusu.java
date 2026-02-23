package com.footbase.patterns.observer;

import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MacOnayKonusu implements Konu {

    private static final Logger logger = LoggerFactory.getLogger(MacOnayKonusu.class);

    private final List<Gozlemci> gozlemciler = new ArrayList<>();

    private Mac aktifMac;

    private String aktifOlayTipi;

    @Autowired(required = false) // required=false cunku test ortaminda olmayabilir
    private BildirimRepository bildirimRepository;

    @Autowired(required = false)
    private KullaniciRepository kullaniciRepository;

    public MacOnayKonusu() {
        logger.info("MacOnayKonusu olusturuldu (Observer Pattern Subject)");
    }

    @Override
    public void ekle(Gozlemci gozlemci) {
        if (gozlemci == null) {
            logger.warn("Null gozlemci eklenemez!");
            return;
        }

        if (!gozlemciler.contains(gozlemci)) {
            gozlemciler.add(gozlemci);

            // Repository'leri gozlemciye enjekte et
            // Gozlemci, YoneticiGozlemci veya EditorGozlemci olabilir
            if (gozlemci instanceof YoneticiGozlemci) {
                YoneticiGozlemci yonetici = (YoneticiGozlemci) gozlemci;
                yonetici.setRepositories(bildirimRepository, kullaniciRepository);
                logger.info("Yonetici gozlemci eklendi: ID={}", yonetici.getYoneticiId());
            } else if (gozlemci instanceof EditorGozlemci) {
                EditorGozlemci editor = (EditorGozlemci) gozlemci;
                editor.setRepositories(bildirimRepository, kullaniciRepository);
                logger.info("Editor gozlemci eklendi: ID={}", editor.getEditorId());
            } else {
                logger.info("Gozlemci eklendi: {}", gozlemci.getClass().getSimpleName());
            }
        } else {
            logger.debug("Gozlemci zaten kayitli, tekrar eklenmedi.");
        }
    }

    @Override
    public void cikar(Gozlemci gozlemci) {
        if (gozlemci == null) {
            logger.warn("Null gozlemci cikarilamaz!");
            return;
        }

        boolean cikarildi = gozlemciler.remove(gozlemci);
        if (cikarildi) {
            if (gozlemci instanceof YoneticiGozlemci) {
                YoneticiGozlemci yonetici = (YoneticiGozlemci) gozlemci;
                logger.info("Yonetici gozlemci cikarildi: ID={}", yonetici.getYoneticiId());
            } else if (gozlemci instanceof EditorGozlemci) {
                EditorGozlemci editor = (EditorGozlemci) gozlemci;
                logger.info("Editor gozlemci cikarildi: ID={}", editor.getEditorId());
            } else {
                logger.info("Gozlemci cikarildi: {}", gozlemci.getClass().getSimpleName());
            }
        } else {
            logger.debug("Gozlemci listede bulunamadi, cikarilamadi.");
        }
    }

    @Override
    public void gozlemcileriBilgilendir() {
        if (gozlemciler.isEmpty()) {
            logger.warn("Hic gozlemci kayitli degil, bildirim gonderilmedi!");
            return;
        }

        logger.info("{} gozlemciye bildirim gonderiliyor: Olay={}, Mac ID={}",
                gozlemciler.size(), aktifOlayTipi,
                aktifMac != null ? aktifMac.getId() : "null");

        // Her gozlemciye bildirim gonder
        int basariliGonderim = 0;
        for (Gozlemci gozlemci : gozlemciler) {
            try {
                gozlemci.guncelle(aktifOlayTipi, aktifMac);
                basariliGonderim++;
            } catch (Exception e) {
                logger.error("Gozlemci bilgilendirilirken hata: {}", e.getMessage(), e);
                // Diger gozlemcilere devam et
            }
        }

        logger.info("Bildirim tamamlandi: {}/{} gozlemciye basariyla ulasildi",
                basariliGonderim, gozlemciler.size());
    }

    public void macEklendi(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile macEklendi cagrildi!");
            return;
        }

        logger.info("Yeni mac eklendi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_EKLENDI";
        gozlemcileriBilgilendir();
    }

    public void macOnaylandi(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile macOnaylandi cagrildi!");
            return;
        }

        logger.info("Mac onaylandi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_ONAYLANDI";
        gozlemcileriBilgilendir();
    }

    public void macReddedildi(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile macReddedildi cagrildi!");
            return;
        }

        logger.info("Mac reddedildi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_REDDEDILDI";
        gozlemcileriBilgilendir();
    }

    public void macBasladi(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile macBasladi cagrildi!");
            return;
        }

        logger.info("Mac basladi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_BASLADI";
        gozlemcileriBilgilendir();
    }

    public void macBitti(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile macBitti cagrildi!");
            return;
        }

        logger.info("Mac bitti: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "MAC_BITTI";
        gozlemcileriBilgilendir();
    }

    public void golAtildi(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile golAtildi cagrildi!");
            return;
        }

        logger.info("Gol atildi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "GOL_ATILDI";
        gozlemcileriBilgilendir();
    }

    public void yeniYorum(Mac mac) {
        if (mac == null) {
            logger.error("Null mac ile yeniYorum cagrildi!");
            return;
        }

        logger.info("Yeni yorum eklendi: Mac ID={}", mac.getId());
        this.aktifMac = mac;
        this.aktifOlayTipi = "YENI_YORUM";
        gozlemcileriBilgilendir();
    }

    public int getGozlemciSayisi() {
        return gozlemciler.size();
    }

    public void tumGozlemcileriTemizle() {
        int oncekiSayi = gozlemciler.size();
        gozlemciler.clear();
        logger.info("Tum gozlemciler temizlendi: {} gozlemci silindi", oncekiSayi);
    }

    @Override
    public String toString() {
        return "MacOnayKonusu{" +
                "gozlemciSayisi=" + gozlemciler.size() +
                ", aktifOlayTipi='" + aktifOlayTipi + '\'' +
                ", aktifMacId=" + (aktifMac != null ? aktifMac.getId() : "null") +
                '}';
    }
}
