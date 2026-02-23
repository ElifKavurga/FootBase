package com.footbase.patterns.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Factory Pattern Test Sınıfı
 * 
 * KullaniciFactory'nin doğru çalıştığını test eder.
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
class KullaniciFactoryTest {
    
    @Test
    @DisplayName("Admin kullanıcı oluşturulmalı")
    void testCreateAdminKullanici() {
        // Given
        String rol = "ADMIN";
        
        // When
        Kullanici kullanici = KullaniciFactory.createKullanici(rol);
        
        // Then
        assertNotNull(kullanici);
        assertEquals("ADMIN", kullanici.getRole());
        assertTrue(kullanici instanceof AdminKullanici);
        assertTrue(kullanici.hasPermission("MATCH_APPROVE"));
        assertTrue(kullanici.hasPermission("USER_MANAGE"));
    }
    
    @Test
    @DisplayName("Editor kullanıcı oluşturulmalı")
    void testCreateEditorKullanici() {
        // Given
        String rol = "EDITOR";
        
        // When
        Kullanici kullanici = KullaniciFactory.createKullanici(rol);
        
        // Then
        assertNotNull(kullanici);
        assertEquals("EDITOR", kullanici.getRole());
        assertTrue(kullanici instanceof EditorKullanici);
        assertTrue(kullanici.hasPermission("MATCH_CREATE"));
        assertFalse(kullanici.hasPermission("MATCH_APPROVE"));
    }
    
    @Test
    @DisplayName("Normal kullanıcı oluşturulmalı")
    void testCreateNormalKullanici() {
        // Given
        String rol = "USER";
        
        // When
        Kullanici kullanici = KullaniciFactory.createKullanici(rol);
        
        // Then
        assertNotNull(kullanici);
        assertEquals("USER", kullanici.getRole());
        assertTrue(kullanici instanceof NormalKullanici);
        assertTrue(kullanici.hasPermission("COMMENT_ADD"));
        assertFalse(kullanici.hasPermission("MATCH_CREATE"));
    }
    
    @Test
    @DisplayName("İsimle kullanıcı oluşturulmalı")
    void testCreateKullaniciWithName() {
        // Given
        String rol = "ADMIN";
        String name = "Ahmet Admin";
        
        // When
        Kullanici kullanici = KullaniciFactory.createKullanici(rol, name);
        
        // Then
        assertNotNull(kullanici);
        assertEquals("ADMIN", kullanici.getRole());
        assertEquals(name, kullanici.getDisplayName());
    }
    
