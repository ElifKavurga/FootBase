package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizasyonlar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Organizasyon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizasyon_id")
    private Long id;

    @Column(nullable = false)
    private String ad;

    @JsonIgnore
    @OneToMany(mappedBy = "organizasyon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MacOrganizasyon> maclar = new ArrayList<>();

    public Organizasyon() {
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

    public List<MacOrganizasyon> getMaclar() {
        return maclar;
    }

    public void setMaclar(List<MacOrganizasyon> maclar) {
        this.maclar = maclar;
    }
}
