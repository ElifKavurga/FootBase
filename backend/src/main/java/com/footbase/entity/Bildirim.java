package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bildirimler")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Bildirim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alici_kullanici_id", nullable = false)
    private Kullanici aliciKullanici;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gonderici_kullanici_id")
    private Kullanici gondericiKullanici;

    @Column(name = "bildirim_tipi", nullable = false, length = 50)
    private String bildirimTipi;

    @Column(name = "baslik", nullable = false)
    private String baslik;

    @Column(name = "icerik", nullable = false, length = 1000)
    private String icerik;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id")
    private Mac mac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id")
    private Oyuncu oyuncu;

    @Column(name = "okundu", nullable = false)
    private Boolean okundu = false;

    @Column(name = "olusturma_zamani", nullable = false)
    private LocalDateTime olusturmaZamani;

    @Column(name = "okunma_zamani")
    private LocalDateTime okunmaZamani;

    @Column(name = "hedef_url", length = 500)
    private String hedefUrl;

    public Bildirim() {
        this.olusturmaZamani = LocalDateTime.now();
        this.okundu = false;
    }

    public Bildirim(Kullanici aliciKullanici, String bildirimTipi, String baslik, String icerik) {
        this();
        this.aliciKullanici = aliciKullanici;
        this.bildirimTipi = bildirimTipi;
        this.baslik = baslik;
        this.icerik = icerik;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Kullanici getAliciKullanici() {
        return aliciKullanici;
    }

    public void setAliciKullanici(Kullanici aliciKullanici) {
        this.aliciKullanici = aliciKullanici;
    }

    public Kullanici getGondericiKullanici() {
        return gondericiKullanici;
    }

    public void setGondericiKullanici(Kullanici gondericiKullanici) {
        this.gondericiKullanici = gondericiKullanici;
    }

    public String getBildirimTipi() {
        return bildirimTipi;
    }

    public void setBildirimTipi(String bildirimTipi) {
        this.bildirimTipi = bildirimTipi;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
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

    public Boolean getOkundu() {
        return okundu;
    }

    public void setOkundu(Boolean okundu) {
        this.okundu = okundu;
        if (okundu && this.okunmaZamani == null) {
            this.okunmaZamani = LocalDateTime.now();
        }
    }

    public LocalDateTime getOlusturmaZamani() {
        return olusturmaZamani;
    }

    public void setOlusturmaZamani(LocalDateTime olusturmaZamani) {
        this.olusturmaZamani = olusturmaZamani;
    }

    public LocalDateTime getOkunmaZamani() {
        return okunmaZamani;
    }

    public void setOkunmaZamani(LocalDateTime okunmaZamani) {
        this.okunmaZamani = okunmaZamani;
    }

    public String getHedefUrl() {
        return hedefUrl;
    }

    public void setHedefUrl(String hedefUrl) {
        this.hedefUrl = hedefUrl;
    }

    public void okunduOlarakIsaretle() {
        this.okundu = true;
        this.okunmaZamani = LocalDateTime.now();
    }

    public boolean okunmamisMi() {
        return !this.okundu;
    }

    @Override
    public String toString() {
        return "Bildirim{" +
                "id=" + id +
                ", bildirimTipi='" + bildirimTipi + '\'' +
                ", baslik='" + baslik + '\'' +
                ", okundu=" + okundu +
                ", olusturmaZamani=" + olusturmaZamani +
                '}';
    }
}