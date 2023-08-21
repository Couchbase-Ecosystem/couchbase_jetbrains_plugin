package com.couchbase.intellij.database;

public class PermissionChecker {
    private final Permissions permissions;

    public PermissionChecker(Permissions permissions) {
        this.permissions = permissions;
    }

    //TODO: Add Capella
    public boolean isClusterAdmin() {
        if (permissions == null || permissions.getRoles() == null) {
            return false;
        }

        for (Permissions.Role role: permissions.getRoles()) {
            if("cluster_admin".equals(role.getRole())) {
                return true;
            }
        }
        return false;
    }

    //TODO: Add Capella
    public boolean canManageBucket(String bucket) {
        if (permissions == null || permissions.getRoles() == null) {
            return false;
        }

        for (Permissions.Role role : permissions.getRoles()) {
            if ("bucket_admin".equals(role.getRole()) && (role.getBucketName().equals("*") || bucket.equals(role.getBucketName()))) {
                return true;
            }
        }
        return false;
    }

    public boolean canManageScopes(String bucket, String scope) {
        if (permissions == null || permissions.getRoles() == null) {
            return false;
        }

        for (Permissions.Role role : permissions.getRoles()) {
            if ("scope_admin".equals(role.getRole()) && (role.getBucketName().equals("*") || bucket.equals(role.getBucketName()))
                    && (role.getScopeName().equals("*") || scope.equals(role.getScopeName()))) {
                return true;
            }
        }
        return false;
    }

    public boolean canWrite(String bucket, String scope, String collection) {
        if (permissions == null || permissions.getRoles() == null) {
            return false;
        }

        for (Permissions.Role role : permissions.getRoles()) {
            if ("data_writer".equals(role.getRole()) &&
                    (role.getBucketName().equals("*") || bucket.equals(role.getBucketName())) &&
                    (role.getScopeName().equals("*") || scope.equals(role.getScopeName())) &&
                    (role.getCollectionName().equals("*") || collection.equals(role.getCollectionName()))) {
                return true;
            }
        }
        return false;
    }
}

