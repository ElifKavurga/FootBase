package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mac_puanlamalari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Puanlama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puan;

    @Column(nullable = false)
    private Integer agirlik = 1;

    @Transient
    private LocalDateTime puanlamaTarihi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @Transient
    private Oyuncu oyuncu;

    public Puanlama() {
        this.puanlamaTarihi = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPuan() {
        return puan;
    }

    public void setPuan(Integer puan) {
        this.puan = puan;
    }

    public Integer getAgirlik() {
        return agirlik;
    }

    public void setAgirlik(Integer agirlik) {
        this.agirlik = agirlik;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
    }

    public LocalDateTime getPuanlamaTarihi() {
        return puanlamaTarihi;
    }

    public void setPuanlamaTarihi(LocalDateTime puanlamaTarihi) {
        this.puanlamaTarihi = puanlamaTarihi;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }
}
