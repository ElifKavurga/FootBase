package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "yorumlar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Yorum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yorum_id")
    private Long id;

    @Column(name = "icerik", nullable = false, columnDefinition = "TEXT")
    private String mesaj;

    @Column(name = "olusturma_tarihi", nullable = false)
    private LocalDateTime yorumTarihi;

    @Column(name = "yorum_tipi", nullable = false)
    private String yorumTipi = "USER";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "yorum_begenileri", joinColumns = @JoinColumn(name = "yorum_id"), inverseJoinColumns = @JoinColumn(name = "kullanici_id", referencedColumnName = "kullanici_id"))
    private Set<Kullanici> begenenKullanicilar = new HashSet<>();

    public Yorum() {
        this.yorumTarihi = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public LocalDateTime getYorumTarihi() {
        return yorumTarihi;
    }

    public void setYorumTarihi(LocalDateTime yorumTarihi) {
        this.yorumTarihi = yorumTarihi;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public Set<Kullanici> getBegenenKullanicilar() {
        return begenenKullanicilar;
    }

    public void setBegenenKullanicilar(Set<Kullanici> begenenKullanicilar) {
        this.begenenKullanicilar = begenenKullanicilar;
    }

    public String getYorumTipi() {
        return yorumTipi;
    }

    public void setYorumTipi(String yorumTipi) {
        this.yorumTipi = yorumTipi;
    }
}
