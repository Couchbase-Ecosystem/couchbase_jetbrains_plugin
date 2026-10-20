package com.couchbase.intellij.tree.cblite;

import org.junit.Assert;
import org.junit.Test;

/**
 * Only the argument validation of CBLDataLoader.saveNewDatabase, which runs before any
 * IDE application service (CBLDatabaseStorage) is touched.
 */
public class CBLDataLoaderValidationTest {

    @Test
    public void nullIdIsRejectedFirst() {
        IllegalStateException ex = Assert.assertThrows(IllegalStateException.class,
                () -> CBLDataLoader.saveNewDatabase(null, null, null));

        Assert.assertEquals("The database id can't be null", ex.getMessage());
    }

    @Test
    public void nullNameIsRejected() {
        IllegalStateException ex = Assert.assertThrows(IllegalStateException.class,
                () -> CBLDataLoader.saveNewDatabase("id", null, "/some/path"));

        Assert.assertEquals("The database name can't be null", ex.getMessage());
    }

    @Test
    public void nullPathIsRejected() {
        IllegalStateException ex = Assert.assertThrows(IllegalStateException.class,
                () -> CBLDataLoader.saveNewDatabase("id", "name", null));

        Assert.assertEquals("The path can't be null", ex.getMessage());
    }
}
