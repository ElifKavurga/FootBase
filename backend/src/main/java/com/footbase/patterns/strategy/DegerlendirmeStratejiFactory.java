package com.footbase.patterns.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DegerlendirmeStratejiFactory {

    private static final Logger logger = LoggerFactory.getLogger(DegerlendirmeStratejiFactory.class);

    @Autowired
    private AdminDegerlendirmeStrateji adminStrateji;

    @Autowired
    private EditorDegerlendirmeStrateji editorStrateji;

    @Autowired
    private NormalKullaniciDegerlendirmeStrateji normalKullaniciStrateji;

    public DegerlendirmeStratejiFactory() {
        logger.info("DegerlendirmeStratejiFactory olusturuldu (Strategy + Factory Pattern)");
    }

    public DegerlendirmeStrateji getStrateji(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            logger.error("Rol bos olamaz!");
            throw new IllegalArgumentException("Rol bos olamaz!");
        }

        String normalizedRol = rol.toUpperCase().trim();

        DegerlendirmeStrateji strateji = switch (normalizedRol) {
            case "ADMIN", "YONETICI" -> {
                logger.debug("Admin stratejisi donduruluyor");
                yield adminStrateji;
            }
            case "EDITOR", "EDITR", "EDIT\u00D6R" -> {
                logger.debug("Editor stratejisi donduruluyor");
                yield editorStrateji;
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.debug("Normal kullanici stratejisi donduruluyor");
                yield normalKullaniciStrateji;
            }
            default -> {
                logger.error("Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Gecersiz rol: " + rol);
            }
        };

        logger.info("Strateji secildi: {} (Agirlik: {}x)", strateji.getStratejAdi(), strateji.getAgirlik());
        return strateji;
    }

    public java.util.List<DegerlendirmeStrateji> tumStratejiler() {
        return java.util.Arrays.asList(adminStrateji, editorStrateji, normalKullaniciStrateji);
    }

    public java.util.List<String> kullanilabilirRoller() {
        return java.util.Arrays.asList("ADMIN", "EDITOR", "USER");
    }

    public boolean isValidRol(String rol) {
        if (rol == null) {
            return false;
        }

        String normalizedRol = rol.toUpperCase().trim();

        return normalizedRol.equals("ADMIN")
                || normalizedRol.equals("YONETICI")
                || normalizedRol.equals("EDITOR")
                || normalizedRol.equals("EDITR")
                || normalizedRol.equals("EDIT\u00D6R")
                || normalizedRol.equals("USER")
                || normalizedRol.equals("KULLANICI")
                || normalizedRol.equals("NORMAL");
    }
}
