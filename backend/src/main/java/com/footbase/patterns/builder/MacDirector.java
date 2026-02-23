package com.footbase.patterns.builder;

import com.footbase.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class MacDirector {

    private static final Logger logger = LoggerFactory.getLogger(MacDirector.class);
    private MacBuilderInterface builder;

    public MacDirector(MacBuilderInterface builder) {
        this.builder = builder;
        logger.info("MacDirector oluşturuldu (Classic GoF Pattern)");
    }

    public void setBuilder(MacBuilderInterface builder) {
        this.builder = builder;
        logger.debug("Builder değiştirildi");
    }

    public Mac yaratLigMaci(Takim evSahibi, Takim deplasman, LocalDate tarih, LocalTime saat) {
        logger.info("Director: Basit lig maçı oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);

        Mac mac = builder.getResult();
        logger.info("Basit lig maçı oluşturuldu");
        return mac;
    }

    public Mac yaratDetayliLigMaci(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig) {

        logger.info("Director: Detaylı lig maçı oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);

        Mac mac = builder.getResult();
        logger.info("Detaylı lig maçı oluşturuldu");
        return mac;
    }

    public Mac yaratTamKapsamliMac(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig,
            Organizasyon organizasyon,
            String not) {

        logger.info("Director: Tam kapsamlı maç oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);
        builder.buildOrganizasyon(organizasyon);
        builder.buildNot(not);

        Mac mac = builder.getResult();
        logger.info("Tam kapsamlı maç oluşturuldu");
        return mac;
    }

    public Mac yaratTamamlanmisMac(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat,
            Integer evSahibiSkor,
            Integer deplasmanSkor) {

        logger.info("Director: Tamamlanmış maç oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildSkorlar(evSahibiSkor, deplasmanSkor);

        Mac mac = builder.getResult();
        logger.info("Tamamlanmış maç oluşturuldu: {} - {}", evSahibiSkor, deplasmanSkor);
        return mac;
    }

    public Mac yaratDerbiMaci(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem) {

        logger.info("Director: Derbi maçı oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildNot("⚡ DERBİ MAÇI - Yüksek güvenlik tedbirleri");

        Mac mac = builder.getResult();
        logger.info("Derbi maçı oluşturuldu");
        return mac;
    }

    public Mac yaratSampiyonlukMaci(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat,
            Stadyum stadyum,
            Hakem hakem,
            Lig lig) {

        logger.info("Director: Şampiyonluk maçı oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildStadyum(stadyum);
        builder.buildHakem(hakem);
        builder.buildLig(lig);
        builder.buildNot("ŞAMPİYONLUK MAÇI - Kritik öneme sahip");

        Mac mac = builder.getResult();
        logger.info("Şampiyonluk maçı oluşturuldu");
        return mac;
    }

    public Mac yaratTestMaci(
            Takim evSahibi,
            Takim deplasman,
            LocalDate tarih,
            LocalTime saat) {

        logger.info("Director: Test maçı oluşturuluyor...");

        builder.reset();
        builder.buildTakimlar(evSahibi, deplasman);
        builder.buildTarihSaat(tarih, saat);
        builder.buildNot("TEST MAÇI - Resmi maç değil");

        Mac mac = builder.getResult();
        logger.info("Test maçı oluşturuldu");
        return mac;
    }
}
