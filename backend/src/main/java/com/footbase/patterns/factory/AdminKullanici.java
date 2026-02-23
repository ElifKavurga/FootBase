package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AdminKullanici implements Kullanici {

    private static final Logger logger = LoggerFactory.getLogger(AdminKullanici.class);
    private String displayName;

    public AdminKullanici() {
        this.displayName = "Admin";
        logger.info("AdminKullanici olusturuldu (Factory Pattern)");
    }

    public AdminKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("AdminKullanici olusturuldu: {}", displayName);
    }

    @Override
    public void login() {
        logger.info("Admin giris yapti: {}", displayName);
        logger.debug("Admin paneli yukleniyor...");
        logger.debug("Onay bekleyen maclar kontrol ediliyor...");
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(
                "MATCH_CREATE",
                "MATCH_EDIT",
                "MATCH_DELETE",
                "MATCH_APPROVE",
                "MATCH_REJECT",
                "MATCH_PUBLISH",
                "USER_CREATE",
                "USER_EDIT",
                "USER_DELETE",
                "USER_ASSIGN_ROLE",
                "EDITOR_ASSIGN",
                "EDITOR_MANAGE",
                "TEAM_MANAGE",
                "PLAYER_MANAGE",
                "COMMENT_MODERATE",
                "COMMENT_DELETE",
                "SYSTEM_SETTINGS",
                "VIEW_ANALYTICS",
                "VIEW_LOGS");
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "AdminKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}
