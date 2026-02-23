package com.footbase.patterns.template;

import com.footbase.entity.Mac;
import com.footbase.repository.MacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MacOlusturmaTemplate extends MacIslemSablonu {

    @Autowired(required = false)
    private MacRepository macRepository;

    @Override
    protected void maciIsle(Mac mac) {
        logger.info("Yeni mac olusturuluyor...");

        // Mac durumunu ayarla
        if (mac.getDurum() == null) {
            mac.setDurum("Planlandi");
        }

        // Onay durumunu ayarla
        if (mac.getOnayDurumu() == null) {
            mac.setOnayDurumu("ONAY_BEKLIYOR");
        }

        logger.info("Mac olusturuldu - Onay bekliyor");
    }

    @Override
    protected String islemTipi() {
        return "MAC_OLUSTURMA";
    }

    @Override
    protected boolean verileriDogrula(Mac mac) {
        // Once parent'in dogrulamasini yap
        if (!super.verileriDogrula(mac)) {
            return false;
        }

        logger.debug("Mac olusturma icin ek dogrulamalar...");

        // Mac ID'si olmamali (yeni mac)
        if (mac.getId() != null) {
            logger.error("Yeni mac icin ID olmamali!");
            return false;
        }

        // Takim kontrolu (macTakimlari uzerinden)
        if (mac.getMacTakimlari() == null || mac.getMacTakimlari().size() < 2) {
            logger.error("Mac icin en az 2 takim gerekli!");
            return false;
        }

        logger.debug("Mac olusturma dogrulamasi basarili");
        return true;
    }

    @Override
    protected void kaydet(Mac mac) {
        logger.info("Yeni mac veritabanina kaydediliyor...");

        if (macRepository != null) {
            try {
                macRepository.save(mac);
                logger.info("Mac basariyla kaydedildi - ID: {}", mac.getId());
            } catch (Exception e) {
                logger.error("Mac kaydetme hatasi: {}", e.getMessage());
                throw new RuntimeException("Mac kaydedilemedi", e);
            }
        } else {
            logger.warn("MacRepository bulunamadi - test modu");
        }
    }

    @Override
    protected boolean bildirimGonder() {
        return true; // Yeni mac olusturuldugunda bildirim gonder
    }

    @Override
    protected void bildirimGonderImpl(Mac mac) {
        logger.info("Admin'e yeni mac bildirimi gonderiliyor...");
        logger.info("Konu: Yeni Mac Onay Bekliyor");
        logger.info("Tarih: {} {}", mac.getTarih(), mac.getSaat());
        // Burada gercek bildirim servisi cagrilabilir
    }

    @Override
    protected void sonIslemler(Mac mac) {
        logger.info("Mac olusturma islemi tamamlandi");
        logger.info("Durum: {}, Onay: {}", mac.getDurum(), mac.getOnayDurumu());
    }
}
