package com.footbase.patterns.command.dto;

public class SkorGirisiDTO {

    private Long macId;
    private Integer evSahibiSkor;
    private Integer deplasmanSkor;
    private String aciklama;

    public SkorGirisiDTO() {
    }

    public SkorGirisiDTO(Long macId, Integer evSahibiSkor, Integer deplasmanSkor) {
        this.macId = macId;
        this.evSahibiSkor = evSahibiSkor;
        this.deplasmanSkor = deplasmanSkor;
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

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    @Override
    public String toString() {
        return "SkorGirisiDTO{" +
                "macId=" + macId +
                ", evSahibiSkor=" + evSahibiSkor +
                ", deplasmanSkor=" + deplasmanSkor +
                ", aciklama='" + aciklama + '\'' +
                '}';
    }
}
