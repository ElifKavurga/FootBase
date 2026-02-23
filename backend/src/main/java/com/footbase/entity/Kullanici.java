package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kullanicilar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kullanici_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String sifre;

    @Column(nullable = false)
    private String rol;

    @Column(name = "kullanici_adi", nullable = false, unique = true)
    private String kullaniciAdi;

    @Transient
    private String ad;

    @Transient
    private String soyad;

    @Transient
    private String profilResmi;

    @Transient
    private Boolean admin;

    @JsonIgnore
    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Yorum> yorumlar = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Puanlama> puanlamalar = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OyuncuYorumlari> oyuncuYorumlari = new ArrayList<>();

    public Kullanici() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getProfilResmi() {
        return profilResmi;
    }

    public void setProfilResmi(String profilResmi) {
        this.profilResmi = profilResmi;
    }

    public Boolean getAdmin() {
        if (admin == null && rol != null) {
            admin = "ADMIN".equals(rol);
        }
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
        this.admin = "ADMIN".equals(rol);
    }

    public List<Yorum> getYorumlar() {
        return yorumlar;
    }

    public void setYorumlar(List<Yorum> yorumlar) {
        this.yorumlar = yorumlar;
    }

    public List<Puanlama> getPuanlamalar() {
        return puanlamalar;
    }

    public void setPuanlamalar(List<Puanlama> puanlamalar) {
        this.puanlamalar = puanlamalar;
    }

    public List<OyuncuYorumlari> getOyuncuYorumlari() {
        return oyuncuYorumlari;
    }

    public void setOyuncuYorumlari(List<OyuncuYorumlari> oyuncuYorumlari) {
        this.oyuncuYorumlari = oyuncuYorumlari;
    }
}
