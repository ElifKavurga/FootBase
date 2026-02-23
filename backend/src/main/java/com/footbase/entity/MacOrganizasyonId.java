package com.footbase.entity;

import java.io.Serializable;
import java.util.Objects;

public class MacOrganizasyonId implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long macId;
    private Long organizasyonId;

    public MacOrganizasyonId() {
    }

    public MacOrganizasyonId(Long macId, Long organizasyonId) {
        this.macId = macId;
        this.organizasyonId = organizasyonId;
    }

    public Long getMacId() {
        return macId;
    }

    public void setMacId(Long macId) {
        this.macId = macId;
    }

    public Long getOrganizasyonId() {
        return organizasyonId;
    }

    public void setOrganizasyonId(Long organizasyonId) {
        this.organizasyonId = organizasyonId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MacOrganizasyonId that = (MacOrganizasyonId) o;
        return Objects.equals(macId, that.macId) && Objects.equals(organizasyonId, that.organizasyonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macId, organizasyonId);
    }
}
