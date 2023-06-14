package com.couchbase.intellij.listener;

import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.ToolSpec;
import com.couchbase.intellij.tools.ToolStatus;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.PathManager;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static utils.FileUtils.*;
import static utils.OSUtil.*;

//TODO: This code is a little bit messy and definitely deserves some love in the future
public class DependenciesDownloader {

    public static final String TOOL_SHELL = "shell";
    public static final String TOOL_IMPORT_EXPORT = "import_export";


    private String getToolInstallPath(String toolKey) {

        if (TOOL_SHELL.equals(toolKey)) {
            return "cbshell";
        } else if (TOOL_IMPORT_EXPORT.equals(toolKey)) {
            return "cbimport_export";
        } else {
            throw new IllegalStateException("Not Implemented yet");
        }
    }

    private List<String> getToolsList(String toolKey, String os) {
        boolean unixBased = MACOS_64.equals(os)
                || MACOS_ARM.equals(os)
                || LINUX_64.equals(os)
                || LINUX_ARM.equals(os);

        if (TOOL_SHELL.equals(toolKey)) {
            if (unixBased) {
                return List.of("cbsh");
            } else {
                return List.of("cbsh.exe");
            }
        } else if (TOOL_IMPORT_EXPORT.equals(toolKey)) {
            String path = "bin" + File.separator;
            if (unixBased) {
                return Arrays.asList(path + "cbimport", path + "cbexport");
            } else {
                return Arrays.asList(path + "cbimport.exe", path + "cbexport.exe");
            }
        } else {
            throw new IllegalStateException("Not implemented yet");
        }
    }

    private ToolSpec getToolSpec(String url, String toolKey, String os) {
        return new ToolSpec(url, getToolInstallPath(toolKey), getToolsList(toolKey, os));
    }

