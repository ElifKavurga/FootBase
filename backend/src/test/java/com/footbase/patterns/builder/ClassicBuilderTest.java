package com.footbase.patterns.builder;

import com.footbase.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classic Builder Pattern Test Sınıfı
 * 
 * MacBuilderInterface, StandardMacBuilder ve MacDirector'ın
 * doğru çalıştığını test eder.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
class ClassicBuilderTest {
    
    private Takim galatasaray;
    private Takim fenerbahce;
    private Takim besiktas;
    private Stadyum ttArena;
    private Hakem hakem;
    private Lig superLig;
    private Organizasyon tff;
    
    @BeforeEach
    void setUp() {
        // Mock takımlar
        galatasaray = new Takim();
        galatasaray.setId(1L);
        galatasaray.setAd("Galatasaray");
        
        fenerbahce = new Takim();
        fenerbahce.setId(2L);
        fenerbahce.setAd("Fenerbahçe");
        
        besiktas = new Takim();
        besiktas.setId(3L);
        besiktas.setAd("Beşiktaş");
        
        // Mock stadyum
        ttArena = new Stadyum();
        ttArena.setId(1L);
        ttArena.setStadyumAdi("Türk Telekom Arena");
        
        // Mock hakem
        hakem = new Hakem();
        hakem.setId(1L);
        hakem.setAdSoyad("Ali Palabıyık");
        
        // Mock lig
        superLig = new Lig();
        superLig.setId(1L);
        superLig.setLigAdi("Süper Lig");
        
        // Mock organizasyon
        tff = new Organizasyon();
        tff.setId(1L);
        tff.setAd("TFF");
    }
    
    // ================= BUILDER INTERFACE TESTLERİ =================
    
    @Test
    @DisplayName("StandardMacBuilder ile minimal maç oluşturulmalı")
    void testStandardBuilderMinimal() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        LocalDate tarih = LocalDate.of(2025, 12, 25);
        LocalTime saat = LocalTime.of(20, 0);
        
        // When
        builder.buildTakimlar(galatasaray, fenerbahce);
        builder.buildTarihSaat(tarih, saat);
        Mac mac = builder.getResult();
        
