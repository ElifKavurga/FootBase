package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oyuncular")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Oyuncu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad;

    @Column(nullable = false)
    private String soyad;

    @Column(name = "mevki", nullable = false)
    private String pozisyon;

    @Transient
    private java.time.LocalDate dogumTarihi;

    @Column(name = "ulke", nullable = false)
    private String milliyet;

    @Column(name = "foto_url")
    private String fotograf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "takim_id")
    private Takim takim;
    @Transient
    private List<Puanlama> puanlamalar = new ArrayList<>();

    public Oyuncu() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getPozisyon() {
        return pozisyon;
    }

    public void setPozisyon(String pozisyon) {
        this.pozisyon = pozisyon;
    }

    public java.time.LocalDate getDogumTarihi() {
        return dogumTarihi;
    }

    public void setDogumTarihi(java.time.LocalDate dogumTarihi) {
        this.dogumTarihi = dogumTarihi;
    }

    @Transient
    public Integer getYas() {
        if (dogumTarihi != null) {
            return java.time.LocalDate.now().getYear() - dogumTarihi.getYear();
        }
        return null;
    }

    public void setYas(Integer yas) {
    }

    public String getMilliyet() {
        return milliyet;
    }

    public void setMilliyet(String milliyet) {
        this.milliyet = milliyet;
    }

    public String getFotograf() {
        return fotograf;
    }

    public void setFotograf(String fotograf) {
        this.fotograf = fotograf;
    }

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public List<Puanlama> getPuanlamalar() {
        return puanlamalar;
    }

    public void setPuanlamalar(List<Puanlama> puanlamalar) {
        this.puanlamalar = puanlamalar;
    }
}
