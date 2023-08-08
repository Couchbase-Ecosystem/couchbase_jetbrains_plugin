package com.couchbase.intellij.database;
import java.util.List;

public class Permissions {
    private List<Role> roles;
    private String id;
    private String domain;
    private String password_change_date;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPasswordChangeDate() {
        return password_change_date;
    }

    public void setPasswordChangeDate(String password_change_date) {
        this.password_change_date = password_change_date;
    }

    public static class Role {
        private String role;
        private String bucket_name;
        private String scope_name;
        private String collection_name;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getBucketName() {
            return bucket_name;
        }

        public void setBucketName(String bucket_name) {
            this.bucket_name = bucket_name;
        }

        public String getScopeName() {
            return scope_name;
        }

        public void setScopeName(String scope_name) {
            this.scope_name = scope_name;
        }

        public String getCollectionName() {
            return collection_name;
        }

        public void setCollectionName(String collection_name) {
            this.collection_name = collection_name;
        }
    }
}