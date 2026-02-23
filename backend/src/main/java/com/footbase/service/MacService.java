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
                        // Stadyum bilgisini ev sahibi takımdan al
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

    public void populateMacDataPublic(Mac mac) {
        populateMacData(mac);
    }

    public List<Mac> tumMaclariGetir() {
        try {
            System.out.println("========== TÜM MAÇLAR GETİRİLİYOR ==========");
            // Sadece en son durumu "YAYINDA" olan maçları getir
            List<Long> yayindakiMacIds;
            try {
                yayindakiMacIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
                System.out.println("Yayında maç ID sayısı: " + yayindakiMacIds.size());
            } catch (Exception e) {
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            if (yayindakiMacIds == null || yayindakiMacIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            List<Mac> maclar = macRepository.findAll().stream()
                    .filter(m -> m != null && yayindakiMacIds.contains(m.getId()))
                    .collect(Collectors.toList());

            // Her maç için takım bilgilerini doldur
            maclar.forEach(m -> {
                try {
                    populateMacData(m);
                    String durum = getLatestDurum(m.getId());
                    if (durum != null) {
                        m.setDurum(durum); // durum alanını set et
                        m.setOnayDurumu(durum); // onayDurumu alanını da set et (geriye dönük uyumluluk için)
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Maçları sırala: Önce gelecek maçlar (yakından uzağa), sonra geçmiş maçlar
            // (yakından uzağa)
            java.time.LocalDateTime simdi = java.time.LocalDateTime.now();

            List<Mac> gelecekMaclar = maclar.stream()
                    .filter(m -> {
                        if (m.getTarih() == null)
                            return false;
                        try {
                            java.time.LocalDateTime macTarihi;
                            if (m.getSaat() != null) {
                                macTarihi = java.time.LocalDateTime.of(m.getTarih(), m.getSaat());
                            } else {
                                macTarihi = m.getTarih().atStartOfDay();
                            }
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
                            return tarih1.compareTo(tarih2); // Artan sırada (yakından uzağa)
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            List<Mac> gecmisMaclar = maclar.stream()
                    .filter(m -> {
                        if (m.getTarih() == null)
                            return true; // Tarihi olmayanlar geçmiş sayılsın
                        try {
                            java.time.LocalDateTime macTarihi;
                            if (m.getSaat() != null) {
                                macTarihi = java.time.LocalDateTime.of(m.getTarih(), m.getSaat());
                            } else {
                                macTarihi = m.getTarih().atStartOfDay();
                            }
                            return !macTarihi.isAfter(simdi);
                        } catch (Exception e) {
                            return true;
                        }
                    })
                    .sorted((m1, m2) -> {
                        try {
                            java.time.LocalDateTime tarih1 = m1.getSaat() != null && m1.getTarih() != null
                                    ? java.time.LocalDateTime.of(m1.getTarih(), m1.getSaat())
                                    : (m1.getTarih() != null ? m1.getTarih().atStartOfDay()
                                            : java.time.LocalDateTime.MIN);
                            java.time.LocalDateTime tarih2 = m2.getSaat() != null && m2.getTarih() != null
                                    ? java.time.LocalDateTime.of(m2.getTarih(), m2.getSaat())
                                    : (m2.getTarih() != null ? m2.getTarih().atStartOfDay()
                                            : java.time.LocalDateTime.MIN);
                            return tarih2.compareTo(tarih1); // Azalan sırada (yakından uzağa)
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            // Önce gelecek, sonra geçmiş
            List<Mac> siraliMaclar = new java.util.ArrayList<>();
            siraliMaclar.addAll(gelecekMaclar);
            siraliMaclar.addAll(gecmisMaclar);

            System.out.println("Gelecek maç sayısı: " + gelecekMaclar.size());
            System.out.println("Geçmiş maç sayısı: " + gecmisMaclar.size());
            System.out.println("Toplam maç sayısı: " + siraliMaclar.size());
            System.out.println("========== TÜM MAÇLAR GETİRME TAMAMLANDI ==========");

            return siraliMaclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macGetir(Long id) {
        try {
            Mac mac = macRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Maç bulunamadı"));

            // Takım bilgilerini doldur
            populateMacData(mac);

            // En son durumu set et
            String latestDurum = getLatestDurum(id);
            if (latestDurum != null) {
                mac.setOnayDurumu(latestDurum);
            }

            return mac;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Maç getirilirken bir hata oluştu: " + e.getMessage());
        }
    }

    // Durum kolonu veritabanında yok, bu metod kaldırıldı

    public List<Mac> gelecekMaclariGetir() {
        try {
            java.time.LocalDate bugun = java.time.LocalDate.now();
            java.time.LocalTime simdi = java.time.LocalTime.now();

            // Sadece en son durumu "YAYINDA" olan maçları getir
            List<Long> yayindakiMacIds;
            try {
                yayindakiMacIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
            } catch (Exception e) {
                e.printStackTrace();
                // Sorgu hatası durumunda boş liste döndür
                return java.util.Collections.emptyList();
            }

            if (yayindakiMacIds == null || yayindakiMacIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            List<Mac> gelecekMaclar = macRepository.findGelecekMaclar(bugun, simdi).stream()
                    .filter(m -> m != null && yayindakiMacIds.contains(m.getId()))
                    .collect(Collectors.toList());

            // Her maç için takım bilgilerini doldur
            gelecekMaclar.forEach(mac -> {
                try {
                    populateMacData(mac);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Hata durumunda devam et
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

            // Sadece en son durumu "YAYINDA" olan maçları getir
            List<Long> yayindakiMacIds;
            try {
                yayindakiMacIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");
            } catch (Exception e) {
                e.printStackTrace();
                // Sorgu hatası durumunda boş liste döndür
                return java.util.Collections.emptyList();
            }

            if (yayindakiMacIds == null || yayindakiMacIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            List<Mac> gecmisMaclar = macRepository.findGecmisMaclar(bugun, simdi).stream()
                    .filter(m -> m != null && yayindakiMacIds.contains(m.getId()))
                    .collect(Collectors.toList());

            // Her maç için takım bilgilerini doldur
            gecmisMaclar.forEach(mac -> {
                try {
                    populateMacData(mac);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Hata durumunda devam et
                }
            });

            return gecmisMaclar;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macOlustur(Mac mac) {
        // Takımları kontrol et
        Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));
        Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                .orElseThrow(() -> new RuntimeException("Deplasman takım bulunamadı"));

        mac.setEvSahibiTakim(evSahibi);
        mac.setDeplasmanTakim(deplasman);

        return macRepository.save(mac);
    }

    public Mac macGuncelle(Long id, Mac mac) {
        Mac mevcutMac = macGetir(id);

        // Takımları güncelle
        if (mac.getEvSahibiTakim() != null) {
            Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));
            mevcutMac.setEvSahibiTakim(evSahibi);
        }

        if (mac.getDeplasmanTakim() != null) {
            Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Deplasman takım bulunamadı"));
            mevcutMac.setDeplasmanTakim(deplasman);
        }

        // Diğer alanları güncelle
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
        // Editörü kontrol et
        Kullanici editor = kullaniciRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("Editör bulunamadı"));

        if (!"EDITOR".equals(editor.getRol())) {
            throw new RuntimeException("Bu kullanıcı editör değil");
        }

        // Editörün admin'ini bul
        EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorIdWithDetails(editorId);
        if (editorYoneticileri == null) {
            throw new RuntimeException("Editörün yöneticisi bulunamadı");
        }

        // Maçı oluştur (maclar tablosuna kaydet)
        mac.setEditor(editor);
        mac.setEditorId(editorId);

        // Takımları ayarla
        if (mac.getEvSahibiTakim() != null && mac.getDeplasmanTakim() != null) {
            Takim evSahibi = takimRepository.findById(mac.getEvSahibiTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Ev sahibi takım bulunamadı"));
            Takim deplasman = takimRepository.findById(mac.getDeplasmanTakim().getId())
                    .orElseThrow(() -> new RuntimeException("Deplasman takım bulunamadı"));

            Mac kaydedilenMac = macRepository.save(mac);

            // MacTakimlari kayıtlarını oluştur
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
                System.out.println("Maç durumu kaydedildi: Mac ID=" + kaydedilenMac.getId()
                        + ", Durum=ONAY_BEKLIYOR, Editor ID=" + editorId);
            } catch (Exception e) {
                System.err.println("Maç durumu kaydedilemedi: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Maç durumu kaydedilemedi: " + e.getMessage());
            }

            // Observer pattern ile admin'e bildirim gönder (Yeni Türkçe versiyon)
            YoneticiGozlemci yoneticiGozlemci = new YoneticiGozlemci(editorYoneticileri.getAdmin());
            macOnayKonusu.ekle(yoneticiGozlemci);
            macOnayKonusu.macEklendi(kaydedilenMac);

            // Mac'i yeniden yükle ve takım bilgilerini doldur
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
            System.out.println("Maç durumu kaydedildi: Mac ID=" + kaydedilenMac.getId()
                    + ", Durum=ONAY_BEKLIYOR, Editor ID=" + editorId);
        } catch (Exception e) {
            System.err.println("Maç durumu kaydedilemedi: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Maç durumu kaydedilemedi: " + e.getMessage());
        }

        // Observer pattern ile admin'e bildirim gönder
        MacApprovalSubject subject = new MacApprovalSubject();
        AdminObserver adminObserver = new AdminObserver(editorYoneticileri.getAdmin());
        subject.attach(adminObserver);
        subject.macEklendi(kaydedilenMac);

        // Mac'i yeniden yükle ve takım bilgilerini doldur
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
            System.out.println("\n========== ADMIN ONAY BEKLEYEN MAÇLAR ==========");
            System.out.println("Admin ID: " + adminId);

            // Admin'in editörlerini bul
            List<EditorYoneticileri> editorYoneticileri = editorYoneticileriRepository.findByAdminId(adminId);
            List<Long> editorIds = editorYoneticileri.stream()
                    .map(EditorYoneticileri::getEditorId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());

            System.out.println("Admin'in editör sayısı: " + editorIds.size());
            System.out.println("Editör ID'leri: " + editorIds);

            if (editorIds.isEmpty()) {
                System.out.println("Admin'in editörü yok!");
                return java.util.Collections.emptyList();
            }

            // Tüm onay bekleyen maçları getir
            List<Long> tumOnayBekleyenMacIds;
            try {
                tumOnayBekleyenMacIds = macDurumGecmisiRepository.findAllPendingMacIds();
                System.out.println("Tüm onay bekleyen maç sayısı: "
                        + (tumOnayBekleyenMacIds != null ? tumOnayBekleyenMacIds.size() : 0));
                System.out.println("Onay bekleyen maç ID'leri: " + tumOnayBekleyenMacIds);
            } catch (Exception e) {
                System.err.println("Sorgu hatası: " + e.getMessage());
                e.printStackTrace();
                return java.util.Collections.emptyList();
            }

            if (tumOnayBekleyenMacIds == null || tumOnayBekleyenMacIds.isEmpty()) {
                System.out.println("Hiç onay bekleyen maç yok!");
                return java.util.Collections.emptyList();
            }

            // Her maçın ilk kaydını kontrol et - editör tarafından oluşturulan mı?
            List<Long> admininEditörlerininMaclari = new java.util.ArrayList<>();
            for (Long macId : tumOnayBekleyenMacIds) {
                try {
                    java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository
                            .findFirstRecordEditorIdByMacId(macId);
                    System.out.println("  Maç ID " + macId + " -> İlk kayıt editör ID: "
                            + (ilkKayitEditorId.isPresent() ? ilkKayitEditorId.get() : "YOK"));

                    if (ilkKayitEditorId.isPresent()) {
                        Long editorId = ilkKayitEditorId.get();
                        if (editorId != null && editorIds.contains(editorId)) {
                            admininEditörlerininMaclari.add(macId);
                            System.out.println("Bu maç admin'in editörüne ait!");
                        } else {
                            System.out.println("Bu maç başka bir editöre ait (Editor ID: " + editorId + ")");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Hata: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Admin'in editörlerinin eklediği maç sayısı: " + admininEditörlerininMaclari.size());
            System.out.println("Admin'in editörlerinin eklediği maç ID'leri: " + admininEditörlerininMaclari);

            if (admininEditörlerininMaclari.isEmpty()) {
                System.out.println("Admin'in editörlerinin eklediği onay bekleyen maç yok!");
                return java.util.Collections.emptyList();
            }

            // Bu maçları getir
            List<Mac> maclar = macRepository.findAll().stream()
                    .filter(m -> m != null && admininEditörlerininMaclari.contains(m.getId()))
                    .collect(Collectors.toList());

            System.out.println("Veritabanından getirilen maç sayısı: " + maclar.size());

            // Her maç için takım bilgilerini doldur ve onay durumunu set et
            maclar.forEach(m -> {
                try {
                    populateMacData(m);
                    String durum = getLatestDurum(m.getId());
                    m.setOnayDurumu(durum != null ? durum : "ONAY_BEKLIYOR");
                    System.out.println("  ✓ Maç hazırlandı: ID=" + m.getId() + ", Durum=" + m.getOnayDurumu());
                } catch (Exception e) {
                    System.err.println("  ✗ Maç hazırlanamadı: ID=" + m.getId() + ", Hata=" + e.getMessage());
                    e.printStackTrace();
                }
            });

            System.out.println("========== TOPLAM DÖNEN MAÇ SAYISI: " + maclar.size() + " ==========\n");
            return maclar;
        } catch (Exception e) {
            System.err.println("GENEL HATA: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public List<Mac> editorMaclariniGetir(Long editorId) {
        try {
            System.out.println("\n========== EDITÖR MAÇLARI ==========");
            System.out.println("Editör ID: " + editorId);

            // Editörün tüm maçlarını getir (mac_durum_gecmisi'nden ilk kayıt editör olan)
            List<Mac> tumMaclar = macRepository.findAll();
            List<Mac> editorMaclari = new java.util.ArrayList<>();

            for (Mac mac : tumMaclar) {
                try {
                    // Bu maçın ilk kaydı editör tarafından mı oluşturuldu?
                    java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository
                            .findFirstRecordEditorIdByMacId(mac.getId());
                    if (ilkKayitEditorId.isPresent() && ilkKayitEditorId.get().equals(editorId)) {
                        // En son durumu kontrol et
                        String latestDurum = getLatestDurum(mac.getId());
                        System.out.println("  Maç ID " + mac.getId() + " -> Durum: " + latestDurum);

                        // Sadece ONAY_BEKLIYOR, REDDEDILDI ve YAYINDA durumundakileri ekle
                        if ("ONAY_BEKLIYOR".equals(latestDurum) ||
                                "REDDEDILDI".equals(latestDurum) ||
                                "YAYINDA".equals(latestDurum)) {
                            populateMacData(mac);
                            mac.setDurum(latestDurum); // durum alanını set et
                            mac.setOnayDurumu(latestDurum); // onayDurumu alanını da set et
                            editorMaclari.add(mac);
                            System.out.println("Maç eklendi: " + latestDurum);
                        } else {
                            System.out.println("Maç durumu uygun değil: " + latestDurum);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Maç kontrol hatası: ID=" + mac.getId() + ", Hata=" + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("========== TOPLAM EDITÖR MAÇI: " + editorMaclari.size() + " ==========\n");
            return editorMaclari;
        } catch (Exception e) {
            System.err.println("✗ GENEL HATA: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public Mac macOnayla(Long macId, Long adminId) {
        Mac mac = macGetir(macId);

        // En son durumu kontrol et
        String latestDurum = getLatestDurum(macId);
        if (!"ONAY_BEKLIYOR".equals(latestDurum)) {
            throw new RuntimeException("Bu maç onay bekliyor durumunda değil");
        }

        // Admin'in bu editörün yöneticisi olduğunu kontrol et
        // Önce editörü bulmak için mac_durum_gecmisi'nden ilk kaydı al
        List<MacDurumGecmisi> durumGecmisiList = macDurumGecmisiRepository.findByMacId(macId);
        if (!durumGecmisiList.isEmpty()) {
            MacDurumGecmisi ilkKayit = durumGecmisiList.stream()
                    .min((a, b) -> a.getIslemTarihi().compareTo(b.getIslemTarihi()))
                    .orElse(null);

            if (ilkKayit != null && ilkKayit.getIslemYapanKullanici() != null) {
                Long editorId = ilkKayit.getIslemYapanKullanici().getId();
                EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorId(editorId);
                if (editorYoneticileri == null || !editorYoneticileri.getAdminId().equals(adminId)) {
                    throw new RuntimeException("Bu maçı onaylama yetkiniz yok");
                }
            }
        }

        // mac_durum_gecmisi'ne "YAYINDA" durumu ile yeni kayıt ekle
        Kullanici admin = kullaniciRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin bulunamadı"));
        java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
        try {
            macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                    macId,
                    "YAYINDA",
                    simdi,
                    adminId);
            System.out.println("Maç onaylandı: Mac ID=" + macId + ", Admin ID=" + adminId);
        } catch (Exception e) {
            System.err.println("Maç onaylanamadı: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Maç onaylanamadı: " + e.getMessage());
        }

        // Observer pattern ile editöre bildirim gönder (Yeni Türkçe versiyon)
        // Maçı ekleyen editörü bul
        java.util.Optional<Long> editorIdOpt = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
        if (editorIdOpt.isPresent()) {
            Long editorId = editorIdOpt.get();
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor != null) {
                EditorGozlemci editorGozlemci = new EditorGozlemci(editor);
                macOnayKonusu.ekle(editorGozlemci);
                macOnayKonusu.macOnaylandi(mac);
                System.out.println("Editöre 'maç onaylandı' bildirimi gönderildi: Editor ID=" + editorId);
            }
        }

        // Takım bilgilerini doldur
        populateMacData(mac);
        mac.setOnayDurumu("YAYINDA");

        return mac;
    }

    public Mac macReddet(Long macId, Long adminId) {
        Mac mac = macGetir(macId);

        // En son durumu kontrol et
        String latestDurum = getLatestDurum(macId);
        if (!"ONAY_BEKLIYOR".equals(latestDurum)) {
            throw new RuntimeException("Bu maç onay bekliyor durumunda değil");
        }

        // Admin'in bu editörün yöneticisi olduğunu kontrol et
        List<MacDurumGecmisi> durumGecmisiList = macDurumGecmisiRepository.findByMacId(macId);
        if (!durumGecmisiList.isEmpty()) {
            MacDurumGecmisi ilkKayit = durumGecmisiList.stream()
                    .min((a, b) -> a.getIslemTarihi().compareTo(b.getIslemTarihi()))
                    .orElse(null);

            if (ilkKayit != null && ilkKayit.getIslemYapanKullanici() != null) {
                Long editorId = ilkKayit.getIslemYapanKullanici().getId();
                EditorYoneticileri editorYoneticileri = editorYoneticileriRepository.findByEditorId(editorId);
                if (editorYoneticileri == null || !editorYoneticileri.getAdminId().equals(adminId)) {
                    throw new RuntimeException("Bu maçı reddetme yetkiniz yok");
                }
            }
        }

        // mac_durum_gecmisi'ne "REDDEDILDI" durumu ile yeni kayıt ekle
        Kullanici admin = kullaniciRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin bulunamadı"));
        java.time.LocalDateTime simdi = java.time.LocalDateTime.now();
        try {
            macDurumGecmisiRepository.saveMacDurumGecmisiNative(
                    macId,
                    "REDDEDILDI",
                    simdi,
                    adminId);
            System.out.println("Maç reddedildi: Mac ID=" + macId + ", Admin ID=" + adminId);
        } catch (Exception e) {
            System.err.println("Maç reddedilemedi: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Maç reddedilemedi: " + e.getMessage());
        }

        // Observer pattern ile editöre bildirim gönder (Yeni Türkçe versiyon)
        // Maçı ekleyen editörü bul
        java.util.Optional<Long> editorIdOptRed = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
        if (editorIdOptRed.isPresent()) {
            Long editorId = editorIdOptRed.get();
            Kullanici editor = kullaniciRepository.findById(editorId).orElse(null);
            if (editor != null) {
                EditorGozlemci editorGozlemci = new EditorGozlemci(editor);
                macOnayKonusu.ekle(editorGozlemci);
                macOnayKonusu.macReddedildi(mac);
                System.out.println("Editöre 'maç reddedildi' bildirimi gönderildi: Editor ID=" + editorId);
            }
        }

        // Takım bilgilerini doldur
        populateMacData(mac);
        mac.setOnayDurumu("REDDEDILDI");

        return mac;
    }

    public List<Mac> yayindakiMaclariGetir() {
        // Sadece en son durumu "YAYINDA" olan maçları getir
        List<Long> yayindakiMacIds = macDurumGecmisiRepository.findMacIdsByLatestDurum("YAYINDA");

        if (yayindakiMacIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Mac> maclar = macRepository.findAll().stream()
                .filter(m -> yayindakiMacIds.contains(m.getId()))
                .collect(Collectors.toList());

        // Her maç için takım bilgilerini doldur
        maclar.forEach(this::populateMacData);

        return maclar;
    }

    public Mac macSkorGuncelle(Long macId, Integer evSahibiSkor, Integer deplasmanSkor, Long editorId) {
        System.out.println("\n========== MAÇ SKOR GÜNCELLEME ==========");
        System.out.println("Maç ID: " + macId);
        System.out.println("Yeni Skor: " + evSahibiSkor + " - " + deplasmanSkor);
        System.out.println("Editör ID: " + editorId);

        try {
            // Maçı kontrol et
            Mac mac = macGetir(macId);
            if (mac == null) {
                throw new RuntimeException("Maç bulunamadı: ID=" + macId);
            }

            // Bu maçın editörü olup olmadığını kontrol et
            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maçın skorunu güncelleme yetkiniz yok");
            }

            // MacTakimlari'nden skorları güncelle
            List<MacTakimlari> macTakimlari = macTakimlariRepository.findByMacId(macId);

            for (MacTakimlari mt : macTakimlari) {
                if (mt.getEvSahibi() != null && mt.getEvSahibi()) {
                    mt.setSkor(evSahibiSkor);
                    System.out.println("Ev sahibi skor güncellendi: " + evSahibiSkor);
                } else {
                    mt.setSkor(deplasmanSkor);
                    System.out.println("Deplasman skor güncellendi: " + deplasmanSkor);
                }
                macTakimlariRepository.save(mt);
            }

            // Observer pattern ile bildirim gönder
            macOnayKonusu.golAtildi(mac);
            System.out.println("Gol bildirimi gönderildi");

            // Güncellenmiş maçı döndür
            populateMacData(mac);
            System.out.println("========== SKOR GÜNCELLEME TAMAMLANDI ==========\n");
            return mac;

        } catch (Exception e) {
            System.err.println("✗ Skor güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Skor güncellenemedi: " + e.getMessage());
        }
    }

    public MacOyuncuOlaylari macOlayEkle(MacOyuncuOlaylari olay, Long editorId) {
        System.out.println("\n========== MAÇ OLAY EKLEME ==========");
        System.out.println("Maç ID: " + olay.getMac().getId());
        System.out.println("Olay Tipi: " + olay.getOlayTipi());
        System.out.println("Dakika: " + olay.getDakika());
        System.out.println("Editör ID: " + editorId);

        try {
            Long macId = olay.getMac().getId();

            // Bu maçın editörü olup olmadığını kontrol et
            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maça olay ekleme yetkiniz yok");
            }

            // Olayı kaydet
            MacOyuncuOlaylari kaydedilenOlay = macOyuncuOlaylariRepository.save(olay);
            System.out.println("Olay kaydedildi: ID=" + kaydedilenOlay.getId());

            // Observer pattern ile bildirim gönder
            Mac mac = macGetir(macId);
            if ("GOL".equalsIgnoreCase(olay.getOlayTipi())) {
                macOnayKonusu.golAtildi(mac);
                System.out.println(" Gol bildirimi gönderildi");
            }

            System.out.println("========== OLAY EKLEME TAMAMLANDI ==========\n");
            return kaydedilenOlay;

        } catch (Exception e) {
            System.err.println("Olay ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Olay eklenemedi: " + e.getMessage());
        }
    }

    public Mac macBaslat(Long macId, Long editorId) {
        System.out.println("\n========== MAÇ BAŞLATMA ==========");
        System.out.println("Maç ID: " + macId);
        System.out.println("Editör ID: " + editorId);

        try {
            Mac mac = macGetir(macId);

            // Bu maçın editörü olup olmadığını kontrol et
            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maçı başlatma yetkiniz yok");
            }

            // Observer pattern ile bildirim gönder
            macOnayKonusu.macBasladi(mac);
            System.out.println("Maç başladı bildirimi gönderildi");
            System.out.println("========== MAÇ BAŞLATMA TAMAMLANDI ==========\n");

            return mac;
        } catch (Exception e) {
            System.err.println("✗ Maç başlatma hatası: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Maç başlatılamadı: " + e.getMessage());
        }
    }

    public Mac macSonuclandir(Long macId, Long editorId) {
        System.out.println("\n========== MAÇ SONUÇLANDIRMA ==========");
        System.out.println("Maç ID: " + macId);
        System.out.println("Editör ID: " + editorId);

        try {
            Mac mac = macGetir(macId);

            // Bu maçın editörü olup olmadığını kontrol et
            java.util.Optional<Long> ilkKayitEditorId = macDurumGecmisiRepository.findFirstRecordEditorIdByMacId(macId);
            if (!ilkKayitEditorId.isPresent() || !ilkKayitEditorId.get().equals(editorId)) {
                throw new RuntimeException("Bu maçı sonuçlandırma yetkiniz yok");
            }

            // Observer pattern ile bildirim gönder
            populateMacData(mac);
            macOnayKonusu.macBitti(mac);
            System.out.println("Maç bitti bildirimi gönderildi");
            System.out.println("========== MAÇ SONUÇLANDIRMA TAMAMLANDI ==========\n");

            return mac;
        } catch (Exception e) {
            System.err.println("Maç sonuçlandırma hatası: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Maç sonuçlandırılamadı: " + e.getMessage());
        }
    }
}
