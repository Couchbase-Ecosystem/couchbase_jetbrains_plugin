package com.couchbase.intellij.listener;

import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.ToolSpec;
import com.couchbase.intellij.tools.ToolStatus;
import com.couchbase.intellij.workbench.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.couchbase.intellij.listener.DependenciesUtil.*;
import static utils.FileUtils.*;
import static utils.OSUtil.*;

//TODO: This code is a little bit messy and definitely deserves some love in the future
public class DependenciesDownloader {

    public static final String TOOL_SHELL = "shell";
    public static final String TOOL_IMPORT_EXPORT = "import_export";

    public static final String ALL_TOOLS = "all_tools";


    private String getToolInstallPath(String toolKey) {

        if (TOOL_SHELL.equals(toolKey)) {
            return "cbshell";
        } else if (TOOL_IMPORT_EXPORT.equals(toolKey)) {
            return "cbimport_export";
        } else if (ALL_TOOLS.equals(toolKey)) {
            return "cbtools";
        } else {
            throw new IllegalStateException("Not Implemented yet");
        }
    }

    private Map<CBTools.Type, String> getToolsMap(String toolKey, String os) {
        String suffix = "";
        String path = "bin" + File.separator;

        boolean unixBased = MACOS_64.equals(os)
                || MACOS_ARM.equals(os)
                || LINUX_64.equals(os)
                || LINUX_ARM.equals(os);

        if (!unixBased) {
            suffix = ".exe";
        }

        Map<CBTools.Type, String> map = new HashMap<>();

        if (TOOL_SHELL.equals(toolKey)) {
            map.put(CBTools.Type.SHELL, "cbsh" + suffix);

        } else if (TOOL_IMPORT_EXPORT.equals(toolKey)) {
            map.put(CBTools.Type.CB_IMPORT, path + "cbimport" + suffix);
            map.put(CBTools.Type.CB_EXPORT, path + "cbexport" + suffix);

        } else if (ALL_TOOLS.equals(toolKey)) {
            map.put(CBTools.Type.CBC_PILLOW_FIGHT, path + "cbc-pillowfight" + suffix);
            map.put(CBTools.Type.MCTIMINGS, path + "mctimings" + suffix);

        } else {
            throw new IllegalStateException("Not implemented yet");
        }

        return map;
    }

    private ToolSpec getToolSpec(String url, String toolKey, String os) {
        return new ToolSpec(url, getToolInstallPath(toolKey), getToolsMap(toolKey, os));
    }

