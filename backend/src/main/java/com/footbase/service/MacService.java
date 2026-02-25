package com.footbase.service;

import com.footbase.entity.EditorYoneticileri;
import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import com.footbase.entity.MacDurumGecmisi;
import com.footbase.entity.MacTakimlari;
import com.footbase.entity.MacOyuncuOlaylari;
import com.footbase.entity.Takim;
import com.footbase.patterns.observer.AdminObserver;
import com.footbase.patterns.observer.MacApprovalSubject;
import com.footbase.patterns.observer.MacOnayKonusu;
import com.footbase.patterns.observer.YoneticiGozlemci;
import com.footbase.patterns.observer.EditorGozlemci;
import com.footbase.repository.EditorYoneticileriRepository;
import com.footbase.repository.KullaniciRepository;
import com.footbase.repository.MacDurumGecmisiRepository;
import com.footbase.repository.MacRepository;
import com.footbase.repository.MacTakimlariRepository;
import com.footbase.repository.MacOyuncuOlaylariRepository;
import com.footbase.repository.TakimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MacService {

    @Autowired
    private MacRepository macRepository;

    @Autowired
    private TakimRepository takimRepository;

    @Autowired
    private EditorYoneticileriRepository editorYoneticileriRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private MacTakimlariRepository macTakimlariRepository;

    @Autowired
    private MacDurumGecmisiRepository macDurumGecmisiRepository;

    @Autowired
    private MacOyuncuOlaylariRepository macOyuncuOlaylariRepository;

    @Autowired
    private MacOnayKonusu macOnayKonusu;

    private void populateMacData(Mac mac) {
        if (mac == null || mac.getId() == null) {
            return;
        }

        try {
            List<MacTakimlari> macTakimlari = macTakimlariRepository.findByMacIdWithDetails(mac.getId());
            for (MacTakimlari mt : macTakimlari) {
                if (mt != null && mt.getTakim() != null) {
                    if (mt.getEvSahibi() != null && mt.getEvSahibi()) {
                        mac.setEvSahibiTakim(mt.getTakim());
                        mac.setEvSahibiSkor(mt.getSkor() != null ? mt.getSkor() : 0);
                        if (mt.getTakim().getStadyum() != null) {
                            mac.setStadyum(mt.getTakim().getStadyum());
                        }
                    } else {
                        mac.setDeplasmanTakim(mt.getTakim());
                        mac.setDeplasmanSkor(mt.getSkor() != null ? mt.getSkor() : 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLatestDurum(Long macId) {
        return macDurumGecmisiRepository.findLatestByMacId(macId)
                .map(MacDurumGecmisi::getDurum)
                .orElse(null);
    }

    private String inferDurumFromDate(Mac mac) {
        if (mac == null || mac.getTarih() == null) {
            return "PLANLI";
        }

        java.time.LocalDateTime macTarihi = mac.getSaat() != null
                ? java.time.LocalDateTime.of(mac.getTarih(), mac.getSaat())
                : mac.getTarih().atStartOfDay();

        return macTarihi.isAfter(java.time.LocalDateTime.now()) ? "PLANLI" : "BITTI";
    }

    private void setDurumWithFallback(Mac mac) {
        if (mac == null || mac.getId() == null) {
            return;
        }

        String latestDurum = getLatestDurum(mac.getId());
        if (latestDurum != null && !latestDurum.isBlank()) {
            mac.setDurum(latestDurum);
            mac.setOnayDurumu(latestDurum);
            return;
        }

        String inferredDurum = inferDurumFromDate(mac);
        mac.setDurum(inferredDurum);
        mac.setOnayDurumu(inferredDurum);
    }


    public void populateMacDataPublic(Mac mac) {
        populateMacData(mac);
    }

    public List<Mac> tumMaclariGetir() {
        try {
            final List<Long> activeMatchIds;
            try {
                activeMatchIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
            } catch (Exception e) {
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            List<Mac> tumMaclar = macRepository.findAll();
            List<Mac> maclar = (activeMatchIds == null || activeMatchIds.isEmpty())
                    ? tumMaclar
                    : tumMaclar.stream()
                            .filter(m -> m != null && activeMatchIds.contains(m.getId()))
                            .collect(Collectors.toList());

            maclar.forEach(m -> {
                try {
                    populateMacData(m);
                    setDurumWithFallback(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            java.time.LocalDateTime simdi = java.time.LocalDateTime.now();

            List<Mac> gelecekMaclar = maclar.stream()
                    .filter(m -> {
                        if (m.getTarih() == null) {
                            return false;
                        }
                        try {
                            java.time.LocalDateTime macTarihi = m.getSaat() != null
                                    ? java.time.LocalDateTime.of(m.getTarih(), m.getSaat())
                                    : m.getTarih().atStartOfDay();
                            return macTarihi.isAfter(simdi);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .sorted((m1, m2) -> {
                        try {
                            java.time.LocalDateTime tarih1 = m1.getSaat() != null
                                    ? java.time.LocalDateTime.of(m1.getTarih(), m1.getSaat())
                                    : m1.getTarih().atStartOfDay();
                            java.time.LocalDateTime tarih2 = m2.getSaat() != null
                                    ? java.time.LocalDateTime.of(m2.getTarih(), m2.getSaat())
                                    : m2.getTarih().atStartOfDay();
                            return tarih1.compareTo(tarih2);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            List<Mac> gecmisMaclar = maclar.stream()
                    .filter(m -> {
                        if (m.getTarih() == null) {
                            return true;
                        }
                        try {
                            java.time.LocalDateTime macTarihi = m.getSaat() != null
                                    ? java.time.LocalDateTime.of(m.getTarih(), m.getSaat())
                                    : m.getTarih().atStartOfDay();
                            return !macTarihi.isAfter(simdi);
                        } catch (Exception e) {
                            return true;
                        }
                    })
                    .sorted((m1, m2) -> {
                        try {
                            java.time.LocalDateTime tarih1 = m1.getSaat() != null && m1.getTarih() != null
                                    ? java.time.LocalDateTime.of(m1.getTarih(), m1.getSaat())
                                    : (m1.getTarih() != null ? m1.getTarih().atStartOfDay() : java.time.LocalDateTime.MIN);
                            java.time.LocalDateTime tarih2 = m2.getSaat() != null && m2.getTarih() != null
                                    ? java.time.LocalDateTime.of(m2.getTarih(), m2.getSaat())
                                    : (m2.getTarih() != null ? m2.getTarih().atStartOfDay() : java.time.LocalDateTime.MIN);
                            return tarih2.compareTo(tarih1);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            List<Mac> siraliMaclar = new java.util.ArrayList<>();
            siraliMaclar.addAll(gelecekMaclar);
            siraliMaclar.addAll(gecmisMaclar);
            return siraliMaclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macGetir(Long id) {
        try {
            Mac mac = macRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));

            populateMacData(mac);
            setDurumWithFallback(mac);

            return mac;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ getirilirken bir hata oluÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¦ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â¦ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¸tu: " + e.getMessage());
        }
    }


    public List<Mac> gelecekMaclariGetir() {
        try {
            java.time.LocalDate bugun = java.time.LocalDate.now();
            java.time.LocalTime simdi = java.time.LocalTime.now();

            final List<Long> activeMatchIds;
            try {
                activeMatchIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
            } catch (Exception e) {
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            List<Mac> tarihBazliMaclar = macRepository.findGelecekMaclar(bugun, simdi);
            List<Mac> gelecekMaclar = (activeMatchIds == null || activeMatchIds.isEmpty())
                    ? tarihBazliMaclar
                    : tarihBazliMaclar.stream()
                            .filter(m -> m != null && activeMatchIds.contains(m.getId()))
                            .collect(Collectors.toList());

            gelecekMaclar.forEach(mac -> {
                try {
                    populateMacData(mac);
                    setDurumWithFallback(mac);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return gelecekMaclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Mac> gecmisMaclariGetir() {
        try {
            java.time.LocalDate bugun = java.time.LocalDate.now();
            java.time.LocalTime simdi = java.time.LocalTime.now();

            final List<Long> activeMatchIds;
            try {
                activeMatchIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
            } catch (Exception e) {
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            List<Mac> tarihBazliMaclar = macRepository.findGecmisMaclar(bugun, simdi);
            List<Mac> gecmisMaclar = (activeMatchIds == null || activeMatchIds.isEmpty())
                    ? tarihBazliMaclar
                    : tarihBazliMaclar.stream()
                            .filter(m -> m != null && activeMatchIds.contains(m.getId()))
                            .collect(Collectors.toList());

            gecmisMaclar.forEach(mac -> {
                try {
                    populateMacData(mac);
                    setDurumWithFallback(mac);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return gecmisMaclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macOlustur(Mac mac) {
        Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                .orElseThrow(() -> new RuntimeException("Ev sahibi tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
        Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                .orElseThrow(() -> new RuntimeException("Deplasman tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));

        mac.setEvSahibiTakim(evSahibi);
        mac.setDeplasmanTakim(deplasman);

        return macRepository.save(mac);
    }

    public Mac macGuncelle(Long id, Mac mac) {
        Mac mevcutMac = macGetir(id);

        if (mac.getEvSahibiTakim() != null) {
            Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Ev sahibi tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
            mevcutMac.setEvSahibiTakim(evSahibi);
        }

        if (mac.getDeplasmanTakim() != null) {
            Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Deplasman tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
            mevcutMac.setDeplasmanTakim(deplasman);
        }

        if (mac.getEvSahibiSkor() != null) {
            mevcutMac.setEvSahibiSkor(mac.getEvSahibiSkor());
        }
        if (mac.getDeplasmanSkor() != null) {
            mevcutMac.setDeplasmanSkor(mac.getDeplasmanSkor());
        }
        if (mac.getTarih() != null) {
            mevcutMac.setTarih(mac.getTarih());
        }
        if (mac.getSaat() != null) {
            mevcutMac.setSaat(mac.getSaat());
        }

        return macRepository.save(mevcutMac);
    }

    public void macSil(Long id) {
        Mac mac = macGetir(id);
        macRepository.delete(mac);
    }

    public Mac editorMacOlustur(Mac mac, Long editorId) {
        Kullanici editor = kullaniciRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("EditÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¶r bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));

        if (!"EDITOR".equals(editor.getRol())) {
            throw new RuntimeException("Bu kullan???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½c???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½ editÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¶r de???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½il");
        }

        EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorIdWithDetails(editorId);
        if (editorYoneticileri == null) {
            throw new RuntimeException("EditÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¶rÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¼n yÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¶neticisi bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½");
        }

        mac.setEditor(editor);
        mac.setEditorId(editorId);

        if (mac.getEvSahibiTakim() != null && mac.getDeplasmanTakim() != null) {
            Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Ev sahibi tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
            Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Deplasman tak???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½m bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));

            Mac kaydedilenMac = macRepository.save(mac);

            MacTakimlari evSahibiMacTakim = new MacTakimlari();
            evSahibiMacTakim.setMac(kaydedilenMac);
            evSahibiMacTakim.setTakim(evSahibi);
            evSahibiMacTakim.setEvSahibi(true);
            evSahibiMacTakim.setSkor(mac.getEvSahibiSkor() != null ? mac.getEvSahibiSkor() : 0);
            macTakimlariRepository.save(evSahibiMacTakim);

            MacTakimlari deplasmanMacTakim = new MacTakimlari();
            deplasmanMacTakim.setMac(kaydedilenMac);
            deplasmanMacTakim.setTakim(deplasman);
            deplasmanMacTakim.setEvSahibi(false);
            deplasmanMacTakim.setSkor(mac.getDeplasmanSkor() != null ? mac.getDeplasmanSkor() : 0);
            macTakimlariRepository.save(deplasmanMacTakim);

            // mac_durum_gecmisi'ne "ONAY_BEKLIYOR" durumu ile kaydet
            java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
            try {
                macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                        kaydedilenMac.getId(),
                        "ONAY_BEKLIYOR",
                        simdi,
                        editorId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ durumu kaydedilemedi: " + e.getMessage());
            }

            YoneticiGozlemci yoneticiGozlemci = new YoneticiGozlemci(editorYoneticileri.getAdmin());
            macOnayKonusu.ekle(yoneticiGozlemci);
            macOnayKonusu.macEklendi(kaydedilenMac);

            Mac yuklenenMac = macRepository.findById(kaydedilenMac.getId())
                    .orElse(kaydedilenMac);
            populateMacData(yuklenenMac);
            yuklenenMac.setOnayDurumu("ONAY_BEKLIYOR");
            yuklenenMac.setEditor(editor);
            yuklenenMac.setEditorId(editorId);

            return yuklenenMac;
        }

        Mac kaydedilenMac = macRepository.save(mac);

        // mac_durum_gecmisi'ne "ONAY_BEKLIYOR" durumu ile kaydet
        java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
        try {
            macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                    kaydedilenMac.getId(),
                    "ONAY_BEKLIYOR",
                    simdi,
                    editorId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ durumu kaydedilemedi: " + e.getMessage());
        }

        MacApprovalSubject subject = new MacApprovalSubject();
        AdminObserver adminObserver = new AdminObserver(editorYoneticileri.getAdmin());
        subject.attach(adminObserver);
        subject.macEklendi(kaydedilenMac);

        Mac yuklenenMac = macRepository.findById(kaydedilenMac.getId())
                .orElse(kaydedilenMac);
        populateMacData(yuklenenMac);
        yuklenenMac.setOnayDurumu("ONAY_BEKLIYOR");
        yuklenenMac.setEditor(editor);
        yuklenenMac.setEditorId(editorId);

        return yuklenenMac;
    }

    public List<Mac> onayBekleyenMaclariGetir() {
        List<Mac> tumMaclar = macRepository.findAll();
        return tumMaclar.stream()
                .filter(m -> "ONAY_BEKLIYOR".equals(m.getOnayDurumu()))
                .collect(Collectors.toList());
    }

    public List<Mac> onaylanmisMaclariGetir() {
        List<Mac> tumMaclar = macRepository.findAll();
        return tumMaclar.stream()
                .filter(m -> "YAYINDA".equals(m.getOnayDurumu()))
                .collect(Collectors.toList());
    }

    public List<Mac> adminOnayBekleyenMaclariGetir(Long adminId) {
        try {

            List<EditorYoneticileri> editorYoneticileri = editorYoneticileriRepository.findByAdminId(adminId);
            List<Long> editorIds = editorYoneticileri.stream()
                    .map(EditorYoneticileri::getEditorId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());


            if (editorIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            List<Long> tumOnayBekleyenMacIds;
            try {
                tumOnayBekleyenMacIds = macDurumGecmisiRepository.findAllPendingMacIds();
            } catch (Exception e) {
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            if (tumOnayBekleyenMacIds == null || tumOnayBekleyenMacIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

                                    // Her macin ilk kaydini kontrol et - editor tarafindan olusturulan mi?
            List<Long> admininEditorlerininMaclari = new java.util.ArrayList<>();
            for (Long macId : tumOnayBekleyenMacIds) {
                try {
                    java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository
                            .findFirstRecordEditorIdByMacId(macId);

                    if (ilkKayitEditorId.isPresent()) {
                        Long editorId = ilkKayitEditorId.get();
                        if (editorId != null && editorIds.contains(editorId)) {
                            admininEditorlerininMaclari.add(macId);
                        } else {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (admininEditorlerininMaclari.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            // Bu maclari getir
            List<Mac> maclar = macRepository.findAll().stream()
                    .filter(m -> m != null && admininEditorlerininMaclari.contains(m.getId()))
                    .collect(Collectors.toList());


            maclar.forEach(m -> {
                try {
                    populateMacData(m);
                    String durum = getLatestDurum(m.getId());
                    m.setOnayDurumu(durum != null ? durum : "ONAY_BEKLIYOR");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return maclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Mac> editorMaclariniGetir(Long editorId) {
        try {

            List<Mac> tumMaclar = macRepository.findAll();
            List<Mac> editorMaclari = new java.util.ArrayList<>();

            for (Mac mac : tumMaclar) {
                try {
                    java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository
                            .findFirstRecordEditorIdByMacId(mac.getId());
                    if (ilkKayitEditorId.isPresent() && ilkKayitEditorId.get().equals(editorId)) {
                        // En son durumu kontrol et
                        String latestDurum = getLatestDurum(mac.getId());

                        // Sadece ONAY_BEKLIYOR, REDDEDILDI ve YAYINDA durumundakileri ekle
                        if ("ONAY_BEKLIYOR".equals(latestDurum) ||
                                "REDDEDILDI".equals(latestDurum) ||
                                "YAYINDA".equals(latestDurum)) {
                            populateMacData(mac);
                            mac.setDurum(latestDurum);
                            mac.setOnayDurumu(latestDurum);
                            editorMaclari.add(mac);
                        } else {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return editorMaclari;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macOnayla(Long macId, Long adminId) {
        Mac mac = macGetir(macId);

        // En son durumu kontrol et
        String latestDurum = getLatestDurum(macId);
        if (!"ONAY_BEKLIYOR".equals(latestDurum)) {
            throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ onay bekliyor durumunda de???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½il");
        }

        List<MacDurumGecmisi> durumGecmisiList = macDurumGecmisiRepository.findByMacId(macId);
        if (!durumGecmisiList.isEmpty()) {
            MacDurumGecmisi ilkKayit = durumGecmisiList.stream()
                    .min((a, b) -> a.getIslemTarihi().compareTo(b.getIslemTarihi()))
                    .orElse(null);

            if (ilkKayit != null && ilkKayit.getIslemYapanKullanici() != null) {
                Long editorId = ilkKayit.getIslemYapanKullanici().getId();
                EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorId(editorId);
                if (editorYoneticileri == null || !editorYoneticileri.getAdminId().equals(adminId)) {
                    throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½ onaylama yetkiniz yok");
                }
            }
        }

        Kullanici admin = kullaniciRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
        java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
        try {
            macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                    macId,
                    "YAYINDA",
                    simdi,
                    adminId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ onaylanamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½: " + e.getMessage());
        }

        java.util.Optional<Long> editorIdOpt = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
        if (editorIdOpt.isPresent()) {
            Long editorId = editorIdOpt.get();
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor != null) {
                EditorGozlemci editorGozlemci = new EditorGozlemci(editor);
                macOnayKonusu.ekle(editorGozlemci);
                macOnayKonusu.macOnaylandi(mac);
            }
        }

        populateMacData(mac);
        mac.setOnayDurumu("YAYINDA");

        return mac;
    }

    public Mac macReddet(Long macId, Long adminId) {
        Mac mac = macGetir(macId);

        // En son durumu kontrol et
        String latestDurum = getLatestDurum(macId);
        if (!"ONAY_BEKLIYOR".equals(latestDurum)) {
            throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ onay bekliyor durumunda de???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½il");
        }

        List<MacDurumGecmisi> durumGecmisiList = macDurumGecmisiRepository.findByMacId(macId);
        if (!durumGecmisiList.isEmpty()) {
            MacDurumGecmisi ilkKayit = durumGecmisiList.stream()
                    .min((a, b) -> a.getIslemTarihi().compareTo(b.getIslemTarihi()))
                    .orElse(null);

            if (ilkKayit != null && ilkKayit.getIslemYapanKullanici() != null) {
                Long editorId = ilkKayit.getIslemYapanKullanici().getId();
                EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorId(editorId);
                if (editorYoneticileri == null || !editorYoneticileri.getAdminId().equals(adminId)) {
                    throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½ reddetme yetkiniz yok");
                }
            }
        }

        Kullanici admin = kullaniciRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½"));
        java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
        try {
            macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                    macId,
                    "REDDEDILDI",
                    simdi,
                    adminId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ reddedilemedi: " + e.getMessage());
        }

        java.util.Optional<Long> editorIdOptRed = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
        if (editorIdOptRed.isPresent()) {
            Long editorId = editorIdOptRed.get();
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor != null) {
                EditorGozlemci editorGozlemci = new EditorGozlemci(editor);
                macOnayKonusu.ekle(editorGozlemci);
                macOnayKonusu.macReddedildi(mac);
            }
        }

        populateMacData(mac);
        mac.setOnayDurumu("REDDEDILDI");

        return mac;
    }

    public List<Mac> yayindakiMaclariGetir() {
        List<Long> activeMatchIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");

        if (activeMatchIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Mac> maclar = macRepository.findAll().stream()
                .filter(m -> activeMatchIds.contains(m.getId()))
                .collect(Collectors.toList());

        maclar.forEach(this::populateMacData);

        return maclar;
    }

    public Mac macSkorGuncelle(Long macId, Integer evSahibiSkor, Integer deplasmanSkor, Long editorId) {

        try {
            Mac mac = macGetir(macId);
            if (mac == null) {
                throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ bulunamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½: ID=" + macId);
            }

            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½n skorunu gÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¼ncelleme yetkiniz yok");
            }

            List<MacTakimlari> macTakimlari = macTakimlariRepository.findByMacId(macId);

            for (MacTakimlari mt : macTakimlari) {
                if (mt.getEvSahibi() != null && mt.getEvSahibi()) {
                    mt.setSkor(evSahibiSkor);
                } else {
                    mt.setSkor(deplasmanSkor);
                }
                macTakimlariRepository.save(mt);
            }

            macOnayKonusu.golAtildi(mac);

            populateMacData(mac);
            return mac;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Skor gÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¼ncellenemedi: " + e.getMessage());
        }
    }

    public MacOyuncuOlaylari macOlayEkle(MacOyuncuOlaylari olay, Long editorId) {

        try {
            Long macId = olay.getMac().getId();

            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§a olay ekleme yetkiniz yok");
            }

            MacOyuncuOlaylari kaydedilenOlay = macOyuncuOlaylariRepository.save(olay);

            Mac mac = macGetir(macId);
            if ("GOL".equalsIgnoreCase(olay.getOlayTipi())) {
                macOnayKonusu.golAtildi(mac);
            }

            return kaydedilenOlay;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Olay eklenemedi: " + e.getMessage());
        }
    }

    public Mac macBaslat(Long macId, Long editorId) {

        try {
            Mac mac = macGetir(macId);

            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½ baÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¦ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â¦ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¸latma yetkiniz yok");
            }

            macOnayKonusu.macBasladi(mac);

            return mac;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ baÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¦ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â¦ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¸lat???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½lamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½: " + e.getMessage());
        }
    }

    public Mac macSonuclandir(Long macId, Long editorId) {

        try {
            Mac mac = macGetir(macId);

            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½ sonuÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§land???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½rma yetkiniz yok");
            }

            populateMacData(mac);
            macOnayKonusu.macBitti(mac);

            return mac;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MaÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ sonuÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€Â¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§land???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½r???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½lamad???ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¯ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¿ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â½: " + e.getMessage());
        }
    }
}







