package com.footbase.patterns.observer;

import com.footbase.entity.Bildirim;
import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.repository.BildirimRepository;
import com.footbase.repository.KullaniciRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorGozlemci implements Gozlemci {

    private static final Logger logger = LoggerFactory.getLogger(EditorGozlemci.class);

    private final Long editorId;

    private final String editorEmail;

    private BildirimRepository bildirimRepository;

    private KullaniciRepository kullaniciRepository;

    public EditorGozlemci(Kullanici editor) {
        this.editorId = editor.getId();
        this.editorEmail = editor.getEmail();
    }

    public EditorGozlemci(Long editorId, String editorEmail) {
        this.editorId = editorId;
        this.editorEmail = editorEmail;
    }

    public void setRepositories(BildirimRepository bildirimRepository,
            KullaniciRepository kullaniciRepository) {
        this.bildirimRepository = bildirimRepository;
        this.kullaniciRepository = kullaniciRepository;
    }

    @Override
    public void guncelle(String olayTipi, Object veri) {
        if (veri instanceof Mac) {
            Mac mac = (Mac) veri;
            macOlayiniIsle(olayTipi, mac);
        } else {
            logger.warn("Editor Gozlemci: Bilinmeyen veri tipi: {}",
                    veri != null ? veri.getClass().getName() : "null");
        }
    }

    private void macOlayiniIsle(String olayTipi, Mac mac) {
        switch (olayTipi) {
            case "MAC_ONAYLANDI":
                macOnaylandiOlayiniIsle(mac);
                break;

            case "MAC_REDDEDILDI":
                macReddedildiOlayiniIsle(mac);
                break;

            case "MAC_GUNCELLENDI":
                macGuncellendiOlayiniIsle(mac);
                break;

            default:
                logger.debug("Editor Gozlemci: Ilgilenilmeyen olay tipi: {}", olayTipi);
        }
    }

    private void macOnaylandiOlayiniIsle(Mac mac) {
        logger.info("EDITOR BILDIRIMI: ID={} Email={} -> Maciniz onaylandi: Mac ID={}",
                editorId, editorEmail, mac.getId());

        bildirimOlustur(
                "MAC_ONAYLANDI",
                "Maciniz Onaylandi",
                String.format("Eklediginiz '%s vs %s' mac yonetici tarafindan onaylandi ve yayina alindi.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
                mac,
                null // Sistem bildirimi, gonderici yok
        );
    }

    private void macReddedildiOlayiniIsle(Mac mac) {
        logger.info("EDITOR BILDIRIMI: ID={} Email={} -> Maciniz reddedildi: Mac ID={}",
                editorId, editorEmail, mac.getId());

        bildirimOlustur(
                "MAC_REDDEDILDI",
                "Maciniz Reddedildi",
                String.format(
                        "Eklediginiz '%s vs %s' mac yonetici tarafindan reddedildi. Lutfen mac bilgilerini kontrol edip tekrar ekleyin.",
                        mac.getEvSahibiTakim() != null ? mac.getEvSahibiTakim().getAd() : "Bilinmeyen",
                        mac.getDeplasmanTakim() != null ? mac.getDeplasmanTakim().getAd() : "Bilinmeyen"),
                mac,
                null);
    }

    private void macGuncellendiOlayiniIsle(Mac mac) {
        logger.info("EDITOR BILDIRIMI: ID={} -> Mac guncellendi: Mac ID={}",
                editorId, mac.getId());
    }

    private void bildirimOlustur(String bildirimTipi, String baslik, String icerik,
            Mac mac, Kullanici gonderici) {
        if (bildirimRepository == null || kullaniciRepository == null) {
            logger.warn("Repository'ler henuz enjekte edilmedi, bildirim kaydedilemedi.");
            return;
        }

        try {
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor == null) {
                logger.error("Editor bulunamadi: ID={}", editorId);
                return;
            }

            Bildirim bildirim = new Bildirim();
            bildirim.setAliciKullanici(editor);
            bildirim.setGondericiKullanici(gonderici);
            bildirim.setBildirimTipi(bildirimTipi);
            bildirim.setBaslik(baslik);
            bildirim.setIcerik(icerik);
            bildirim.setMac(mac);
            bildirim.setOkundu(false);

            if (mac != null && mac.getId() != null) {
                bildirim.setHedefUrl("/app/matches/" + mac.getId());
            }

            bildirimRepository.save(bildirim);

            logger.info("Bildirim veritabanina kaydedildi: Alici ID={}, Tip={}",
                    editorId, bildirimTipi);

        } catch (Exception e) {
            logger.error("Bildirim olusturulurken hata: {}", e.getMessage(), e);
        }
    }

    public Long getEditorId() {
        return editorId;
    }

    public String getEditorEmail() {
        return editorEmail;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        EditorGozlemci that = (EditorGozlemci) obj;
        return editorId != null && editorId.equals(that.editorId);
    }

    @Override
    public int hashCode() {
        return editorId != null ? editorId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EditorGozlemci{" +
                "editorId=" + editorId +
                ", editorEmail='" + editorEmail + '\'' +
                '}';
    }
}
