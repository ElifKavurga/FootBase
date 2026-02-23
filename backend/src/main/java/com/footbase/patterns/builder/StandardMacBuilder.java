package com.footbase.patterns.builder;

import com.footbase.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StandardMacBuilder implements MacBuilderInterface {

    private static final Logger logger = LoggerFactory.getLogger(StandardMacBuilder.class);
    private Mac mac;
    private Takim evSahibiTakim;
    private Takim deplasmanTakim;
    private Integer evSahibiSkor;
    private Integer deplasmanSkor;

    public StandardMacBuilder() {
        this.reset();
        logger.info("StandardMacBuilder oluşturuldu (Classic GoF Pattern)");
    }

    @Override
    public void buildTakimlar(Takim evSahibi, Takim deplasman) {
        if (evSahibi == null || deplasman == null) {
            throw new IllegalArgumentException("Takımlar null olamaz!");
        }
        if (evSahibi.getId().equals(deplasman.getId())) {
            throw new IllegalArgumentException("Aynı takım hem ev sahibi hem deplasman olamaz!");
        }
        this.evSahibiTakim = evSahibi;
        this.deplasmanTakim = deplasman;
        logger.debug("Takımlar build edildi: {} vs {}", evSahibi.getAd(), deplasman.getAd());
    }

    @Override
    public void buildTarihSaat(LocalDate tarih, LocalTime saat) {
        if (tarih == null || saat == null) {
            throw new IllegalArgumentException("Tarih ve saat null olamaz!");
        }
        this.mac.setTarih(tarih);
        this.mac.setSaat(saat);
        logger.debug("Tarih/Saat build edildi: {} {}", tarih, saat);
    }

    @Override
    public void buildStadyum(Stadyum stadyum) {
        this.mac.setStadyum(stadyum);
        if (stadyum != null) {
            logger.debug("Stadyum build edildi: {}", stadyum.getStadyumAdi());
        }
    }

    @Override
    public void buildHakem(Hakem hakem) {
        this.mac.setHakem(hakem);
        if (hakem != null) {
            logger.debug("Hakem build edildi: {}", hakem.getAdSoyad());
        }
    }

    @Override
    public void buildLig(Lig lig) {
        this.mac.setLig(lig);
        if (lig != null) {
            logger.debug("Lig build edildi: {}", lig.getLigAdi());
        }
    }

    @Override
    public void buildOrganizasyon(Organizasyon organizasyon) {
        this.mac.setOrganizasyon(organizasyon);
        if (organizasyon != null) {
            logger.debug("Organizasyon build edildi: {}", organizasyon.getAd());
        }
    }

    @Override
    public void buildSkorlar(Integer evSahibiSkor, Integer deplasmanSkor) {
        this.evSahibiSkor = evSahibiSkor;
        this.deplasmanSkor = deplasmanSkor;
        if (evSahibiSkor != null && deplasmanSkor != null) {
            logger.debug("Skorlar build edildi: {} - {}", evSahibiSkor, deplasmanSkor);
        }
    }

    @Override
    public void buildNot(String not) {
        this.mac.setNot(not);
        if (not != null && !not.isEmpty()) {
            logger.debug("Not build edildi");
        }
    }

    @Override
    public void reset() {
        this.mac = new Mac();
        this.evSahibiTakim = null;
        this.deplasmanTakim = null;
        this.evSahibiSkor = null;
        this.deplasmanSkor = null;
        logger.debug("StandardMacBuilder resetlendi");
    }

    @Override
    public Mac getResult() {
        logger.info("Mac nesnesi getiriliyor...");
        validate();
        List<MacTakimlari> macTakimlariList = new ArrayList<>();
        MacTakimlari evSahibiMT = new MacTakimlari();
        evSahibiMT.setMac(this.mac);
        evSahibiMT.setTakim(this.evSahibiTakim);
        evSahibiMT.setEvSahibi(true);
        evSahibiMT.setSkor(this.evSahibiSkor);
        macTakimlariList.add(evSahibiMT);
        MacTakimlari deplasmanMT = new MacTakimlari();
        deplasmanMT.setMac(this.mac);
        deplasmanMT.setTakim(this.deplasmanTakim);
        deplasmanMT.setEvSahibi(false);
        deplasmanMT.setSkor(this.deplasmanSkor);
        macTakimlariList.add(deplasmanMT);
        this.mac.setMacTakimlari(macTakimlariList);

        logger.info("Mac nesnesi başarıyla oluşturuldu: {} vs {}",
                evSahibiTakim.getAd(), deplasmanTakim.getAd());
        Mac result = this.mac;
        this.reset();

        return result;
    }

    private void validate() {
        List<String> errors = new ArrayList<>();

        if (evSahibiTakim == null) {
            errors.add("Ev sahibi takım zorunludur");
        }

        if (deplasmanTakim == null) {
            errors.add("Deplasman takımı zorunludur");
        }

        if (mac.getTarih() == null) {
            errors.add("Tarih zorunludur");
        }

        if (mac.getSaat() == null) {
            errors.add("Saat zorunludur");
        }

        if (!errors.isEmpty()) {
            String errorMsg = "Builder validation hatası:\n  - " + String.join("\n  - ", errors);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        logger.debug("Validation başarılı");
    }
}
