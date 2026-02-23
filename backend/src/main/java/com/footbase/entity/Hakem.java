package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hakemler")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Hakem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hakem_id")
    private Long id;

    @Column(name = "ad_soyad", nullable = false)
    private String adSoyad;

    @Column(name = "uyruk")
    private String uyruk;

    @JsonIgnore
    @OneToMany(mappedBy = "hakem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mac> maclar = new ArrayList<>();

    public Hakem() {
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

    public List<Mac> getMaclar() {
        return maclar;
    }

    public void setMaclar(List<Mac> maclar) {
        this.maclar = maclar;
    }
}