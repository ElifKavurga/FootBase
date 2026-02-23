package com.footbase.patterns.command.service;

import com.footbase.patterns.command.Command;
import com.footbase.patterns.command.CommandInvoker;
import com.footbase.patterns.command.MacSonlandirCommand;
import com.footbase.patterns.command.SkorGirisiCommand;
import com.footbase.patterns.command.dto.MacSonlandirDTO;
import com.footbase.patterns.command.dto.SkorGirisiDTO;
import com.footbase.repository.MacRepository;
import com.footbase.repository.MacTakimlariRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MacCommandService {

    private static final Logger logger = LoggerFactory.getLogger(MacCommandService.class);

    @Autowired
    private CommandInvoker commandInvoker;

    @Autowired
    private MacRepository macRepository;

    @Autowired
    private MacTakimlariRepository macTakimlariRepository;

    public Map<String, Object> skorGirisiYap(SkorGirisiDTO skorGirisiDTO, Long kullaniciId) {
        logger.info("Skor girişi başlatıldı: {}", skorGirisiDTO);

        Map<String, Object> sonuc = new HashMap<>();

        try {
            // Validasyon
            if (skorGirisiDTO.getMacId() == null) {
                throw new RuntimeException("Maç ID gerekli!");
            }
            if (skorGirisiDTO.getEvSahibiSkor() == null || skorGirisiDTO.getDeplasmanSkor() == null) {
                throw new RuntimeException("Skorlar gerekli!");
            }
            if (skorGirisiDTO.getEvSahibiSkor() < 0 || skorGirisiDTO.getDeplasmanSkor() < 0) {
                throw new RuntimeException("Skorlar negatif olamaz!");
            }

            SkorGirisiCommand command = new SkorGirisiCommand(
                    skorGirisiDTO,
                    kullaniciId,
                    macRepository,
                    macTakimlariRepository);

            boolean basarili = commandInvoker.executeCommand(command);

            if (basarili) {
                sonuc.put("basarili", true);
                sonuc.put("mesaj", "Skor girişi başarılı!");
                sonuc.put("macId", skorGirisiDTO.getMacId());
                sonuc.put("evSahibiSkor", skorGirisiDTO.getEvSahibiSkor());
                sonuc.put("deplasmanSkor", skorGirisiDTO.getDeplasmanSkor());
                sonuc.put("gecmisBoyutu", commandInvoker.getHistorySize());
                logger.info("Skor girişi başarılı!");
            } else {
                throw new RuntimeException("Skor girişi başarısız!");
            }

        } catch (Exception e) {
            logger.error("Skor girişi hatası: {}", e.getMessage());
            sonuc.put("basarili", false);
            sonuc.put("mesaj", "Hata: " + e.getMessage());
        }

        return sonuc;
    }

    public Map<String, Object> macSonlandir(MacSonlandirDTO macSonlandirDTO, Long kullaniciId) {
        logger.info("Maç sonlandırma başlatıldı: {}", macSonlandirDTO);

        Map<String, Object> sonuc = new HashMap<>();

        try {
            if (macSonlandirDTO.getMacId() == null) {
                throw new RuntimeException("Maç ID gerekli!");
            }
            if (macSonlandirDTO.getEvSahibiSkor() == null || macSonlandirDTO.getDeplasmanSkor() == null) {
                throw new RuntimeException("Skorlar gerekli!");
            }
            if (macSonlandirDTO.getEvSahibiSkor() < 0 || macSonlandirDTO.getDeplasmanSkor() < 0) {
                throw new RuntimeException("Skorlar negatif olamaz!");
            }

            if (macSonlandirDTO.getDurum() == null || macSonlandirDTO.getDurum().isEmpty()) {
                macSonlandirDTO.setDurum("BITTI");
            }

            MacSonlandirCommand command = new MacSonlandirCommand(
                    macSonlandirDTO,
                    kullaniciId,
                    macRepository,
                    macTakimlariRepository);

            boolean basarili = commandInvoker.executeCommand(command);

            if (basarili) {
                String sonucMetni = hesaplaSonuc(
                        macSonlandirDTO.getEvSahibiSkor(),
                        macSonlandirDTO.getDeplasmanSkor());

                sonuc.put("basarili", true);
                sonuc.put("mesaj", "Maç başarıyla sonlandırıldı!");
                sonuc.put("macId", macSonlandirDTO.getMacId());
                sonuc.put("evSahibiSkor", macSonlandirDTO.getEvSahibiSkor());
                sonuc.put("deplasmanSkor", macSonlandirDTO.getDeplasmanSkor());
                sonuc.put("durum", macSonlandirDTO.getDurum());
                sonuc.put("sonuc", sonucMetni);
                sonuc.put("gecmisBoyutu", commandInvoker.getHistorySize());
                logger.info("✅ Maç sonlandırma başarılı! Sonuç: {}", sonucMetni);
            } else {
                throw new RuntimeException("Maç sonlandırma başarısız!");
            }

        } catch (Exception e) {
            logger.error("Maç sonlandırma hatası: {}", e.getMessage());
            sonuc.put("basarili", false);
            sonuc.put("mesaj", "Hata: " + e.getMessage());
        }

        return sonuc;
    }

    public Map<String, Object> sonIslemGeriAl(Long kullaniciId) {
        logger.info("Son işlem geri alınıyor... (Kullanıcı: {})", kullaniciId);

        Map<String, Object> sonuc = new HashMap<>();

        try {
            Command lastCommand = commandInvoker.getLastCommand();
            if (lastCommand == null) {
                throw new RuntimeException("Geri alınacak işlem bulunamadı!");
            }

            if (!lastCommand.getKullaniciId().equals(kullaniciId)) {
                throw new RuntimeException(
                        String.format("Bu işlem size ait değil! (İşlem sahibi: %d)",
                                lastCommand.getKullaniciId()));
            }

            boolean basarili = commandInvoker.undoByKullaniciId(kullaniciId);

            if (basarili) {
                sonuc.put("basarili", true);
                sonuc.put("mesaj", "İşlem başarıyla geri alındı!");
                sonuc.put("geriAlinanIslem", lastCommand.getDescription());
                sonuc.put("islemTipi", lastCommand.getCommandType());
                logger.info("✅ İşlem geri alındı: {}", lastCommand.getDescription());
            } else {
                throw new RuntimeException("İşlem geri alınamadı!");
            }

        } catch (Exception e) {
            logger.error("Geri alma hatası: {}", e.getMessage());
            sonuc.put("basarili", false);
            sonuc.put("mesaj", "Hata: " + e.getMessage());
        }

        return sonuc;
    }

    public Map<String, Object> islemGecmisiGetir() {
        Map<String, Object> sonuc = new HashMap<>();

        List<Map<String, Object>> gecmis = commandInvoker.getCommandHistory()
                .getHistory()
                .stream()
                .map(cmd -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tip", cmd.getCommandType());
                    item.put("aciklama", cmd.getDescription());
                    item.put("kullaniciId", cmd.getKullaniciId());
                    item.put("zaman", cmd.getExecutionTime().toString());
                    return item;
                })
                .collect(Collectors.toList());

        sonuc.put("gecmis", gecmis);
        sonuc.put("toplamIslem", gecmis.size());
        sonuc.put("redoMevcutMu", !commandInvoker.getCommandHistory().isRedoEmpty());

        logger.info("📚 İşlem geçmişi getirildi: {} işlem", gecmis.size());

        return sonuc;
    }

    private String hesaplaSonuc(Integer evSahibiSkor, Integer deplasmanSkor) {
        if (evSahibiSkor > deplasmanSkor) {
            return "EV_SAHIBI_GALIP";
        } else if (deplasmanSkor > evSahibiSkor) {
            return "DEPLASMAN_GALIP";
        } else {
            return "BERABERLIK";
        }
    }

    public void logIstatistikler() {
        commandInvoker.printHistory();
    }
}
