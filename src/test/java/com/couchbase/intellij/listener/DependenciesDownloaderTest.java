package com.couchbase.intellij.listener;

import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.ToolSpec;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static utils.OSUtil.LINUX_64;
import static utils.OSUtil.LINUX_ARM;
import static utils.OSUtil.MACOS_64;
import static utils.OSUtil.MACOS_ARM;
import static utils.OSUtil.WINDOWS_64;
import static utils.OSUtil.WINDOWS_ARM;

/**
 * Only the pure URL / path / binary mapping of getDownloadList is exercised. No download is started.
 */
public class DependenciesDownloaderTest {

    private static final String BIN = "bin" + File.separator;

    private final DependenciesDownloader downloader = new DependenciesDownloader();

    @Test
    public void macOs64ProvidesAllFourToolKeys() {
        Map<String, ToolSpec> list = downloader.getDownloadList(MACOS_64);

        assertEquals(4, list.size());
        assertTrue(list.containsKey(DependenciesDownloader.TOOL_SHELL));
        assertTrue(list.containsKey(DependenciesDownloader.TOOL_IMPORT_EXPORT));
        assertTrue(list.containsKey(DependenciesDownloader.ALL_TOOLS));
        assertTrue(list.containsKey(DependenciesDownloader.CB_MIGRATE));
    }

    @Test
    public void installationPathsMatchToolKeys() {
        Map<String, ToolSpec> list = downloader.getDownloadList(LINUX_64);

        assertEquals("cbshell", list.get(DependenciesDownloader.TOOL_SHELL).getInstallationPath());
        assertEquals("cbimport_export", list.get(DependenciesDownloader.TOOL_IMPORT_EXPORT).getInstallationPath());
        assertEquals("cbtools", list.get(DependenciesDownloader.ALL_TOOLS).getInstallationPath());
        assertEquals("cbmigrate", list.get(DependenciesDownloader.CB_MIGRATE).getInstallationPath());
    }

    @Test
    public void unixBinariesHaveNoExeSuffix() {
        Map<String, ToolSpec> list = downloader.getDownloadList(LINUX_64);

        Map<CBTools.Type, String> shell = list.get(DependenciesDownloader.TOOL_SHELL).getToolsMap();
        assertEquals("cbsh", shell.get(CBTools.Type.SHELL));

        Map<CBTools.Type, String> importExport = list.get(DependenciesDownloader.TOOL_IMPORT_EXPORT).getToolsMap();
        assertEquals(BIN + "cbimport", importExport.get(CBTools.Type.CB_IMPORT));
        assertEquals(BIN + "cbexport", importExport.get(CBTools.Type.CB_EXPORT));

        Map<CBTools.Type, String> tools = list.get(DependenciesDownloader.ALL_TOOLS).getToolsMap();
        assertEquals(BIN + "cbc-pillowfight", tools.get(CBTools.Type.CBC_PILLOW_FIGHT));
        assertEquals(BIN + "mctimings", tools.get(CBTools.Type.MCTIMINGS));

        assertEquals("cbmigrate", list.get(DependenciesDownloader.CB_MIGRATE).getToolsMap().get(CBTools.Type.CBMIGRATE));
    }

    @Test
    public void windowsBinariesGetExeSuffix() {
        Map<String, ToolSpec> list = downloader.getDownloadList(WINDOWS_64);

        assertEquals("cbsh.exe", list.get(DependenciesDownloader.TOOL_SHELL).getToolsMap().get(CBTools.Type.SHELL));
        assertEquals(BIN + "cbimport.exe", list.get(DependenciesDownloader.TOOL_IMPORT_EXPORT).getToolsMap().get(CBTools.Type.CB_IMPORT));
        assertEquals("cbmigrate.exe", list.get(DependenciesDownloader.CB_MIGRATE).getToolsMap().get(CBTools.Type.CBMIGRATE));
    }

    @Test
    public void macOsArmUsesArmArtifacts() {
        Map<String, ToolSpec> list = downloader.getDownloadList(MACOS_ARM);

        assertTrue(list.get(DependenciesDownloader.TOOL_SHELL).getUrl().endsWith("cbsh-aarch64-apple-darwin.zip"));
        assertTrue(list.get(DependenciesDownloader.CB_MIGRATE).getUrl().endsWith("cbmigrate_0.1.0_darwin_arm64.zip"));
    }

    @Test
    public void linux64UrlsPointAtExpectedArtifacts() {
        Map<String, ToolSpec> list = downloader.getDownloadList(LINUX_64);

        assertTrue(list.get(DependenciesDownloader.TOOL_SHELL).getUrl().endsWith("cbsh-x86_64-unknown-linux-gnu.tar.gz"));
        assertTrue(list.get(DependenciesDownloader.ALL_TOOLS).getUrl().endsWith("7.2.0-linux_64.zip"));
    }

    @Test
    public void linuxArmHasNoAllToolsEntry() {
        // NOTE: possible bug: linux-arm has no ALL_TOOLS entry (cbc-pillowfight / mctimings), unlike every other OS.
        Map<String, ToolSpec> list = downloader.getDownloadList(LINUX_ARM);

        assertEquals(3, list.size());
        assertFalse(list.containsKey(DependenciesDownloader.ALL_TOOLS));
    }

    @Test
    public void windowsArmReusesX86ShellArtifact() {
        // NOTE: possible bug: windows-arm shell and tools point at x86_64 / amd64 artifacts.
        Map<String, ToolSpec> list = downloader.getDownloadList(WINDOWS_ARM);

        assertTrue(list.get(DependenciesDownloader.TOOL_SHELL).getUrl().contains("x86_64-pc-windows-msvc"));
        assertTrue(list.get(DependenciesDownloader.TOOL_IMPORT_EXPORT).getUrl().contains("windows_amd64"));
    }

    @Test
    public void unsupportedOsThrows() {
        try {
            downloader.getDownloadList("solaris-64");
            fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals("OS not supported.", expected.getMessage());
        }
    }

    @Test
    public void everySpecHasUrlAndToolsMap() {
        for (String os : new String[]{MACOS_64, MACOS_ARM, WINDOWS_64, WINDOWS_ARM, LINUX_64, LINUX_ARM}) {
            for (ToolSpec spec : downloader.getDownloadList(os).values()) {
                assertNotNull(os, spec.getUrl());
                assertNotNull(os, spec.getToolsMap());
                assertFalse(os, spec.getToolsMap().isEmpty());
            }
        }
    }
}
