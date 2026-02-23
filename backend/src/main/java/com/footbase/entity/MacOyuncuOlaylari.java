package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mac_oyuncu_olaylari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MacOyuncuOlaylari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    @Column(name = "olay_turu", nullable = false, length = 20)
    private String olayTuru;

    @Column(name = "dakika")
    private Integer dakika;

    public MacOyuncuOlaylari() {
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

    public String getOlayTuru() {
        return olayTuru;
    }

    public void setOlayTuru(String olayTuru) {
        this.olayTuru = olayTuru;
    }

    public Integer getDakika() {
        return dakika;
    }

    public void setDakika(Integer dakika) {
        this.dakika = dakika;
    }

    public String getOlayTipi() {
        return olayTuru;
    }

    public void setOlayTipi(String olayTipi) {
        this.olayTuru = olayTipi;
    }
}
