package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import com.footbase.repository.MacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MacGuncellemeTemplate extends MacIslemSablonu {

    @Autowired(required = false)
    private MacRepository macRepository;

    private Mac eskiMac; // Degisiklik karsilastirmasi icin
    private Mac guncelMac; // Guncel degerler

    @Override
    protected void maciIsle(Mac mac) {
        this.guncelMac = mac;
        logger.info("Mac guncelleniyor...");

        // Degisiklikleri logla
        if (eskiMac != null) {
            if (!eskiMac.getTarih().equals(mac.getTarih())) {
                logger.info("Tarih degisti: {} -> {}", eskiMac.getTarih(), mac.getTarih());
            }
            if (!eskiMac.getSaat().equals(mac.getSaat())) {
                logger.info("Saat degisti: {} -> {}", eskiMac.getSaat(), mac.getSaat());
            }
        }

        logger.info("Mac bilgileri guncellendi");
    }

    @Override
    protected String islemTipi() {
        return "MAC_GUNCELLEME";
    }

    @Override
    protected boolean onKontrollerYap(Mac mac) {
        if (!super.onKontrollerYap(mac)) {
            return false;
        }

        logger.debug("Guncelleme icin ek kontroller...");

        // Mac ID'si olmali (mevcut mac)
        if (mac.getId() == null) {
            logger.error("Guncellenecek macin ID'si olmali!");
            return false;
        }

        // Eski maci al (karsilastirma icin)
        if (macRepository != null) {
            try {
                eskiMac = macRepository.findById(mac.getId()).orElse(null);
                if (eskiMac == null) {
                    logger.error("Mac bulunamadi!");
                    return false;
                }
            } catch (Exception e) {
                logger.warn("Eski mac bilgisi alinamadi: {}", e.getMessage());
            }
        }

        logger.debug("Guncelleme kontrolleri basarili");
        return true;
    }

    @Override
    protected boolean verileriDogrula(Mac mac) {
        if (!super.verileriDogrula(mac)) {
            return false;
        }

        logger.debug("Guncelleme icin veri dogrulama...");

        // Eger mac yayinda ise bazi degisiklikler yapilamaz
        if ("YAYINDA".equals(mac.getOnayDurumu())) {
            logger.info("Mac YAYINDA - bazi kisitlamalar var");
            // Ornek: Tarih degisikligi yapilamaz
            if (eskiMac != null && !eskiMac.getTarih().equals(mac.getTarih())) {
                logger.warn("Yayindaki macin tarihi degistirilemez!");
                mac.setTarih(eskiMac.getTarih()); // Eski tarihe geri al
            }
        }

        logger.debug("Guncelleme dogrulamasi basarili");
        return true;
    }

    @Override
    protected void kaydet(Mac mac) {
        logger.info("Mac guncellemeleri kaydediliyor...");

        if (macRepository != null) {
            try {
                macRepository.save(mac);
                logger.info("Mac basariyla guncellendi - ID: {}", mac.getId());
            } catch (Exception e) {
                logger.error("Mac guncelleme hatasi: {}", e.getMessage());
                throw new RuntimeException("Mac guncellenemedi", e);
            }
        } else {
            logger.warn("MacRepository bulunamadi - test modu");
        }
    }

    @Override
    protected boolean bildirimGonder() {
        // Sadece onemli degisikliklerde bildirim gonder
        if (eskiMac != null && guncelMac != null) {
            return !eskiMac.getTarih().equals(guncelMac.getTarih())
                    || !eskiMac.getSaat().equals(guncelMac.getSaat());
        }
        return false;
    }

    @Override
    protected void bildirimGonderImpl(Mac mac) {
        logger.info("Mac guncelleme bildirimi gonderiliyor...");
        logger.info("Mac bilgileri degistirildi - ID: {}", mac.getId());
    }

    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("Mac guncelleme islemi tamamlandi");
        logger.info("Guncel Durum: {}", mac.getDurum());
    }

    public void setEskiMac(Mac eskiMac) {
        this.eskiMac = eskiMac;
    }
}
