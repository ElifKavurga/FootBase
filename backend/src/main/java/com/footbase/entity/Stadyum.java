package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stadyumlar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Stadyum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stadyum_id")
    private Long id;

    @Column(name = "stadyum_adi", nullable = false, unique = true)
    private String stadyumAdi;

    @Column(name = "sehir", nullable = false)
    private String sehir;

    @Column(name = "ulke", nullable = false)
    private String ulke;

    @JsonIgnore
    @OneToMany(mappedBy = "stadyum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Takim> takimlar = new ArrayList<>();

    public Stadyum() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStadyumAdi() {
        return stadyumAdi;
    }

    public void setStadyumAdi(String stadyumAdi) {
        this.stadyumAdi = stadyumAdi;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getUlke() {
        return ulke;
    }

    public void setUlke(String ulke) {
        this.ulke = ulke;
    }

    public List<Takim> getTakimlar() {
        return takimlar;
    }

    public void setTakimlar(List<Takim> takimlar) {
        this.takimlar = takimlar;
    }
}