    public Map<String, ToolSpec> getDownloadList(String os) {
        Map<String, ToolSpec> map = new HashMap<>();

        if (MACOS_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-apple-darwin.zip", TOOL_SHELL, MACOS_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-macos_x86_64.zip", TOOL_IMPORT_EXPORT, MACOS_64));
            map.put(ALL_TOOLS, getToolSpec("https://intellij-plugin-dependencies.s3.us-east-2.amazonaws.com/7.2.0-macos_64.zip", ALL_TOOLS, MACOS_64));

        } else if (MACOS_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-aarch64-apple-darwin.zip", TOOL_SHELL, MACOS_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-macos_arm64.zip", TOOL_IMPORT_EXPORT, MACOS_ARM));
            map.put(ALL_TOOLS, getToolSpec("https://intellij-plugin-dependencies.s3.us-east-2.amazonaws.com/7.2.0-macos_arm.zip", ALL_TOOLS, MACOS_64));

        } else if (WINDOWS_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-pc-windows-msvc.zip", TOOL_SHELL, WINDOWS_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-windows_amd64.zip", TOOL_IMPORT_EXPORT, WINDOWS_64));
            map.put(ALL_TOOLS, getToolSpec("https://intellij-plugin-dependencies.s3.us-east-2.amazonaws.com/7.2.0-windows_64.zip", ALL_TOOLS, WINDOWS_64));

        } else if (WINDOWS_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-pc-windows-msvc.zip", TOOL_SHELL, WINDOWS_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-windows_amd64.zip", TOOL_IMPORT_EXPORT, WINDOWS_ARM));
            map.put(ALL_TOOLS, getToolSpec("https://intellij-plugin-dependencies.s3.us-east-2.amazonaws.com/7.2.0-windows_64.zip", ALL_TOOLS, WINDOWS_ARM));

        } else if (LINUX_64.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-x86_64-unknown-linux-gnu.tar.gz", TOOL_SHELL, LINUX_64));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-linux_x86_64.tar.gz", TOOL_IMPORT_EXPORT, LINUX_64));
            map.put(ALL_TOOLS, getToolSpec("https://intellij-plugin-dependencies.s3.us-east-2.amazonaws.com/7.2.0-linux_64.zip", ALL_TOOLS, LINUX_64));

        } else if (LINUX_ARM.equals(os)) {
            map.put(TOOL_SHELL, getToolSpec("https://github.com/couchbaselabs/couchbase-shell/releases/download/v0.75.1/cbsh-aarch64-unknown-linux-gnu.tar.gz", TOOL_SHELL, LINUX_ARM));
            map.put(TOOL_IMPORT_EXPORT, getToolSpec("https://packages.couchbase.com/releases/7.2.0/couchbase-server-tools_7.2.0-linux_aarch64.tar.gz", TOOL_IMPORT_EXPORT, LINUX_ARM));
        } else {
            throw new IllegalStateException("OS not supported.");
        }
        return map;
    }

    private void cleanOldVersions(String configPath, String toolsPath, Map<String, ToolSpec> downloads) {

        if (isInstalled(toolsPath, downloads.get(TOOL_SHELL), CBTools.Type.SHELL)
                && !SHELL_VERSION.equals(getPropertyValue(configPath, SHELL_KEY))) {
            ToolSpec shell = downloads.get(TOOL_SHELL);
            System.out.println("===========DDELETING CBSHELL");
            deleteFolder(toolsPath + File.separator + shell.getInstallationPath());
        }

        if (isInstalled(toolsPath, downloads.get(TOOL_IMPORT_EXPORT), CBTools.Type.CB_EXPORT)
                && !CBIMPORT_EXPORT_VERSION.equals(getPropertyValue(configPath, CBIMPORT_EXPORT_KEY))) {
            System.out.println("===========DELETING CBEXPORT");
            ToolSpec cbImport = downloads.get(TOOL_IMPORT_EXPORT);
            deleteFolder( toolsPath + File.separator + cbImport.getInstallationPath());
        }

        if (isInstalled(toolsPath, downloads.get(ALL_TOOLS), CBTools.Type.CBC_PILLOW_FIGHT)
                && !TOOLS_VERSION.equals(getPropertyValue(configPath, TOOLS_KEY))) {
            System.out.println("===========DELETING ALL TOOLS");
            ToolSpec cbTools = downloads.get(ALL_TOOLS);
            String toolsDir = toolsPath + File.separator + cbTools.getInstallationPath();
            deleteFolder( toolsDir);
        }
    }

    public void downloadDependencies(String pluginConfigPath) throws Exception {

        String toolsPath = pluginConfigPath + File.separator + "tools";
        createFolder(toolsPath);

        String os = getOSArch();
        Map<String, ToolSpec> downloads = getDownloadList(os);
        cleanOldVersions(pluginConfigPath, toolsPath, downloads);

        ToolSpec shell = downloads.get(TOOL_SHELL);
        String shellPath = toolsPath + File.separator + shell.getInstallationPath();


        if (CBTools.getTool(CBTools.Type.SHELL).getStatus() == ToolStatus.NOT_AVAILABLE
                && !isInstalled(toolsPath, downloads.get(TOOL_SHELL), CBTools.Type.SHELL)) {
            //avoiding 2 threads to install the same thing at the same time
            Log.info("Downloading CB Shell. The feature will be automatically enabled when the download is complete.");
            CBTools.getTool(CBTools.Type.SHELL).setStatus(ToolStatus.DOWNLOADING);
            downloadAndUnzip(shellPath, shell, pluginConfigPath, SHELL_KEY, SHELL_VERSION);
        } else {
            Log.debug("CBShell is already installed");
            setToolActive(ToolStatus.AVAILABLE, shellPath, shell);
        }

        ToolSpec cbImport = downloads.get(TOOL_IMPORT_EXPORT);
        String cbImportDir = toolsPath + File.separator + cbImport.getInstallationPath();
        if (CBTools.getTool(CBTools.Type.CB_IMPORT).getStatus() == ToolStatus.NOT_AVAILABLE
                && !isInstalled(toolsPath, downloads.get(TOOL_IMPORT_EXPORT), CBTools.Type.CB_EXPORT)) {
            //avoiding 2 threads to install the same thing at the same time
            Log.info("Downloading CB Import/Export. The feature will be automatically enabled when the download is complete.");
            CBTools.getTool(CBTools.Type.CB_EXPORT).setStatus(ToolStatus.DOWNLOADING);
            CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.DOWNLOADING);
            downloadAndUnzip(cbImportDir, cbImport, pluginConfigPath, CBIMPORT_EXPORT_KEY, CBIMPORT_EXPORT_VERSION);
        } else {
            Log.debug("CB Import/Export is already installed");
            setToolActive(ToolStatus.AVAILABLE, cbImportDir, cbImport);
        }


        ToolSpec cbTools = downloads.get(ALL_TOOLS);
        String toolsDir = toolsPath + File.separator + cbTools.getInstallationPath();
        if (CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).getStatus() == ToolStatus.NOT_AVAILABLE
                && !isInstalled(toolsPath, downloads.get(ALL_TOOLS), CBTools.Type.CBC_PILLOW_FIGHT)) {

            Log.info("Downloading CB tools. The feature will be automatically enabled when the download is complete.");
            CBTools.getTool(CBTools.Type.CBC_PILLOW_FIGHT).setStatus(ToolStatus.DOWNLOADING);
            CBTools.getTool(CBTools.Type.MCTIMINGS).setStatus(ToolStatus.DOWNLOADING);
            downloadAndUnzip(toolsDir, cbTools, pluginConfigPath, TOOLS_KEY, TOOLS_VERSION);
        } else {
            Log.debug("CB Tools are already installed");
            setToolActive(ToolStatus.AVAILABLE, toolsDir, cbTools);
        }

    }

    private void setToolActive(ToolStatus status, String path, ToolSpec spec) {
        for (Map.Entry<CBTools.Type, String> entry : spec.getToolsMap().entrySet()) {
            CBTools.getTool(entry.getKey()).setPath(path + File.separator + entry.getValue());
            CBTools.getTool(entry.getKey()).setStatus(status);
        }
    }

    /**
     *
     * @param targetDir where the zip file should be downloaded
     * @param spec
     * @param configFolder folder where the file with the versions is stored
     * @param key the key that will be updated
     * @param value the value that will be set
     */
    public void downloadAndUnzip(String targetDir, ToolSpec spec, String configFolder, String key, String value) {
        CompletableFuture.runAsync(() -> {
            try {
                createFolder(targetDir);
                String fileName = spec.getUrl().substring(spec.getUrl().lastIndexOf("/") + 1);
                String localFilePath = targetDir + File.separator + fileName;
                URL website = new URL(spec.getUrl());
                try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                     FileOutputStream fos = new FileOutputStream(localFilePath)) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }

                unzipFile(localFilePath, targetDir);
                makeFilesExecutable(new File(targetDir));
                setToolActive(ToolStatus.AVAILABLE, targetDir, spec);
                DependenciesUtil.setPropertyValue(configFolder, key, value);
            } catch (Exception e) {
                setToolActive(ToolStatus.NOT_AVAILABLE, null, null);
                Log.error(e);
                e.printStackTrace();
            }
        });
    }

    public boolean isInstalled(String pluginPath, ToolSpec spec, CBTools.Type type) {
        return Files.exists(Paths.get(pluginPath + File.separator
                + spec.getInstallationPath()
                + File.separator
                + spec.getToolsMap().get(type)));
    }
}
