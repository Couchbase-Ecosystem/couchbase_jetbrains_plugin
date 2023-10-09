package com.couchbase.intellij.tree.iq.api;

import java.util.List;

public class User {

    private String id;
    private String name;
    private String email;
    private List<Address> addresses;
    private String lastLoginTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public void setLastLoginTimestamp(String lastLoginTimestamp) {
        this.lastLoginTimestamp = lastLoginTimestamp;
    }
}
