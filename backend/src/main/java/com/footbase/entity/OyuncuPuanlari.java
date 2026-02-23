package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "oyuncu_puanlari")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OyuncuPuanlari {

    @Id
    @Column(name = "oyuncu_id", nullable = false)
    private Long oyuncuId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oyuncu_id", insertable = false, updatable = false)
    private Oyuncu oyuncu;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal puan;

    public OyuncuPuanlari() {
    }

    public Long getOyuncuId() {
        return oyuncuId;
    }

    public void setOyuncuId(Long oyuncuId) {
        this.oyuncuId = oyuncuId;
    }

    public Oyuncu getOyuncu() {
        return oyuncu;
    }

    public void setOyuncu(Oyuncu oyuncu) {
        this.oyuncu = oyuncu;
    }

    public BigDecimal getPuan() {
        return puan;
    }

    public void setPuan(BigDecimal puan) {
        this.puan = puan;
    }
}
