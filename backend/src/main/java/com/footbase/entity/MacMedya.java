package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mac_medya")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MacMedya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mac_id", nullable = false)
    private Mac mac;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false, length = 20)
    private String tip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Kullanici editor;

    public MacMedya() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mac getMac() {
        return mac;
    }

    public void setMac(Mac mac) {
        this.mac = mac;
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

    public Kullanici getEditor() {
        return editor;
    }

    public void setEditor(Kullanici editor) {
        this.editor = editor;
    }
}
