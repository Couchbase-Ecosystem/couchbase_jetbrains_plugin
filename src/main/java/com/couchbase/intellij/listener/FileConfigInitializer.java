package com.couchbase.intellij.listener;

import com.couchbase.intellij.tools.CBFolders;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.PathManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static utils.FileUtils.*;

public class FileConfigInitializer {


    public static void start() throws Exception {
        String toolsPath = PathManager.getConfigPath() + File.separator + "couchbase-intellij-plugin";
        createFolder(toolsPath);
        toolsPath += File.separator + "config";
        createFolder(toolsPath);

        initCBShell(toolsPath);
        initExplain(toolsPath);
    }


    public static void initCBShell(String toolsPath) throws Exception {
        String path = toolsPath + File.separator + "cbshell";
        Path dest = Paths.get(path);

        if (!Files.exists(dest)) {
            Log.debug("Copying CBShell autopass.sh script");
            createFolder(path);
            copyFile("/tools/cbshell.zip", Paths.get(toolsPath + File.separator + "cbshell.zip"));
            unzipFile(toolsPath + File.separator + "cbshell.zip", toolsPath);
            makeFilesExecutable(new File(path));
        } else {
            Log.debug("The script for cbshell is already copied to the config");
        }

        CBFolders.getInstance().setCbShellPath(path);
    }

    public static void initExplain(String toolsPath) throws Exception {
        String path = toolsPath + File.separator + "explain";
        Path dest = Paths.get(path);
        if (!Files.exists(dest)) {
            Log.debug("Copying explain files");
            createFolder(path);
            copyFile("/tools/explain.zip", Paths.get(toolsPath + File.separator + "explain.zip"));
            unzipFile(toolsPath + File.separator + "explain.zip", toolsPath);
        } else {
            Log.debug("The script for explain is already copied to the config");
        }
        CBFolders.getInstance().setExplainPath(path);
    }


    public static void copyFile(String src, Path dest) throws IOException {
        try (InputStream is = FileConfigInitializer.class.getResourceAsStream(src)) {
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + src);
            }
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
