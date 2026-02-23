package com.footbase.patterns.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class EditorKullanici implements Kullanici {

    private static final Logger logger = LoggerFactory.getLogger(EditorKullanici.class);
    private String displayName;

    public EditorKullanici() {
        this.displayName = "Editor";
        logger.info("EditorKullanici olusturuldu (Factory Pattern)");
    }

    public EditorKullanici(String displayName) {
        this.displayName = displayName;
        logger.info("EditorKullanici olusturuldu: {}", displayName);
    }

    @Override
    public void login() {
        logger.info("Editor giris yapti: {}", displayName);
        logger.debug("Editor paneli yukleniyor...");
        logger.debug("Bekleyen maclar kontrol ediliyor...");
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(
                "MATCH_CREATE",
                "MATCH_EDIT_OWN",
                "MATCH_VIEW_OWN",
                "MATCH_UPDATE_SCORE",
                "MATCH_ADD_EVENT",
                "MATCH_START",
                "MATCH_FINISH",
                "COMMENT_ADD",
                "VIEW_OWN_STATS");
    }

    @Override
    public String getRole() {
        return "EDITOR";
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
        return "EditorKullanici{" +
                "displayName='" + displayName + '\'' +
                ", role='" + getRole() + '\'' +
                ", permissions=" + getPermissions().size() +
                '}';
    }
}
