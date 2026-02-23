package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class NormalKullanici implements Kullanici {

    private static final Logger logger = LoggerFactory.getLogger(NormalKullanici.class);
    private String displayName;

    public NormalKullanici() {
        this.displayName = "User";
        logger.info("NormalKullanici olusturuldu (Factory Pattern)");
    }

    public NormalKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("NormalKullanici olusturuldu: {}", displayName);
    }

    @Override
    public void login() {
        logger.info("Kullanici giris yapti: {}", displayName);
        logger.debug("Ana sayfa yukleniyor...");
        logger.debug("Son maclar getiriliyor...");
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(
                "MATCH_VIEW",
                "PLAYER_VIEW",
                "TEAM_VIEW",
                "STATS_VIEW",
                "COMMENT_ADD",
                "COMMENT_EDIT_OWN",
                "COMMENT_DELETE_OWN",
                "PLAYER_RATE",
                "MATCH_PREDICT",
                "PROFILE_EDIT_OWN",
                "VIEW_OWN_ACTIVITY");
    }

    @Override
    public String getRole() {
        return "USER";
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
        return "NormalKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}
