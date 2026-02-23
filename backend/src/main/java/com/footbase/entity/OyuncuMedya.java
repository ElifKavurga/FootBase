package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oyuncu_medya")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OyuncuMedya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medya_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false, length = 20)
    private String tip;

    @Transient
    private LocalDateTime yuklenmeTarihi;

    @Transient
    private String aciklama;

    public OyuncuMedya() {
        this.yuklenmeTarihi = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public LocalDateTime getYuklenmeTarihi() {
        return yuklenmeTarihi;
    }

    public void setYuklenmeTarihi(LocalDateTime yuklenmeTarihi) {
        this.yuklenmeTarihi = yuklenmeTarihi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
}
