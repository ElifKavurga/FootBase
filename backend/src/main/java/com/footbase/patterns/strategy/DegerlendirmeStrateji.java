package com.footbase.patterns.strategy;

public interface DegerlendirmeStrateji {

    double puanHesapla(int yildizSayisi);

    double getAgirlik();

    String getStratejAdi();

    String getAciklama();
}
