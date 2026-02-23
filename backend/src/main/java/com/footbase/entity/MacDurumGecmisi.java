package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mac_durum_gecmisi")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MacDurumGecmisi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @Column(nullable = false, columnDefinition = "yayim_durumu_enum")
    private String durum;

    @Column(name = "islem_tarihi")
    private LocalDateTime islemTarihi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "islem_yapan_kullanici_id")
    private Kullanici islemYapanKullanici;

    public MacDurumGecmisi() {
        this.islemTarihi = LocalDateTime.now();
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

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public LocalDateTime getIslemTarihi() {
        return islemTarihi;
    }

    public void setIslemTarihi(LocalDateTime islemTarihi) {
        this.islemTarihi = islemTarihi;
    }

    public Kullanici getIslemYapanKullanici() {
        return islemYapanKullanici;
    }

    public void setIslemYapanKullanici(Kullanici islemYapanKullanici) {
        this.islemYapanKullanici = islemYapanKullanici;
    }
}
