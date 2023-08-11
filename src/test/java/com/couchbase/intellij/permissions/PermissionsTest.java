package com.couchbase.intellij.permissions;

import com.couchbase.intellij.database.PermissionChecker;
import com.couchbase.intellij.database.Permissions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
public class PermissionsTest {
    @Test
    public void testIsClusterAdmin() throws Exception {
        String response = getCode("cluster_admin.json");
        Permissions permissions = parsePermissions(response);
        PermissionChecker permissionChecker = new PermissionChecker(permissions);

        Assertions.assertTrue(permissionChecker.isClusterAdmin());
    }

    @Test
    public void testCanManageBucket() throws Exception {
        String response = getCode("bucket_admin.json");
        Permissions permissions = parsePermissions(response);
        PermissionChecker permissionChecker = new PermissionChecker(permissions);

        Assertions.assertTrue(permissionChecker.canManageBucket("travel-sample"));
    }

    @Test
    public void testCanManageScopes() throws Exception {
        String response = getCode("scope_admin.json");
        Permissions permissions = parsePermissions(response);
        PermissionChecker permissionChecker = new PermissionChecker(permissions);

        Assertions.assertTrue(permissionChecker.canManageScopes("travel-sample", "inventory"));
    }

    @Test
    public void testCanWrite() throws Exception {
        String response = getCode("data_writer.json");
        Permissions permissions = parsePermissions(response);
        PermissionChecker permissionChecker = new PermissionChecker(permissions);

        Assertions.assertTrue(permissionChecker.canWrite("travel-sample", "inventory", "*"));
    }

    private String getCode(String fileName) throws Exception {
        return new String(PermissionsTest.class.getClassLoader().getResourceAsStream("permissions_responses/" + fileName).readAllBytes());
    }

    private Permissions parsePermissions(String json) throws Exception {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(json, Permissions.class);
    }
}
