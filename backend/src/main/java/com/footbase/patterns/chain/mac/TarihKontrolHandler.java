package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class TarihKontrolHandler extends MacOnayHandler {

    private static final int MAX_GELECEK_GUN = 365;

    public TarihKontrolHandler() {
        this.priority = 1;
        logger.info("TarihKontrolHandler oluşturuldu");
    }

    @Override
    protected HandlerResult doHandle(Mac mac) {
        LocalDate tarih = mac.getTarih();
        LocalTime saat = mac.getSaat();

        if (tarih == null) {
            return HandlerResult.failure("Maç tarihi zorunludur", getHandlerName());
        }

        if (saat == null) {
            return HandlerResult.failure("Maç saati zorunludur", getHandlerName());
        }

        LocalDate bugun = LocalDate.now();

        if (mac.getId() == null && tarih.isBefore(bugun)) {
            logMacAction(mac, "HATA: Geçmiş tarih - " + tarih);
            return HandlerResult.failure("Maç tarihi geçmişte olamaz", getHandlerName());
        }

        if (tarih.isAfter(bugun.plusDays(MAX_GELECEK_GUN))) {
            logMacAction(mac, "HATA: Çok uzak gelecek - " + tarih);
            return HandlerResult.failure(
                    String.format("Maç tarihi en fazla %d gün sonrası olabilir", MAX_GELECEK_GUN),
                    getHandlerName());
        }

        if (tarih.isEqual(bugun)) {
            LocalTime simdi = LocalTime.now();
            if (saat.isBefore(simdi.minusHours(1))) {
                logMacAction(mac, "UYARI: Bugün için geçmiş saat - " + saat);
            }
        }

        logMacAction(mac, String.format("Tarih kontrolü BAŞARILI (%s %s)", tarih, saat));
        return HandlerResult.success();
    }
}
