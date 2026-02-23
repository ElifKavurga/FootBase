package com.footbase.patterns.chain.yorum;

import com.footbase.entity.Yorum;
import com.footbase.patterns.chain.HandlerResult;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkKontrolHandler extends YorumHandler {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "((https?|ftp)://|(www\\.))[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);

    private static final int MAX_LINK_SAYISI = 2;
    private boolean linkIzinVer = true;

    public LinkKontrolHandler() {
        this.priority = 4;
        logger.info("LinkKontrolHandler oluşturuldu");
    }

    @Override
    protected HandlerResult doHandle(Yorum yorum) {
        String mesaj = yorum.getMesaj();

        if (mesaj == null) {
            return HandlerResult.success();
        }

        Matcher matcher = URL_PATTERN.matcher(mesaj);
        int linkSayisi = 0;

        while (matcher.find()) {
            linkSayisi++;
        }

        if (linkSayisi > 0) {
            logYorumAction(yorum, String.format("Link tespit edildi: %d adet", linkSayisi));

            if (!linkIzinVer) {
                logYorumAction(yorum, "ENGELLENDI: Link'e izin yok");
                return HandlerResult.failure(
                        "Yorumlarda link paylaşımı yasaktır",
                        getHandlerName());
            }

            if (linkSayisi > MAX_LINK_SAYISI) {
                logYorumAction(yorum, String.format("ENGELLENDI: Çok fazla link (%d > %d)",
                        linkSayisi, MAX_LINK_SAYISI));
                return HandlerResult.failure(
                        String.format("En fazla %d link paylaşabilirsiniz", MAX_LINK_SAYISI),
                        getHandlerName());
            }

            logYorumAction(yorum, "Link kontrolü BAŞARILI");
        }

        return HandlerResult.success();
    }

    public void setLinkIzinVer(boolean izinVer) {
        this.linkIzinVer = izinVer;
        logger.info("Link izin durumu değiştirildi: {}", izinVer ? "İZİN VERİLDİ" : "ENGELLENDİ");
    }

    public String removeLinkler(String mesaj) {
        return URL_PATTERN.matcher(mesaj).replaceAll("[Link Kaldırıldı]");
    }
}