        // Then
        assertNotNull(mac);
        assertEquals(tarih, mac.getTarih());
        assertEquals(saat, mac.getSaat());
        assertEquals(2, mac.getMacTakimlari().size());
    }
    
    @Test
    @DisplayName("StandardMacBuilder ile detaylı maç oluşturulmalı")
    void testStandardBuilderDetayli() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        
        // When
        builder.buildTakimlar(galatasaray, fenerbahce);
        builder.buildTarihSaat(LocalDate.now(), LocalTime.of(20, 0));
        builder.buildStadyum(ttArena);
        builder.buildHakem(hakem);
        builder.buildLig(superLig);
        builder.buildOrganizasyon(tff);
        builder.buildNot("Test maçı");
        Mac mac = builder.getResult();
        
        // Then
        assertNotNull(mac);
        assertEquals(ttArena, mac.getStadyum());
        assertEquals(hakem, mac.getHakem());
        assertEquals(superLig, mac.getLig());
        assertEquals(tff, mac.getOrganizasyon());
        assertEquals("Test maçı", mac.getNot());
    }
    
    @Test
    @DisplayName("Builder reset sonrası yeni maç oluşturulabilmeli")
    void testBuilderReset() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        
        // First build
        builder.buildTakimlar(galatasaray, fenerbahce);
        builder.buildTarihSaat(LocalDate.now(), LocalTime.of(20, 0));
        Mac mac1 = builder.getResult(); // getResult otomatik reset yapar
        
        // Second build
        builder.buildTakimlar(besiktas, fenerbahce);
        builder.buildTarihSaat(LocalDate.now(), LocalTime.of(19, 0));
        Mac mac2 = builder.getResult();
        
        // Then
        assertNotNull(mac1);
        assertNotNull(mac2);
        assertNotSame(mac1, mac2);
    }
    
    @Test
    @DisplayName("Zorunlu alanlar eksikse exception fırlatılmalı")
    void testBuilderValidation() {
        MacBuilderInterface builder = new StandardMacBuilder();
        
        // Sadece takımlar var
        builder.buildTakimlar(galatasaray, fenerbahce);
        
        // Tarih/saat yok - exception bekleniyor
        assertThrows(IllegalStateException.class, builder::getResult);
    }
    
    // ================= DIRECTOR TESTLERİ =================
    
    @Test
    @DisplayName("Director ile basit lig maçı oluşturulmalı")
    void testDirectorLigMaci() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratLigMaci(
            galatasaray, 
            fenerbahce, 
            LocalDate.now(), 
            LocalTime.of(20, 0)
        );
        
        // Then
        assertNotNull(mac);
        assertEquals(2, mac.getMacTakimlari().size());
    }
    
    @Test
    @DisplayName("Director ile detaylı lig maçı oluşturulmalı")
    void testDirectorDetayliLigMaci() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratDetayliLigMaci(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0),
            ttArena,
            hakem,
            superLig
        );
        
        // Then
        assertNotNull(mac);
        assertEquals(ttArena, mac.getStadyum());
        assertEquals(hakem, mac.getHakem());
        assertEquals(superLig, mac.getLig());
    }
    
    @Test
    @DisplayName("Director ile tam kapsamlı maç oluşturulmalı")
    void testDirectorTamKapsamliMac() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratTamKapsamliMac(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0),
            ttArena,
            hakem,
            superLig,
            tff,
            "Önemli maç"
        );
        
        // Then
        assertNotNull(mac);
        assertEquals(ttArena, mac.getStadyum());
        assertEquals(hakem, mac.getHakem());
        assertEquals(superLig, mac.getLig());
        assertEquals(tff, mac.getOrganizasyon());
        assertEquals("Önemli maç", mac.getNot());
    }
    
    @Test
    @DisplayName("Director ile tamamlanmış maç oluşturulmalı")
    void testDirectorTamamlanmisMac() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratTamamlanmisMac(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0),
            2,
            1
        );
        
        // Then
        assertNotNull(mac);
        
        // Skorları kontrol et
        MacTakimlari evSahibiMT = mac.getMacTakimlari().stream()
            .filter(MacTakimlari::isEvSahibi)
            .findFirst()
            .orElseThrow();
        assertEquals(2, evSahibiMT.getSkor());
        
        MacTakimlari deplasmanMT = mac.getMacTakimlari().stream()
            .filter(mt -> !mt.isEvSahibi())
            .findFirst()
            .orElseThrow();
        assertEquals(1, deplasmanMT.getSkor());
    }
    
    @Test
    @DisplayName("Director ile derbi maçı oluşturulmalı")
    void testDirectorDerbiMaci() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratDerbiMaci(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0),
            ttArena,
            hakem
        );
        
        // Then
        assertNotNull(mac);
        assertTrue(mac.getNot().contains("DERBİ"));
    }
    
    @Test
    @DisplayName("Director ile şampiyonluk maçı oluşturulmalı")
    void testDirectorSampiyonlukMaci() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratSampiyonlukMaci(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0),
            ttArena,
            hakem,
            superLig
        );
        
        // Then
        assertNotNull(mac);
        assertTrue(mac.getNot().contains("ŞAMPİYONLUK"));
    }
    
    @Test
    @DisplayName("Director ile test maçı oluşturulmalı")
    void testDirectorTestMaci() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When
        Mac mac = director.yaratTestMaci(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0)
        );
        
        // Then
        assertNotNull(mac);
        assertTrue(mac.getNot().contains("TEST"));
    }
    
    @Test
    @DisplayName("Director builder değiştirebilmeli")
    void testDirectorBuilderDegistirme() {
        // Given
        MacBuilderInterface builder1 = new StandardMacBuilder();
        MacBuilderInterface builder2 = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder1);
        
        // When
        director.setBuilder(builder2);
        Mac mac = director.yaratLigMaci(
            galatasaray,
            fenerbahce,
            LocalDate.now(),
            LocalTime.of(20, 0)
        );
        
        // Then
        assertNotNull(mac);
    }
    
    @Test
    @DisplayName("Director aynı builder ile birden fazla maç oluşturabilmeli")
    void testDirectorMultipleMac() {
        // Given
        MacBuilderInterface builder = new StandardMacBuilder();
        MacDirector director = new MacDirector(builder);
        
        // When - Birden fazla maç oluştur
        Mac mac1 = director.yaratLigMaci(
            galatasaray, fenerbahce, 
            LocalDate.of(2025, 12, 25), LocalTime.of(20, 0)
        );
        
        Mac mac2 = director.yaratLigMaci(
            besiktas, fenerbahce,
            LocalDate.of(2025, 12, 26), LocalTime.of(19, 0)
        );
        
        // Then
        assertNotNull(mac1);
        assertNotNull(mac2);
        assertNotSame(mac1, mac2);
    }
}

