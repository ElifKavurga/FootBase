package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oyuncu_yorumlari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OyuncuYorumlari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yorum_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", nullable = false)
    private Oyuncu oyuncu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kullanici_id", nullable = false, referencedColumnName = "kullanici_id")
    private Kullanici kullanici;

    @Column(name = "icerik", nullable = false, columnDefinition = "TEXT")
    private String icerik;

    @Column(name = "olusturma_tarihi", nullable = false)
    private LocalDateTime olusturmaTarihi;

    public OyuncuYorumlari() {
        this.olusturmaTarihi = LocalDateTime.now();
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

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }
}
