package com.footbase.patterns.command.dto;

public class MacSonlandirDTO {

    private Long macId;
    private Integer evSahibiSkor;
    private Integer deplasmanSkor;
    private String durum;
    private String sonuc;
    private String aciklama;

    public MacSonlandirDTO() {
    }

    public MacSonlandirDTO(Long macId, Integer evSahibiSkor, Integer deplasmanSkor, String durum) {
        this.macId = macId;
        this.evSahibiSkor = evSahibiSkor;
        this.deplasmanSkor = deplasmanSkor;
        this.durum = durum;
    }

    public Long getMacId() {
        return macId;
    }

    public void setMacId(Long macId) {
        this.macId = macId;
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

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getSonuc() {
        return sonuc;
    }

    public void setSonuc(String sonuc) {
        this.sonuc = sonuc;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    @Override
    public String toString() {
        return "MacSonlandirDTO{" +
                "macId=" + macId +
                ", evSahibiSkor=" + evSahibiSkor +
                ", deplasmanSkor=" + deplasmanSkor +
                ", durum='" + durum + '\'' +
                ", sonuc='" + sonuc + '\'' +
                ", aciklama='" + aciklama + '\'' +
                '}';
    }
}
