package com.footbase.patterns.observer;

import com.footbase.entity.Bildirim;
import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoneticiGozlemci implements Gozlemci {

    private static final Logger logger = LoggerFactory.getLogger(YoneticiGozlemci.class);

    private final Long yoneticiId;

    private final String yoneticiEmail;

    private BildirimRepository bildirimRepository;

    private KullaniciRepository kullaniciRepository;

    public YoneticiGozlemci(Kullanici yonetici) {
        this.yoneticiId = yonetici.getId();
        this.yoneticiEmail = yonetici.getEmail();
    }

    public YoneticiGozlemci(Long yoneticiId, String yoneticiEmail) {
        this.yoneticiId = yoneticiId;
        this.yoneticiEmail = yoneticiEmail;
    }

    public void setRepositories(BildirimRepository bildirimRepository,
            KullaniciRepository kullaniciRepository) {
        this.bildirimRepository = bildirimRepository;
        this.kullaniciRepository = kullaniciRepository;
    }

    @Override
    public void guncelle(String olayTipi, Object veri) {
        // Veri tipini kontrol et (tip guvenligi)
        if (veri instanceof Mac) {
            Mac mac = (Mac) veri;
            macOlayiniIsle(olayTipi, mac);
        } else {
            logger.warn("Yonetici Gozlemci: Bilinmeyen veri tipi geldi: {}",
                    veri != null ? veri.getClass().getName() : "null");
        }
    }

    private void macOlayiniIsle(String olayTipi, Mac mac) {
        switch (olayTipi) {
            case "MAC_EKLENDI":
                macEklendiOlayiniIsle(mac);
                break;

            case "MAC_ONAYLANDI":
                macOnaylandiOlayiniIsle(mac);
                break;

            case "MAC_REDDEDILDI":
                macReddedildiOlayiniIsle(mac);
                break;

            case "MAC_BASLADI":
                macBasladiOlayiniIsle(mac);
                break;

            case "MAC_BITTI":
                macBittiOlayiniIsle(mac);
                break;

            default:
                logger.warn("Yonetici Gozlemci: Bilinmeyen olay tipi: {}", olayTipi);
        }
    }

    private void macEklendiOlayiniIsle(Mac mac) {
        logger.info("YONETICI BILDIRIMI: ID={} Email={} -> Yeni mac onay bekliyor: Mac ID={}",
                yoneticiId, yoneticiEmail, mac.getId());

        // Veritabanina bildirim kaydi olustur
        bildirimOlustur(
                "MAC_EKLENDI",
                "Yeni Mac Onay Bekliyor",
                String.format("%s vs %s mac eklendi ve onayinizi bekliyor.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
                mac,
                mac.getEditor());
    }

    private void macOnaylandiOlayiniIsle(Mac mac) {
        logger.info("YONETICI BILDIRIMI: ID={} Email={} -> Mac onaylandi: Mac ID={}",
                yoneticiId, yoneticiEmail, mac.getId());

        bildirimOlustur(
                "MAC_ONAYLANDI",
                "Mac Onaylandi",
                String.format("%s vs %s mac basariyla onaylandi ve yayina alindi.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
                mac,
                null);
    }

    private void macReddedildiOlayiniIsle(Mac mac) {
        logger.info("YONETICI BILDIRIMI: ID={} Email={} -> Mac reddedildi: Mac ID={}",
                yoneticiId, yoneticiEmail, mac.getId());

        bildirimOlustur(
                "MAC_REDDEDILDI",
                "Mac Reddedildi",
                String.format("%s vs %s mac reddedildi.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
                mac,
                null);
    }

    private void macBasladiOlayiniIsle(Mac mac) {
        logger.info("YONETICI BILDIRIMI: ID={} -> Mac basladi: Mac ID={}",
                yoneticiId, mac.getId());
    }

    private void macBittiOlayiniIsle(Mac mac) {
        logger.info("YONETICI BILDIRIMI: ID={} -> Mac bitti: Mac ID={}",
                yoneticiId, mac.getId());
    }

    private void bildirimOlustur(String bildirimTipi, String baslik, String icerik,
            Mac mac, Kullanici gonderici) {
        // Repository kontrol (null ise veritabanina kaydetme)
        if (bildirimRepository == null || kullaniciRepository == null) {
            logger.warn("Repository'ler henuz enjekte edilmedi, bildirim kaydedilemedi.");
            return;
        }

        try {
            // Yonetici kullaniciyi veritabanindan cek
            Kullanici yonetici = kullaniciRepository.findById(yoneticiId).orElse(null);
            if (yonetici == null) {
                logger.error("Yonetici bulunamadi: ID={}", yoneticiId);
                return;
            }

            // Bildirim entity'si olustur
            Bildirim bildirim = new Bildirim();
            bildirim.setAliciKullanici(yonetici);
            bildirim.setGondericiKullanici(gonderici);
            bildirim.setBildirimTipi(bildirimTipi);
            bildirim.setBaslik(baslik);
            bildirim.setIcerik(icerik);
            bildirim.setMac(mac);
            bildirim.setOkundu(false);

            // Hedef URL olustur (mac detay sayfasi)
            if (mac != null && mac.getId() != null) {
                bildirim.setHedefUrl("/app/matches/" + mac.getId());
            }

            // Veritabanina kaydet
            bildirimRepository.save(bildirim);

            logger.info("Bildirim veritabanina kaydedildi: Alici ID={}, Tip={}",
                    yoneticiId, bildirimTipi);

        } catch (Exception e) {
            logger.error("Bildirim olusturulurken hata: {}", e.getMessage(), e);
        }
    }

    // ==================== GETTER METODLARI ====================

    public Long getYoneticiId() {
        return yoneticiId;
    }

    public String getYoneticiEmail() {
        return yoneticiEmail;
    }

    // ==================== EQUALS VE HASHCODE ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        YoneticiGozlemci that = (YoneticiGozlemci) obj;
        return yoneticiId != null && yoneticiId.equals(that.yoneticiId);
    }

    @Override
    public int hashCode() {
        return yoneticiId != null ? yoneticiId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "YoneticiGozlemci{" +
                "yoneticiId=" + yoneticiId +
                ", yoneticiEmail='" + yoneticiEmail + '\'' +
                '}';
    }
}
