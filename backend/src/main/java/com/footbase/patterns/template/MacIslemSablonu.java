package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MacIslemSablonu {

    protected static final Logger logger = LoggerFactory.getLogger(MacIslemSablonu.class);

    public final boolean macIsle(Mac mac) {
        logger.info("Mac isleme baslatiliyor... [{}]", this.getClass().getSimpleName());

        try {
            // 1. On kontroller
            if (!onKontrollerYap(mac)) {
                logger.error("On kontroller basarisiz!");
                return false;
            }

            // 2. Verileri dogrula
            if (!verileriDogrula(mac)) {
                logger.error("Veri dogrulama basarisiz!");
                return false;
            }

            // 3. Mac isle (alt sinif implementasyonu)
            logger.info("Mac isleniyor...");
            maciIsle(mac);

            // 4. Kaydet
            logger.info("Mac kaydediliyor...");
            kaydet(mac);

            // 5. Bildirim gonder (opsiyonel - hook method)
            if (bildirimGonder()) {
                logger.info("Bildirimler gonderiliyor...");
                bildirimGonderImpl(mac);
            }

            // 6. Son islemler (opsiyonel - hook method)
            sonIslemler(mac);

            logger.info("Mac isleme tamamlandi!");
            return true;

        } catch (Exception e) {
            logger.error("Mac isleme hatasi: {}", e.getMessage(), e);
            hataYonet(mac, e);
            return false;
        }
    }

    // Alt siniflar MUTLAKA implement etmeli

    protected abstract void maciIsle(Mac mac);

    protected abstract String islemTipi();

    // Varsayilan implementasyon var, override edilebilir

    protected boolean onKontrollerYap(Mac mac) {
        logger.debug("On kontroller yapiliyor...");

        if (mac == null) {
            logger.error("Mac null olamaz!");
            return false;
        }

        if (mac.getTarih() == null) {
            logger.error("Mac tarihi zorunludur!");
            return false;
        }

        if (mac.getSaat() == null) {
            logger.error("Mac saati zorunludur!");
            return false;
        }

        logger.debug("On kontroller basarili");
        return true;
    }

    protected boolean verileriDogrula(Mac mac) {
        logger.debug("Veri dogrulama yapiliyor...");

        // Tarih gecmis olmamali (olusturma icin)
        if (mac.getId() == null && mac.getTarih().isBefore(java.time.LocalDate.now())) {
            logger.warn("Mac tarihi gecmiste!");
        }

        logger.debug("Veri dogrulama basarili");
        return true;
    }

    protected void kaydet(Mac mac) {
        logger.info("Mac kaydediliyor: {}", islemTipi());
        // Varsayilan implementasyon - alt siniflar override edebilir
    }

    protected void hataYonet(Mac mac, Exception e) {
        logger.error("Hata yonetimi: {} - {}", islemTipi(), e.getMessage());
        // Varsayilan hata yonetimi
    }

    // Opsiyonel - Alt siniflar isterse override eder

    protected boolean bildirimGonder() {
        return false; // Varsayilan: bildirim gonderilmez
    }

    protected void bildirimGonderImpl(Mac mac) {
        logger.info("Varsayilan bildirim gonderimi");
    }

    protected void sonIslemler(Mac mac) {
        logger.debug("Islem tamamlandi: {}", islemTipi());
    }

    protected String logMesaji() {
        return String.format("Mac Isleme: %s", islemTipi());
    }
}
