package com.couchbase.intellij.tree.iq.api;

public class OrganizationData {

    String id;
    String name;
    String description;
    String website;
    Preferences preferences;
    String createdByUserID;
    String upsertedByUserID;
    String createdAt;
    String upsertedAt;
    String modifiedByUserID;
    String modifiedAt;
    int version;
    String createdBy;
    String modifiedBy;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getCreatedByUserID() {
        return createdByUserID;
    }

    public void setCreatedByUserID(String createdByUserID) {
        this.createdByUserID = createdByUserID;
    }

    public String getUpsertedByUserID() {
        return upsertedByUserID;
    }

    public void setUpsertedByUserID(String upsertedByUserID) {
        this.upsertedByUserID = upsertedByUserID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpsertedAt() {
        return upsertedAt;
    }

    public void setUpsertedAt(String upsertedAt) {
        this.upsertedAt = upsertedAt;
    }

    public String getModifiedByUserID() {
        return modifiedByUserID;
    }

    public void setModifiedByUserID(String modifiedByUserID) {
        this.modifiedByUserID = modifiedByUserID;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
