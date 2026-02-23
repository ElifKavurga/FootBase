package com.footbase.patterns.facade;

import com.footbase.dto.MacDetayDTO;
import com.footbase.entity.Mac;
import com.footbase.entity.MacDurumGecmisi;
import com.footbase.entity.MacMedya;
import com.footbase.entity.MacOyuncuOlaylari;
import com.footbase.entity.MacTakimlari;
import com.footbase.repository.MacDurumGecmisiRepository;
import com.footbase.repository.MacMedyaRepository;
import com.footbase.repository.MacOyuncuOlaylariRepository;
import com.footbase.repository.MacTakimlariRepository;
import com.footbase.service.MacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MacIstatistikFacade {

    private static final Logger logger = LoggerFactory.getLogger(MacIstatistikFacade.class);

    @Autowired
    private MacService macService;

    @Autowired
    private MacTakimlariRepository macTakimlariRepository;

    @Autowired
    private MacOyuncuOlaylariRepository macOyuncuOlaylariRepository;

    @Autowired
    private MacMedyaRepository macMedyaRepository;

    @Autowired
    private MacDurumGecmisiRepository macDurumGecmisiRepository;

    public MacIstatistikFacade() {
        logger.info("MacIstatistikFacade oluşturuldu (Facade Pattern)");
    }

    public MacDetayDTO macDetaylariniGetir(Long macId) {
        logger.debug("Maç detayları getiriliyor: macId={}", macId);

        // 1. Maç bilgilerini al
        Mac mac = macService.macGetir(macId);

        // 2. Takım bilgilerini al
        List<MacTakimlari> takimlar = macTakimlariRepository.findByMacIdWithDetails(macId);
        logger.debug("Takımlar bulundu: {} adet", takimlar.size());

        // 3. Maç olaylarını al (goller, kartlar)
        List<MacOyuncuOlaylari> olaylar = macOyuncuOlaylariRepository.findByMacIdWithDetails(macId);
        logger.debug("Olaylar bulundu: {} adet", olaylar.size());

        // 4. Maç medyasını al
        List<MacMedya> medya = macMedyaRepository.findByMacIdWithDetails(macId);
        logger.debug("Medya bulundu: {} adet", medya.size());

        // 5. Maç durum geçmişini al
        List<MacDurumGecmisi> durumGecmisi = macDurumGecmisiRepository.findByMacIdWithDetails(macId);
        logger.debug("Durum geçmişi bulundu: {} adet", durumGecmisi.size());

        // Tüm verileri tek bir DTO'da topla ve döndür
        MacDetayDTO detay = new MacDetayDTO(mac, takimlar, olaylar, medya, durumGecmisi);
        logger.info("✅ Maç detayları başarıyla toplandı: macId={}", macId);

        return detay;
    }

    public List<MacTakimlari> macTakimlariniGetir(Long macId) {
        return macTakimlariRepository.findByMacIdWithDetails(macId);
    }

    public List<MacOyuncuOlaylari> macOlaylariniGetir(Long macId) {
        return macOyuncuOlaylariRepository.findByMacIdWithDetails(macId);
    }

    public List<MacMedya> macMedyasiniGetir(Long macId) {
        return macMedyaRepository.findByMacIdWithDetails(macId);
    }

    public List<MacDurumGecmisi> macDurumGecmisiniGetir(Long macId) {
        return macDurumGecmisiRepository.findByMacIdWithDetails(macId);
    }
}
