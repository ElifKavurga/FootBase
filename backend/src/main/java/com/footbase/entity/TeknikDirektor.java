package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teknik_direktorler")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TeknikDirektor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "td_id")
    private Long id;

    @Column(name = "ad_soyad", nullable = false)
    private String adSoyad;

    @Column(name = "uyruk")
    private String uyruk;

    @Transient
    private List<TakimTeknikDirektor> takimGecmisi = new ArrayList<>();

    public TeknikDirektor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public String getUyruk() {
        return uyruk;
    }

    public void setUyruk(String uyruk) {
        this.uyruk = uyruk;
    }

    public List<TakimTeknikDirektor> getTakimGecmisi() {
        return takimGecmisi;
    }

    public void setTakimGecmisi(List<TakimTeknikDirektor> takimGecmisi) {
        this.takimGecmisi = takimGecmisi;
    }
}
