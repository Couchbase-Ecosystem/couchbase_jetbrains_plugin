package com.couchbase.intellij.listener;

import com.couchbase.intellij.workbench.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DependenciesUtilTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void captureLog() {
        Log.setPrinter((message, type) -> { /* swallow log output, avoids needing an IDE console */ });
    }

    @After
    public void resetLog() {
        Log.setPrinter(null);
    }

    @Test
    public void createVersioningFileWritesEmptyKeys() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();

        DependenciesUtil.createVersioningFile(dir);

        assertTrue(new File(dir, DependenciesUtil.VERSION_FILE).exists());
        // blank values are reported as null by getPropertyValue
        assertNull(DependenciesUtil.getPropertyValue(dir, DependenciesUtil.TOOLS_KEY));
        assertNull(DependenciesUtil.getPropertyValue(dir, DependenciesUtil.SHELL_KEY));
        assertNull(DependenciesUtil.getPropertyValue(dir, DependenciesUtil.EXPLAIN_KEY));
    }

    @Test
    public void createVersioningFileDoesNotOverwriteExisting() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();
        DependenciesUtil.setPropertyValue(dir, DependenciesUtil.SHELL_KEY, "4");

        DependenciesUtil.createVersioningFile(dir);

        assertEquals("4", DependenciesUtil.getPropertyValue(dir, DependenciesUtil.SHELL_KEY));
    }

    @Test
    public void setThenGetPropertyRoundTrips() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();

        DependenciesUtil.setPropertyValue(dir, DependenciesUtil.CBMIGRATE_KEY, DependenciesUtil.CBMIGRATE_VERSION);

        assertEquals(DependenciesUtil.CBMIGRATE_VERSION, DependenciesUtil.getPropertyValue(dir, DependenciesUtil.CBMIGRATE_KEY));
    }

    @Test
    public void setPropertyKeepsOtherKeys() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();
        DependenciesUtil.setPropertyValue(dir, DependenciesUtil.TOOLS_KEY, DependenciesUtil.TOOLS_VERSION);
        DependenciesUtil.setPropertyValue(dir, DependenciesUtil.EXPLAIN_KEY, DependenciesUtil.EXPLAIN_VERSION);

        assertEquals(DependenciesUtil.TOOLS_VERSION, DependenciesUtil.getPropertyValue(dir, DependenciesUtil.TOOLS_KEY));
        assertEquals(DependenciesUtil.EXPLAIN_VERSION, DependenciesUtil.getPropertyValue(dir, DependenciesUtil.EXPLAIN_KEY));
    }

    @Test
    public void getPropertyFromMissingFileIsNull() {
        assertNull(DependenciesUtil.getPropertyValue(tmp.getRoot().getAbsolutePath(), "anything"));
    }

    @Test
    public void unknownPropertyIsNull() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();
        DependenciesUtil.createVersioningFile(dir);

        assertNull(DependenciesUtil.getPropertyValue(dir, "does_not_exist"));
    }

    @Test
    public void whitespaceOnlyValueIsNull() throws Exception {
        String dir = tmp.getRoot().getAbsolutePath();
        DependenciesUtil.setPropertyValue(dir, "k", "   ");

        assertNull(DependenciesUtil.getPropertyValue(dir, "k"));
    }

    @Test
    public void setPropertyInMissingDirectoryThrows() {
        String missing = new File(tmp.getRoot(), "no/such/dir").getAbsolutePath();
        try {
            DependenciesUtil.setPropertyValue(missing, "k", "v");
            org.junit.Assert.fail("expected IOException");
        } catch (Exception expected) {
            assertTrue(expected instanceof IOException);
        }
    }

    @Test
    public void deleteFolderRemovesTreeRecursively() throws IOException {
        File root = tmp.newFolder("victim");
        File nested = new File(root, "a/b");
        assertTrue(nested.mkdirs());
        Files.writeString(new File(nested, "f.txt").toPath(), "x");

        DependenciesUtil.deleteFolder(root.getAbsolutePath());

        assertFalse(root.exists());
    }

    @Test
    public void deleteFolderOnMissingPathIsNoOp() {
        DependenciesUtil.deleteFolder(new File(tmp.getRoot(), "missing").getAbsolutePath());
        // no exception expected
    }
}
