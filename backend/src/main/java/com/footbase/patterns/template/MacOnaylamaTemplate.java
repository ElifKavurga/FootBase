package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MacOnaylamaTemplate extends MacIslemSablonu {

    @Autowired(required = false)

    private boolean onaylandiMi = true; // true: onayla, false: reddet

    @Override
    protected void maciIsle(Mac mac) {
        logger.info("Mac onaylaniyor...");

        if (onaylandiMi) {
            mac.setOnayDurumu("YAYINDA");
            logger.info("Mac YAYINDA durumuna getirildi");
        } else {
            mac.setOnayDurumu("REDDEDILDI");
            logger.warn("Mac REDDEDILDI");
        }
    }

    @Override
    protected String islemTipi() {
        return onaylandiMi ? "MAC_ONAYLAMA" : "MAC_REDDETME";
    }

    @Override
    protected boolean onKontrollerYap(Mac mac) {
        if (!super.onKontrollerYap(mac)) {
            return false;
        }

        logger.debug("Onaylama icin ek kontroller...");

        // Mac ID'si olmali (mevcut mac)
        if (mac.getId() == null) {
            logger.error("Onaylanacak macin ID'si olmali!");
            return false;
        }

        // Onay durumu kontrolu
        if (!"ONAY_BEKLIYOR".equals(mac.getOnayDurumu())) {
            logger.error("Sadece ONAY_BEKLIYOR durumundaki maclar onaylanabilir!");
            return false;
        }

        logger.debug("Onaylama kontrolleri basarili");
        return true;
    }

    @Override
    protected void kaydet(Mac mac) {
        logger.info("Onay durumu kaydediliyor...");
        // Burada MacService uzerinden kayit yapilabilir
    }

    @Override
    protected boolean bildirimGonder() {
        return true; // Onaylama/Red durumunda bildirim gonder
    }

    @Override
    protected void bildirimGonderImpl(Mac mac) {
        if (onaylandiMi) {
            logger.info("Editor'e mac onaylandi bildirimi gonderiliyor...");
            logger.info("Mac yayina alindi - ID: {}", mac.getId());
        } else {
            logger.info("Editor'e mac reddedildi bildirimi gonderiliyor...");
            logger.warn("Mac reddedildi - ID: {}", mac.getId());
        }
    }

    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("Durum gecmisine kaydediliyor...");
        // MacDurumGecmisi kaydi yapilabilir

        if (onaylandiMi) {
            logger.info("Mac basariyla yayina alindi!");
        } else {
            logger.info("Mac reddedildi");
        }
    }

    public void setOnayla(boolean onayla) {
        this.onaylandiMi = onayla;
    }
}
