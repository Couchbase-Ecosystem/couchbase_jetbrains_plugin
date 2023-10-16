package com.couchbase.intellij.tree.iq.api;

import java.util.List;

public class OrganizationResponse {
    List<OrganizationDataWrapper> data;
    Permissions permissions;

    public List<OrganizationDataWrapper> getData() {
        return data;
    }

    public void setData(List<OrganizationDataWrapper> data) {
        this.data = data;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}
