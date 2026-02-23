package com.footbase.service;

import com.footbase.entity.Kullanici;
import com.footbase.repository.KullaniciRepository;
import com.footbase.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> girisYap(String email, String sifre) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("E-posta adresi gereklidir");
        }
        if (sifre == null || sifre.trim().isEmpty()) {
            throw new RuntimeException("Şifre gereklidir");
        }

        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-posta veya şifre hatalı"));

        // Şifreyi kontrol et (veritabanında hash'lenmiş veya düz metin olabilir)
        // Önce hash'lenmiş mi kontrol et, değilse düz metin olarak karşılaştır
        boolean sifreDogru = false;
        if (kullanici.getSifre().startsWith("$2a$") || kullanici.getSifre().startsWith("$2b$")) {
            // BCrypt hash'lenmiş şifre
            sifreDogru = passwordEncoder.matches(sifre, kullanici.getSifre());
        } else {
            // Düz metin şifre (eski veritabanı için)
            sifreDogru = sifre.equals(kullanici.getSifre());
            // Eğer düz metin ise, hash'le ve güncelle
            if (sifreDogru) {
                kullanici.setSifre(passwordEncoder.encode(sifre));
                kullaniciRepository.save(kullanici);
            }
        }

        if (!sifreDogru) {
            throw new RuntimeException("E-posta veya şifre hatalı");
        }

        // JWT token oluştur
        String token = jwtUtil.generateToken(kullanici.getEmail(), kullanici.getId());

        // Sonuç map'i oluştur
        Map<String, Object> sonuc = new HashMap<>();
        sonuc.put("token", token);
        sonuc.put("kullanici", kullaniciBilgileriniMaple(kullanici));

        return sonuc;
    }

    public Map<String, Object> kayitOl(Kullanici kullanici) {
        // Validasyon kontrolleri
        if (kullanici.getEmail() == null || kullanici.getEmail().trim().isEmpty()) {
            throw new RuntimeException("E-posta adresi gereklidir");
        }
        if (kullanici.getKullaniciAdi() == null || kullanici.getKullaniciAdi().trim().isEmpty()) {
            throw new RuntimeException("Kullanıcı adı gereklidir");
        }
        if (kullanici.getSifre() == null || kullanici.getSifre().trim().isEmpty()) {
            throw new RuntimeException("Şifre gereklidir");
        }

        // E-posta kontrolü
        if (kullaniciRepository.existsByEmail(kullanici.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor");
        }

        // Kullanıcı adı kontrolü
        if (kullaniciRepository.existsByKullaniciAdi(kullanici.getKullaniciAdi())) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
        }

        // Şifreyi hash'le
        kullanici.setSifre(passwordEncoder.encode(kullanici.getSifre()));

        // Rol ataması (varsayılan USER)
        if (kullanici.getRol() == null || kullanici.getRol().isEmpty()) {
            kullanici.setRol("USER");
        }

        // Kullanıcıyı kaydet
        Kullanici kaydedilenKullanici = kullaniciRepository.save(kullanici);

        // JWT token oluştur
        String token = jwtUtil.generateToken(kaydedilenKullanici.getEmail(), kaydedilenKullanici.getId());

        // Sonuç map'i oluştur
        Map<String, Object> sonuc = new HashMap<>();
        sonuc.put("token", token);
        sonuc.put("kullanici", kullaniciBilgileriniMaple(kaydedilenKullanici));

        return sonuc;
    }

    public void sifreSifirla(String email, String yeniSifre) {
        Kullanici kullanici = kullaniciRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Yeni şifreyi hash'le ve kaydet
        kullanici.setSifre(passwordEncoder.encode(yeniSifre));
        kullaniciRepository.save(kullanici);
    }

    private Map<String, Object> kullaniciBilgileriniMaple(Kullanici kullanici) {
        Map<String, Object> kullaniciMap = new HashMap<>();
        kullaniciMap.put("id", kullanici.getId());
        kullaniciMap.put("email", kullanici.getEmail());
        kullaniciMap.put("kullaniciAdi", kullanici.getKullaniciAdi());
        kullaniciMap.put("rol", kullanici.getRol());
        kullaniciMap.put("admin",
                kullanici.getAdmin() != null ? kullanici.getAdmin() : "ADMIN".equals(kullanici.getRol()));
        // Ad ve soyad kullaniciAdi'den türetilebilir (şimdilik null)
        kullaniciMap.put("ad", kullanici.getAd() != null ? kullanici.getAd() : "");
        kullaniciMap.put("soyad", kullanici.getSoyad() != null ? kullanici.getSoyad() : "");
        kullaniciMap.put("profilResmi", kullanici.getProfilResmi() != null ? kullanici.getProfilResmi() : "");
        return kullaniciMap;
    }
}
