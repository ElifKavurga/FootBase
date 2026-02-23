package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpamKontrolHandler extends YorumHandler {

    private static final Map<Long, LocalDateTime> SON_YORUM_ZAMANLARI = new HashMap<>();
    private static final int MINIMUM_YORUM_ARALIGI_SANIYE = 10;
    private static final int MAX_TEKRAR_KARAKTER = 5;

    public SpamKontrolHandler() {
        this.priority = 2;
        logger.info("SpamKontrolHandler oluşturuldu");
    }

    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();
        Long kullaniciId = yorum.getKullanici() != null ? yorum.getKullanici().getId() : null;

        if (kullaniciId != null) {
            LocalDateTime sonYorum = SON_YORUM_ZAMANLARI.get(kullaniciId);
            if (sonYorum != null) {
                long saniyeFarki = java.time.Duration.between(sonYorum, LocalDateTime.now()).getSeconds();

                if (saniyeFarki < MINIMUM_YORUM_ARALIGI_SANIYE) {
                    logYorumAction(yorum, "SPAM TESPİT EDİLDİ: Çok hızlı yorum");
                    return HandlerResult.failure(
                            String.format("Lütfen %d saniye bekleyiniz", MINIMUM_YORUM_ARALIGI_SANIYE - saniyeFarki),
                            getHandlerName());
                }
            }

            SON_YORUM_ZAMANLARI.put(kullaniciId, LocalDateTime.now());
        }

        if (cokTekrarEdenKarakterVar(mesaj)) {
            logYorumAction(yorum, "SPAM TESPİT EDİLDİ: Çok fazla tekrar eden karakter");
            return HandlerResult.failure(
                    "Yorumunuzda çok fazla tekrar eden karakter var",
                    getHandlerName());
        }

        if (tumunuBuyukHarf(mesaj)) {
            logYorumAction(yorum, "UYARI: Tamamen büyük harf");
        }

        logYorumAction(yorum, "Spam kontrolü BAŞARILI");
        return HandlerResult.success();
    }

    private boolean cokTekrarEdenKarakterVar(String mesaj) {
        if (mesaj == null || mesaj.length() < MAX_TEKRAR_KARAKTER) {
            return false;
        }

        int tekrarSayisi = 1;
        char oncekiKarakter = mesaj.charAt(0);

        for (int i = 1; i < mesaj.length(); i++) {
            char mevcutKarakter = mesaj.charAt(i);

            if (mevcutKarakter == oncekiKarakter) {
                tekrarSayisi++;
                if (tekrarSayisi > MAX_TEKRAR_KARAKTER) {
                    return true;
                }
            } else {
                tekrarSayisi = 1;
                oncekiKarakter = mevcutKarakter;
            }
        }

        return false;
    }

    private boolean tumunuBuyukHarf(String mesaj) {
        if (mesaj == null || mesaj.length() < 10) {
            return false;
        }

        long buyukHarfSayisi = mesaj.chars().filter(Character::isUpperCase).count();
        long harfSayisi = mesaj.chars().filter(Character::isLetter).count();

        return harfSayisi > 0 && (buyukHarfSayisi * 100.0 / harfSayisi) > 80;
    }

    public void clearSpamHistory(Long kullaniciId) {
        SON_YORUM_ZAMANLARI.remove(kullaniciId);
    }
}
