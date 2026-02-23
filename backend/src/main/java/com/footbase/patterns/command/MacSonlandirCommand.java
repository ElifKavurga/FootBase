package com.footbase.patterns.command;

import com.footbase.entity.Mac;
import com.footbase.entity.MacTakimlari;
import com.footbase.patterns.command.dto.MacSonlandirDTO;
import com.footbase.repository.MacRepository;
import com.footbase.repository.MacTakimlariRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacSonlandirCommand extends MacCommand {

    private final MacRepository macRepository;
    private final MacTakimlariRepository macTakimlariRepository;
    private final MacSonlandirDTO macSonlandirDTO;

    // Undo için önceki verileri sakla
    private Map<Long, Integer> oncekiSkorlar = new HashMap<>();
    private String oncekiDurum;

    public MacSonlandirCommand(
            MacSonlandirDTO macSonlandirDTO,
            Long kullaniciId,
            MacRepository macRepository,
            MacTakimlariRepository macTakimlariRepository) {
        super(kullaniciId);
        this.macSonlandirDTO = macSonlandirDTO;
        this.macRepository = macRepository;
        this.macTakimlariRepository = macTakimlariRepository;
    }

    @Override
    protected boolean doExecute() {
        try {
            // Maçı kontrol et
            Mac mac = macRepository.findById(macSonlandirDTO.getMacId())
                    .orElseThrow(() -> new RuntimeException("Maç bulunamadı: " + macSonlandirDTO.getMacId()));

            // Önceki durumu kaydet
            oncekiDurum = mac.getDurum();

            // Maç takımlarını getir
            List<MacTakimlari> macTakimlari = macTakimlariRepository.findByMacId(macSonlandirDTO.getMacId());

            if (macTakimlari.size() != 2) {
                throw new RuntimeException("Maçta tam 2 takım olmalı!");
            }

            // Önceki skorları kaydet (undo için)
            for (MacTakimlari mt : macTakimlari) {
                oncekiSkorlar.put(mt.getId(), mt.getSkor());
            }

            // Skorları güncelle
            for (MacTakimlari mt : macTakimlari) {
                if (mt.getEvSahibi()) {
                    mt.setSkor(macSonlandirDTO.getEvSahibiSkor());
                    logger.info("Ev sahibi final skor: {} → {}",
                            oncekiSkorlar.get(mt.getId()),
                            macSonlandirDTO.getEvSahibiSkor());
                } else {
                    mt.setSkor(macSonlandirDTO.getDeplasmanSkor());
                    logger.info("Deplasman final skor: {} → {}",
                            oncekiSkorlar.get(mt.getId()),
                            macSonlandirDTO.getDeplasmanSkor());
                }
                macTakimlariRepository.save(mt);
            }

            // Maç durumunu güncelle
            mac.setDurum(macSonlandirDTO.getDurum() != null ? macSonlandirDTO.getDurum() : "BITTI");
            macRepository.save(mac);

            logger.info("Maç sonlandırıldı: {} - {} (Durum: {} → {})",
                    macSonlandirDTO.getEvSahibiSkor(),
                    macSonlandirDTO.getDeplasmanSkor(),
                    oncekiDurum,
                    mac.getDurum());

            // Sonucu hesapla ve logla
            String sonuc = hesaplaSonuc(macSonlandirDTO.getEvSahibiSkor(), macSonlandirDTO.getDeplasmanSkor());
            logger.info("Maç sonucu: {}", sonuc);

            return true;

        } catch (Exception e) {
            logger.error("Maç sonlandırma hatası: {}", e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean doUndo() {
        try {
            // Maçı bul
            Mac mac = macRepository.findById(macSonlandirDTO.getMacId())
                    .orElseThrow(() -> new RuntimeException("Maç bulunamadı: " + macSonlandirDTO.getMacId()));

            // Önceki durumu geri yükle
            mac.setDurum(oncekiDurum);
            macRepository.save(mac);

            // Önceki skorları geri yükle
            for (Map.Entry<Long, Integer> entry : oncekiSkorlar.entrySet()) {
                Long macTakimlariId = entry.getKey();
                if (macTakimlariId == null) {
                    continue;
                }
                MacTakimlari mt = macTakimlariRepository.findById(macTakimlariId)
                        .orElseThrow(() -> new RuntimeException("MacTakimlari bulunamadı: " + macTakimlariId));

                Integer oncekiSkor = entry.getValue();
                mt.setSkor(oncekiSkor);
                macTakimlariRepository.save(mt);

                logger.info("Skor geri alındı: {} (ID: {})", oncekiSkor, mt.getId());
            }

            logger.info("Maç sonlandırma başarıyla geri alındı! Durum: {} → {}",
                    macSonlandirDTO.getDurum(), oncekiDurum);
            return true;

        } catch (Exception e) {
            logger.error("Maç sonlandırma geri alma hatası: {}", e.getMessage());
            return false;
        }
    }

    private String hesaplaSonuc(Integer evSahibiSkor, Integer deplasmanSkor) {
        if (evSahibiSkor > deplasmanSkor) {
            return "EV SAHİBİ GALİP";
        } else if (deplasmanSkor > evSahibiSkor) {
            return "DEPLASMAN GALİP";
        } else {
            return "BERABERLİK";
        }
    }

    @Override
    public String getDescription() {
        return String.format("Maç Sonlandırma: Maç #%d - %d:%d (%s) - (Kullanıcı: %d)",
                macSonlandirDTO.getMacId(),
                macSonlandirDTO.getEvSahibiSkor(),
                macSonlandirDTO.getDeplasmanSkor(),
                macSonlandirDTO.getDurum(),
                kullaniciId);
    }

    @Override
    public String getCommandType() {
        return "MAC_SONLANDIR";
    }
}
