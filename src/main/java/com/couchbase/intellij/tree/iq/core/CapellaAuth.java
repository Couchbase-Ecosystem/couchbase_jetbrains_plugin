package com.couchbase.intellij.tree.iq.core;

public class CapellaAuth {
    private String jwt;
    private String tenant;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
