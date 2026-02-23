package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KullaniciFactory {

    private static final Logger logger = LoggerFactory.getLogger(KullaniciFactory.class);

    public static Kullanici createKullanici(String rol) {
        logger.info("Factory: Kullanici olusturuluyor - Rol: {}", rol);

        if (rol == null || rol.trim().isEmpty()) {
            logger.error("Gecersiz rol: null veya bos");
            throw new IllegalArgumentException("Rol bos olamaz!");
        }

        return switch (rol.toUpperCase()) {
            case "ADMIN", "YONETICI" -> {
                logger.info("AdminKullanici olusturuldu");
                yield new AdminKullanici();
            }
            case "EDITOR", "EDIT\u00D6R" -> {
                logger.info("EditorKullanici olusturuldu");
                yield new EditorKullanici();
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("NormalKullanici olusturuldu");
                yield new NormalKullanici();
            }
            default -> {
                logger.error("Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Gecersiz rol: " + rol);
            }
        };
    }

    public static Kullanici createKullanici(String rol, String displayName) {
        logger.info("Factory: Kullanici olusturuluyor - Rol: {}, Isim: {}", rol, displayName);

        if (rol == null || rol.trim().isEmpty()) {
            logger.error("Gecersiz rol: null veya bos");
            throw new IllegalArgumentException("Rol bos olamaz!");
        }

        return switch (rol.toUpperCase()) {
            case "ADMIN", "YONETICI" -> {
                logger.info("AdminKullanici olusturuldu: {}", displayName);
                yield new AdminKullanici(displayName);
            }
            case "EDITOR", "EDIT\u00D6R" -> {
                logger.info("EditorKullanici olusturuldu: {}", displayName);
                yield new EditorKullanici(displayName);
            }
            case "USER", "KULLANICI", "NORMAL" -> {
                logger.info("NormalKullanici olusturuldu: {}", displayName);
                yield new NormalKullanici(displayName);
            }
            default -> {
                logger.error("Bilinmeyen rol: {}", rol);
                throw new IllegalArgumentException("Gecersiz rol: " + rol);
            }
        };
    }

    public static Kullanici fromEntity(com.footbase.entity.Kullanici kullaniciEntity) {
        if (kullaniciEntity == null) {
            throw new IllegalArgumentException("Kullanici entity null olamaz!");
        }

        String displayName = kullaniciEntity.getEmail();
        String rol = kullaniciEntity.getRol() != null ? kullaniciEntity.getRol() : "USER";

        logger.info("Entity'den Factory kullanici olusturuluyor: {} ({})", displayName, rol);

        return createKullanici(rol, displayName);
    }

    public static java.util.List<String> getAvailableRoles() {
        return java.util.Arrays.asList("ADMIN", "EDITOR", "USER");
    }

    public static boolean isValidRole(String rol) {
        if (rol == null) {
            return false;
        }

        return getAvailableRoles().contains(rol.toUpperCase())
                || rol.equalsIgnoreCase("YONETICI")
                || rol.equalsIgnoreCase("EDIT\u00D6R")
                || rol.equalsIgnoreCase("KULLANICI");
    }
}