    public Map<String, ToolSpec> getDownloadList(String os) {
        Map<String, ToolSpec> map = new HashMap<>();

        if (MACOS_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-apple-darwin.zip", TOOL_SHELL, MACOS_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-macos_x86_64.zip", TOOL_IMPORT_EXPORT, MACOS_64));

        } else if (MACOS_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-aarch64-apple-darwin.zip", TOOL_SHELL, MACOS_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-macos_arm64.zip", TOOL_IMPORT_EXPORT, MACOS_ARM));

        } else if (WINDOWS_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-pc-windows-msvc.zip", TOOL_SHELL, WINDOWS_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-windows_amd64.zip", TOOL_IMPORT_EXPORT, WINDOWS_64));

        } else if (WINDOWS_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-pc-windows-msvc.zip", TOOL_SHELL, WINDOWS_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-windows_amd64.zip", TOOL_IMPORT_EXPORT, WINDOWS_ARM));

        } else if (LINUX_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-unknown-linux-gnu.tar.gz", TOOL_SHELL, LINUX_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-linux_x86_64.tar.gz", TOOL_IMPORT_EXPORT, LINUX_64));

        } else if (LINUX_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-aarch64-unknown-linux-gnu.tar.gz", TOOL_SHELL, LINUX_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-linux_aarch64.tar.gz", TOOL_IMPORT_EXPORT, LINUX_ARM));
        } else {
            throw new IllegalStateException("OS not supported.");
        }
        return map;
    }

    public void downloadDependencies() throws Exception {
        String toolsPath = PathManager.getConfigPath() + File.separator + "couchbase-intellij-plugin";
        createFolder(toolsPath);
        toolsPath += File.separator + "tools";
        createFolder(toolsPath);

        String os = getOSArch();
        Map<String, ToolSpec> downloads = getDownloadList(os);

        ToolSpec shell = downloads.get(TOOL_SHELL);
        String shellPath = toolsPath + File.separator + shell.getInstallationPath();
        if (CBTools.getShell().getStatus() == ToolStatus.NOT_AVAILABLE
                && !isInstalled(toolsPath, downloads.get(TOOL_SHELL))) {
            //avoiding 2 threads to install the same thing at the same time
            Log.debug("Downloading CB Shell");
            CBTools.getShell().setStatus(ToolStatus.DOWNLOADING);
            downloadAndUnzip(TOOL_SHELL, shellPath, shell);
        } else {
            Log.debug("CBShell is already downloaded");
            setToolActive(TOOL_SHELL, ToolStatus.AVAILABLE, shellPath, shell);
        }

        ToolSpec cbImport = downloads.get(TOOL_IMPORT_EXPORT);
        String cbImportDir = toolsPath + File.separator + cbImport.getInstallationPath();
        if (CBTools.getCbImport().getStatus() == ToolStatus.NOT_AVAILABLE
                && !isInstalled(toolsPath, downloads.get(TOOL_IMPORT_EXPORT))) {
            //avoiding 2 threads to install the same thing at the same time
            Log.debug("Downloading CB Import/Export");
            CBTools.getCbExport().setStatus(ToolStatus.DOWNLOADING);
            CBTools.getCbImport().setStatus(ToolStatus.DOWNLOADING);
            downloadAndUnzip(TOOL_IMPORT_EXPORT, cbImportDir, cbImport);
        } else {
            Log.debug("CB Import/Export is already downloaded");
            setToolActive(TOOL_IMPORT_EXPORT, ToolStatus.AVAILABLE, cbImportDir, cbImport);
        }
    }


    private void setToolActive(String toolKey, ToolStatus status, String path, ToolSpec spec) {
        if (TOOL_SHELL.equals(toolKey)) {
            CBTools.getShell().setStatus(status);
            if (status == ToolStatus.AVAILABLE) {
                CBTools.getShell().setPath(path + File.separator + spec.getToolsList().get(0));
            }
        } else if (TOOL_IMPORT_EXPORT.equals(toolKey)) {
            CBTools.getCbImport().setStatus(status);
            CBTools.getCbExport().setStatus(status);

            if (status == ToolStatus.AVAILABLE) {
                for (String tool : spec.getToolsList()) {
                    if (tool.contains("cbimport")) {
                        CBTools.getCbImport().setPath(path + File.separator + tool);
                    } else if (tool.contains("cbexport")) {
                        CBTools.getCbExport().setPath(path + File.separator + tool);
                    }
                }
            }
        } else {
            throw new IllegalStateException("Not Implemented Yet");
        }
    }

    public void downloadAndUnzip(String toolKey, String targetDir, ToolSpec spec) {
        CompletableFuture.runAsync(() -> {
            try {
                createFolder(targetDir);
                String fileName = spec.getUrl().substring(spec.getUrl().lastIndexOf("/") + 1);
                String localFilePath = targetDir + File.separator + fileName;
                URL website = new URL(spec.getUrl());
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(localFilePath);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                unzipFile(localFilePath, targetDir);
                makeFilesExecutable(new File(targetDir));
                setToolActive(toolKey, ToolStatus.AVAILABLE, targetDir, spec);
            } catch (Exception e) {
                setToolActive(toolKey, ToolStatus.NOT_AVAILABLE, null, null);
                Log.error(e);
                e.printStackTrace();
            }
        });
    }


    //TODO: Keep this code until we have tested everything on windows
//    private void unzipFile(String zipFilePath, String destDir) throws IOException {
//        File dir = new File(destDir);
//        if (!dir.exists()) dir.mkdirs();
//        File zipFile = new File(zipFilePath);
//        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
//            ZipEntry entry = zis.getNextEntry();
//            while (entry != null) {
//                String filePath = destDir + File.separator + entry.getName();
//                if (!entry.isDirectory()) {
//                    extractFile(zis, filePath);
//                } else {
//                    File dir1 = new File(filePath);
//                    dir1.mkdir();
//                }
//                zis.closeEntry();
//                entry = zis.getNextEntry();
//            }
//        }
//        Files.delete(zipFile.toPath());
//    }
//
//    private void extractFile(ZipInputStream zis, String filePath) throws IOException {
//        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
//            byte[] bytesIn = new byte[4096];
//            int read;
//            while ((read = zis.read(bytesIn)) != -1) {
//                bos.write(bytesIn, 0, read);
//            }
//        }
//    }


    public boolean isInstalled(String pluginPath, ToolSpec spec) {
        return Files.exists(Paths.get(pluginPath + File.separator
                + spec.getInstallationPath()
                + File.separator
                + spec.getToolsList().get(0)));
    }
}
