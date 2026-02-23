package com.footbase.patterns.builder;

import com.footbase.entity.*;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MacBuilderInterface {

    void buildTakimlar(Takim evSahibi, Takim deplasman);

    void buildTarihSaat(LocalDate tarih, LocalTime saat);

    void buildStadyum(Stadyum stadyum);

    void buildHakem(Hakem hakem);

    void buildLig(Lig lig);

    void buildOrganizasyon(Organizasyon organizasyon);

    void buildSkorlar(Integer evSahibiSkor, Integer deplasmanSkor);

    void buildNot(String not);

    void reset();

    Mac getResult();
}
