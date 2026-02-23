package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maclar")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Mac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Takim evSahibiTakim;

    @Transient
    private Takim deplasmanTakim;

    @Transient
    private Integer evSahibiSkor;

    @Transient
    private Integer deplasmanSkor;

    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MacTakimlari> macTakimlari = new ArrayList<>();

    @Column(nullable = false)
    private java.time.LocalDate tarih;

    @Column(nullable = false)
    private java.time.LocalTime saat;

    @Transient
    private String durum = "Planlandı";

    @Transient
    private String onayDurumu = "ONAY_BEKLIYOR";

    @Transient
    private Kullanici editor;

    @Transient
    private Long editorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakem_id")
    private Hakem hakem;

    @Transient
    private Stadyum stadyum;

    @Transient
    private Lig lig;

    @Transient
    private Organizasyon organizasyon;

    @Transient
    private String not;

    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Yorum> yorumlar = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "mac", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Puanlama> puanlamalar = new ArrayList<>();

    public Mac() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Takim getEvSahibiTakim() {
        return evSahibiTakim;
    }

    public void setEvSahibiTakim(Takim evSahibiTakim) {
        this.evSahibiTakim = evSahibiTakim;
    }

    public Takim getDeplasmanTakim() {
        return deplasmanTakim;
    }

    public void setDeplasmanTakim(Takim deplasmanTakim) {
        this.deplasmanTakim = deplasmanTakim;
    }

    public Integer getEvSahibiSkor() {
        return evSahibiSkor;
    }

    public void setEvSahibiSkor(Integer evSahibiSkor) {
        this.evSahibiSkor = evSahibiSkor;
    }

    public Integer getDeplasmanSkor() {
        return deplasmanSkor;
    }

    public void setDeplasmanSkor(Integer deplasmanSkor) {
        this.deplasmanSkor = deplasmanSkor;
    }

    public java.time.LocalDate getTarih() {
        return tarih;
    }

    public void setTarih(java.time.LocalDate tarih) {
        this.tarih = tarih;
    }

    public java.time.LocalTime getSaat() {
        return saat;
    }

    public void setSaat(java.time.LocalTime saat) {
        this.saat = saat;
    }

    public LocalDateTime getMacTarihi() {
        if (tarih != null && saat != null) {
            return LocalDateTime.of(tarih, saat);
        }
        return null;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public Hakem getHakem() {
        return hakem;
    }

    public void setHakem(Hakem hakem) {
        this.hakem = hakem;
    }

    public List<Yorum> getYorumlar() {
        return yorumlar;
    }

    public void setYorumlar(List<Yorum> yorumlar) {
        this.yorumlar = yorumlar;
    }

    public List<Puanlama> getPuanlamalar() {
        return puanlamalar;
    }

    public void setPuanlamalar(List<Puanlama> puanlamalar) {
        this.puanlamalar = puanlamalar;
    }

    public List<MacTakimlari> getMacTakimlari() {
        return macTakimlari;
    }

    public void setMacTakimlari(List<MacTakimlari> macTakimlari) {
        this.macTakimlari = macTakimlari;
    }

    public String getOnayDurumu() {
        return onayDurumu;
    }

    public void setOnayDurumu(String onayDurumu) {
        this.onayDurumu = onayDurumu;
    }

    public Kullanici getEditor() {
        return editor;
    }

    public void setEditor(Kullanici editor) {
        this.editor = editor;
        if (editor != null) {
            this.editorId = editor.getId();
        }
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public Stadyum getStadyum() {
        return stadyum;
    }

    public void setStadyum(Stadyum stadyum) {
        this.stadyum = stadyum;
    }

    public Lig getLig() {
        return lig;
    }

    public void setLig(Lig lig) {
        this.lig = lig;
    }

    public Organizasyon getOrganizasyon() {
        return organizasyon;
    }

    public void setOrganizasyon(Organizasyon organizasyon) {
        this.organizasyon = organizasyon;
    }

    public String getNot() {
        return not;
    }

    public void setNot(String not) {
        this.not = not;
    }
}
