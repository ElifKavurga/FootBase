package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mac_takimlari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MacTakimlari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "takim_id", nullable = false)
    private Takim takim;

    @Column(name = "ev_sahibi", nullable = false)
    private Boolean evSahibi;

    @Column(nullable = false)
    private Integer skor = 0;

    public MacTakimlari() {
        this.skor = 0;
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

    public Takim getTakim() {
        return takim;
    }

    public void setTakim(Takim takim) {
        this.takim = takim;
    }

    public Boolean getEvSahibi() {
        return evSahibi;
    }

    public boolean isEvSahibi() {
        return evSahibi != null && evSahibi;
    }

    public void setEvSahibi(Boolean evSahibi) {
        this.evSahibi = evSahibi;
    }

    public Integer getSkor() {
        return skor;
    }

    public void setSkor(Integer skor) {
        this.skor = skor;
    }
}
