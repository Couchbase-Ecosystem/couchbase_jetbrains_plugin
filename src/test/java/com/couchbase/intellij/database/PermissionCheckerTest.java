package com.couchbase.intellij.database;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PermissionCheckerTest {

    private static Permissions.Role role(String role, String bucket, String scope, String collection) {
        Permissions.Role r = new Permissions.Role();
        r.setRole(role);
        r.setBucketName(bucket);
        r.setScopeName(scope);
        r.setCollectionName(collection);
        return r;
    }

    private static PermissionChecker checker(Permissions.Role... roles) {
        Permissions p = new Permissions();
        p.setRoles(Arrays.asList(roles));
        return new PermissionChecker(p);
    }

    @Test
    public void nullPermissionsDenyEverything() {
        PermissionChecker c = new PermissionChecker(null);
        assertFalse(c.isClusterAdmin());
        assertFalse(c.canManageBucket("b"));
        assertFalse(c.canManageScopes("b", "s"));
        assertFalse(c.canWrite("b", "s", "c"));
    }

    @Test
    public void nullRolesDenyEverything() {
        assertFalse(new PermissionChecker(new Permissions()).isClusterAdmin());
    }

    @Test
    public void clusterAdminIsDetected() {
        assertTrue(checker(role("cluster_admin", null, null, null)).isClusterAdmin());
        assertFalse(checker(role("bucket_admin", "b", null, null)).isClusterAdmin());
    }

    @Test
    public void bucketAdminMatchesOnlyItsBucketUnlessWildcard() {
        assertTrue(checker(role("bucket_admin", "b", null, null)).canManageBucket("b"));
        assertFalse(checker(role("bucket_admin", "b", null, null)).canManageBucket("other"));
        assertTrue(checker(role("bucket_admin", "*", null, null)).canManageBucket("anything"));
    }

    @Test
    public void clusterAdminDoesNotImplyBucketManagement() {
        // Current behavior (see TODO "Add Capella" in PermissionChecker): only bucket_admin grants bucket management.
        assertFalse(checker(role("cluster_admin", null, null, null)).canManageBucket("b"));
    }

    @Test
    public void scopeAdminChecksBucketAndScope() {
        PermissionChecker c = checker(role("scope_admin", "b", "s", null));
        assertTrue(c.canManageScopes("b", "s"));
        assertFalse(c.canManageScopes("b", "other"));
        assertFalse(c.canManageScopes("other", "s"));
        assertTrue(checker(role("scope_admin", "*", "*", null)).canManageScopes("x", "y"));
    }

    @Test
    public void dataWriterChecksAllThreeLevelsWithWildcards() {
        assertTrue(checker(role("data_writer", "b", "s", "c")).canWrite("b", "s", "c"));
        assertFalse(checker(role("data_writer", "b", "s", "c")).canWrite("b", "s", "other"));
        assertFalse(checker(role("data_writer", "b", "other", "c")).canWrite("b", "s", "c"));
        assertTrue(checker(role("data_writer", "b", "s", "*")).canWrite("b", "s", "anything"));
        assertFalse(checker(role("bucket_admin", "b", "s", "c")).canWrite("b", "s", "c"));
    }

    @Test
    public void dataWriterWithNullCollectionNameThrowsNullPointerException() {
        // NOTE: possible bug: canWrite dereferences role.getCollectionName() without a null check, so a
        // data_writer role with no collection name throws instead of being treated as "no access".
        try {
            checker(role("data_writer", "b", "s", null)).canWrite("b", "s", "c");
            fail("expected NullPointerException");
        } catch (NullPointerException expected) {
            // pinned current behavior
        }
    }
}
