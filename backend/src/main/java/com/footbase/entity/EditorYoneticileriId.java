package com.footbase.entity;

import java.io.Serializable;
import java.util.Objects;


public class EditorYoneticileriId implements Serializable {

    private Long editorId;
    private Long adminId;

    public EditorYoneticileriId() {
    }

    public EditorYoneticileriId(Long editorId, Long adminId) {
        this.editorId = editorId;
        this.adminId = adminId;
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditorYoneticileriId that = (EditorYoneticileriId) o;
        return Objects.equals(editorId, that.editorId) &&
               Objects.equals(adminId, that.adminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(editorId, adminId);
    }
}

