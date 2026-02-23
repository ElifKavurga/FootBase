package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mac_kadrolari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MacKadro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    @Column(name = "baslangic_11", nullable = false)
    private Boolean baslangic11 = false;

    @Transient
    private String takimTarafi;

    @Transient
    private Short sariKartSayisi = 0;

    @Transient
    private Boolean kirmiziKart = false;

    @Transient
    private Short golSayisi = 0;

    @Transient
    private Integer dakikaCikis;

    @Transient
    private Integer dakikaGiris;

    public MacKadro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public String getTakimTarafi() {
        return takimTarafi;
    }

    public void setTakimTarafi(String takimTarafi) {
        this.takimTarafi = takimTarafi;
    }

    public Boolean getBaslangic11() {
        return baslangic11;
    }

    public void setBaslangic11(Boolean baslangic11) {
        this.baslangic11 = baslangic11;
    }

    public Short getSariKartSayisi() {
        return sariKartSayisi;
    }

    public void setSariKartSayisi(Short sariKartSayisi) {
        this.sariKartSayisi = sariKartSayisi;
    }

    public Boolean getKirmiziKart() {
        return kirmiziKart;
    }

    public void setKirmiziKart(Boolean kirmiziKart) {
        this.kirmiziKart = kirmiziKart;
    }

    public Short getGolSayisi() {
        return golSayisi;
    }

    public void setGolSayisi(Short golSayisi) {
        this.golSayisi = golSayisi;
    }

    public Integer getDakikaCikis() {
        return dakikaCikis;
    }

    public void setDakikaCikis(Integer dakikaCikis) {
        this.dakikaCikis = dakikaCikis;
    }

    public Integer getDakikaGiris() {
        return dakikaGiris;
    }

    public void setDakikaGiris(Integer dakikaGiris) {
        this.dakikaGiris = dakikaGiris;
    }
}
