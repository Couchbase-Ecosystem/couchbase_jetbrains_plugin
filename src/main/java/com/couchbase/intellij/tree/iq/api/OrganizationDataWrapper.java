package com.couchbase.intellij.tree.iq.api;

import java.util.Map;

public class OrganizationDataWrapper {

    OrganizationData data;
    Map<String, Resource> resources;
    Permissions permissions;

    public OrganizationData getData() {
        return data;
    }

    public void setData(OrganizationData data) {
        this.data = data;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public void setResources(Map<String, Resource> resources) {
        this.resources = resources;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}
