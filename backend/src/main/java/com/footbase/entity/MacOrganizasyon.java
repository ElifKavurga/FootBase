package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mac_organizasyon")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@IdClass(MacOrganizasyonId.class)
public class MacOrganizasyon {

    @Id
    @Column(name = "mac_id", nullable = false)
    private Long macId;

    @Id
    @Column(name = "organizasyon_id", nullable = false)
    private Long organizasyonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", insertable = false, updatable = false)
    private Mac mac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizasyon_id", insertable = false, updatable = false)
    private Organizasyon organizasyon;

    @Column(nullable = false)
    private String etap;

    public MacOrganizasyon() {
    }

    public Long getMacId() {
        return macId;
    }

    public void setMacId(Long macId) {
        this.macId = macId;
    }

    public Long getOrganizasyonId() {
        return organizasyonId;
    }

    public void setOrganizasyonId(Long organizasyonId) {
        this.organizasyonId = organizasyonId;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
        if (mac != null) {
            this.macId = mac.getId();
        }
    }

    public Organizasyon getOrganizasyon() {
        return organizasyon;
    }

    public void setOrganizasyon(Organizasyon organizasyon) {
        this.organizasyon = organizasyon;
        if (organizasyon != null) {
            this.organizasyonId = organizasyon.getId();
        }
    }

    public String getEtap() {
        return etap;
    }

    public void setEtap(String etap) {
        this.etap = etap;
    }
}