    @Test
    @DisplayName("Geçersiz rol için exception fırlatılmalı")
    void testInvalidRole() {
        // Given
        String invalidRole = "INVALID_ROLE";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            KullaniciFactory.createKullanici(invalidRole);
        });
    }
    
    @Test
    @DisplayName("Null rol için exception fırlatılmalı")
    void testNullRole() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            KullaniciFactory.createKullanici(null);
        });
    }
    
    @Test
    @DisplayName("Boş rol için exception fırlatılmalı")
    void testEmptyRole() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            KullaniciFactory.createKullanici("");
        });
    }
    
    @Test
    @DisplayName("Türkçe roller de çalışmalı")
    void testTurkishRoles() {
        // Test YONETICI
        Kullanici yonetici = KullaniciFactory.createKullanici("YONETICI");
        assertEquals("ADMIN", yonetici.getRole());
        
        // Test EDITÖR
        Kullanici editor = KullaniciFactory.createKullanici("EDITÖR");
        assertEquals("EDITOR", editor.getRole());
        
        // Test KULLANICI
        Kullanici kullanici = KullaniciFactory.createKullanici("KULLANICI");
        assertEquals("USER", kullanici.getRole());
    }
    
    @Test
    @DisplayName("Büyük/küçük harf duyarlı olmamalı")
    void testCaseInsensitive() {
        Kullanici admin1 = KullaniciFactory.createKullanici("ADMIN");
        Kullanici admin2 = KullaniciFactory.createKullanici("admin");
        Kullanici admin3 = KullaniciFactory.createKullanici("Admin");
        
        assertEquals("ADMIN", admin1.getRole());
        assertEquals("ADMIN", admin2.getRole());
        assertEquals("ADMIN", admin3.getRole());
    }
    
    @Test
    @DisplayName("Admin tüm yetkilere sahip olmalı")
    void testAdminHasAllPermissions() {
        Kullanici admin = KullaniciFactory.createKullanici("ADMIN");
        List<String> permissions = admin.getPermissions();
        
        // Admin çok sayıda yetkiye sahip olmalı
        assertTrue(permissions.size() > 15);
        
        // Kritik yetkiler kontrol et
        assertTrue(admin.hasPermission("MATCH_APPROVE"));
        assertTrue(admin.hasPermission("USER_MANAGE"));
        assertTrue(admin.hasPermission("SYSTEM_SETTINGS"));
    }
    
    @Test
    @DisplayName("Editor sınırlı yetkilere sahip olmalı")
    void testEditorHasLimitedPermissions() {
        Kullanici editor = KullaniciFactory.createKullanici("EDITOR");
        
        // Editor'ün olması gereken yetkiler
        assertTrue(editor.hasPermission("MATCH_CREATE"));
        assertTrue(editor.hasPermission("MATCH_UPDATE_SCORE"));
        assertTrue(editor.hasPermission("MATCH_ADD_EVENT"));
        
        // Editor'ün olmaması gereken yetkiler
        assertFalse(editor.hasPermission("MATCH_APPROVE"));
        assertFalse(editor.hasPermission("USER_MANAGE"));
        assertFalse(editor.hasPermission("SYSTEM_SETTINGS"));
    }
    
    @Test
    @DisplayName("User minimum yetkilere sahip olmalı")
    void testUserHasMinimalPermissions() {
        Kullanici user = KullaniciFactory.createKullanici("USER");
        
        // User'ın olması gereken yetkiler
        assertTrue(user.hasPermission("MATCH_VIEW"));
        assertTrue(user.hasPermission("COMMENT_ADD"));
        assertTrue(user.hasPermission("PLAYER_RATE"));
        
        // User'ın olmaması gereken yetkiler
        assertFalse(user.hasPermission("MATCH_CREATE"));
        assertFalse(user.hasPermission("MATCH_APPROVE"));
        assertFalse(user.hasPermission("USER_MANAGE"));
    }
    
    @Test
    @DisplayName("Login metodu çalışmalı")
    void testLoginMethod() {
        // Bu test sadece exception fırlatmamasını kontrol eder
        Kullanici admin = KullaniciFactory.createKullanici("ADMIN");
        assertDoesNotThrow(() -> admin.login());
        
        Kullanici editor = KullaniciFactory.createKullanici("EDITOR");
        assertDoesNotThrow(() -> editor.login());
        
        Kullanici user = KullaniciFactory.createKullanici("USER");
        assertDoesNotThrow(() -> user.login());
    }
    
    @Test
    @DisplayName("Kullanılabilir roller listesi doğru olmalı")
    void testAvailableRoles() {
        List<String> roles = KullaniciFactory.getAvailableRoles();
        
        assertEquals(3, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("EDITOR"));
        assertTrue(roles.contains("USER"));
    }
    
    @Test
    @DisplayName("Rol geçerliliği doğru kontrol edilmeli")
    void testIsValidRole() {
        assertTrue(KullaniciFactory.isValidRole("ADMIN"));
        assertTrue(KullaniciFactory.isValidRole("EDITOR"));
        assertTrue(KullaniciFactory.isValidRole("USER"));
        assertTrue(KullaniciFactory.isValidRole("YONETICI"));
        assertTrue(KullaniciFactory.isValidRole("EDITÖR"));
        
        assertFalse(KullaniciFactory.isValidRole("INVALID"));
        assertFalse(KullaniciFactory.isValidRole(null));
        assertFalse(KullaniciFactory.isValidRole(""));
    }
}

