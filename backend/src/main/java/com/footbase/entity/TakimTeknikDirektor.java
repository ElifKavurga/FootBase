package com.footbase.entity;

import java.time.LocalDate;

public class TakimTeknikDirektor {
    private Integer id;
    private Takim takim;
    private TeknikDirektor teknikDirektor;
    private LocalDate baslangicTarihi;
    private LocalDate bitisTarihi;

    public TakimTeknikDirektor() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public TeknikDirektor getTeknikDirektor() {
        return teknikDirektor;
    }

    public void setTeknikDirektor(TeknikDirektor teknikDirektor) {
        this.teknikDirektor = teknikDirektor;
    }

    public LocalDate getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(LocalDate baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public LocalDate getBitisTarihi() {
        return bitisTarihi;
    }

    public void setBitisTarihi(LocalDate bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }
}
