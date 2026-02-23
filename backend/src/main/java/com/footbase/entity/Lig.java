package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ligler")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Lig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lig_id")
    private Long id;

    @Column(name = "lig_adi", nullable = false, unique = true)
    private String ligAdi;

    @Column(name = "ulke", nullable = false)
    private String ulke;

    @JsonIgnore
    @OneToMany(mappedBy = "lig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Takim> takimlar = new ArrayList<>();

    public Lig() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLigAdi() {
        return ligAdi;
    }

    public void setLigAdi(String ligAdi) {
        this.ligAdi = ligAdi;
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
