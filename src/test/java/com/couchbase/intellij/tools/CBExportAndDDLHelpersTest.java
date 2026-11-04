package com.couchbase.intellij.tools;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Private, side-effect-free helpers of CBExport and DDLExport, reached via reflection.
 */
public class CBExportAndDDLHelpersTest {

    private static Object invokeStatic(Class<?> owner, String name, Class<?>[] types, Object... args) throws Exception {
        Method m = owner.getDeclaredMethod(name, types);
        m.setAccessible(true);
        try {
            return m.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    // ---- CBExport.hasAnyScope ----

    private static boolean hasAnyScope(String collection, List<String> scopes) throws Exception {
        return (Boolean) invokeStatic(CBExport.class, "hasAnyScope", new Class<?>[]{String.class, List.class},
                collection, scopes);
    }

    @Test
    public void hasAnyScopeMatchesDotPrefixedCollectionPath() throws Exception {
        Assert.assertTrue(hasAnyScope("inventory.hotel", Arrays.asList("inventory")));
    }

    @Test
    public void hasAnyScopeRequiresExactScopeBeforeDot() throws Exception {
        Assert.assertFalse(hasAnyScope("inventoryX.hotel", Collections.singletonList("inventory")));
    }

    @Test
    public void hasAnyScopeFalseForBareCollectionOrEmptyScopes() throws Exception {
        Assert.assertFalse(hasAnyScope("hotel", Collections.singletonList("inventory")));
        Assert.assertFalse(hasAnyScope("inventory.hotel", Collections.emptyList()));
    }

    @Test
    public void hasAnyScopeMatchesAnyOfSeveralScopes() throws Exception {
        Assert.assertTrue(hasAnyScope("b.c", Arrays.asList("a", "b")));
    }

    // ---- CBExport.getEncodedIndexes ----

    @Test
    public void getEncodedIndexesEmptyListIsEmptyBase64() throws Exception {
        Object encoded = invokeStatic(CBExport.class, "getEncodedIndexes", new Class<?>[]{List.class},
                Collections.emptyList());

        Assert.assertEquals("", encoded);
    }

    // ---- DDLExport.getScopesFileName ----

    private static String scopesFileName(List<String> scopes) throws Exception {
        return (String) invokeStatic(DDLExport.class, "getScopesFileName", new Class<?>[]{List.class}, scopes);
    }

    @Test
    public void singleScopeFileNameUsesScopeName() throws Exception {
        Assert.assertEquals("inventory_", scopesFileName(Collections.singletonList("inventory")));
    }

    @Test
    public void multipleScopesFileNameUsesCount() throws Exception {
        Assert.assertEquals("2_scopes_", scopesFileName(Arrays.asList("a", "b")));
    }

    // NOTE: possible bug: an empty selection produces "0_scopes_" in the file name instead of being rejected.
    @Test
    public void emptyScopeListFileNameIsZeroScopes() throws Exception {
        Assert.assertEquals("0_scopes_", scopesFileName(Collections.emptyList()));
    }
}
