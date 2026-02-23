package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "takimlar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Takim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad;

    @Column(name = "logo_url")
    private String logo;

    @Column(name = "kurulma_yili")
    private Integer kurulusYili;

    private String aciklama;

    @Transient
    private String renkler;

    @Transient
    private Long takimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadyum_id")
    private Stadyum stadyum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lig_id")
    private Lig lig;

    @Transient
    private String teknikDirektorAdi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teknik_direktor_id")
    private TeknikDirektor teknikDirektor;

    @JsonIgnore
    @OneToMany(mappedBy = "takim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Oyuncu> oyuncular = new ArrayList<>();

    @Transient
    private List<Mac> evSahibiMaclar = new ArrayList<>();

    @Transient
    private List<Mac> deplasmanMaclar = new ArrayList<>();

    public Takim() {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getKurulusYili() {
        return kurulusYili;
    }

    public void setKurulusYili(Integer kurulusYili) {
        this.kurulusYili = kurulusYili;
    }

    public Long getTakimId() {
        return takimId;
    }

    public void setTakimId(Long takimId) {
        this.takimId = takimId;
    }

    public Stadyum getStadyum() {
        return stadyum;
    }

    public void setStadyum(Stadyum stadyum) {
        this.stadyum = stadyum;
    }

    public Lig getLig() {
        return lig;
    }

    public void setLig(Lig lig) {
        this.lig = lig;
    }

    public String getTeknikDirektorAdi() {
        return teknikDirektorAdi;
    }

    public void setTeknikDirektorAdi(String teknikDirektorAdi) {
        this.teknikDirektorAdi = teknikDirektorAdi;
    }

    public TeknikDirektor getTeknikDirektor() {
        return teknikDirektor;
    }

    public void setTeknikDirektor(TeknikDirektor teknikDirektor) {
        this.teknikDirektor = teknikDirektor;
    }

    public List<Oyuncu> getOyuncular() {
        return oyuncular;
    }

    public void setOyuncular(List<Oyuncu> oyuncular) {
        this.oyuncular = oyuncular;
    }

    public List<Mac> getEvSahibiMaclar() {
        return evSahibiMaclar;
    }

    public void setEvSahibiMaclar(List<Mac> evSahibiMaclar) {
        this.evSahibiMaclar = evSahibiMaclar;
    }

    public List<Mac> getDeplasmanMaclar() {
        return deplasmanMaclar;
    }

    public void setDeplasmanMaclar(List<Mac> deplasmanMaclar) {
        this.deplasmanMaclar = deplasmanMaclar;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getRenkler() {
        return renkler;
    }

    public void setRenkler(String renkler) {
        this.renkler = renkler;
    }
}
