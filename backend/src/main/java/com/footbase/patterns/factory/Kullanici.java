package com.footbase.patterns.factory;

import java.util.List;

public interface Kullanici {

    void login();

    List<String> getPermissions();

    String getRole();

    String getDisplayName();

    default boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }
}
