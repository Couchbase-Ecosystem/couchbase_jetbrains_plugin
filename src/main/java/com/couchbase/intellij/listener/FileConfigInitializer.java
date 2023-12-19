package com.couchbase.intellij.listener;

import com.couchbase.intellij.tools.CBFolders;
import com.couchbase.intellij.workbench.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.couchbase.intellij.listener.DependenciesUtil.*;
import static utils.FileUtils.createFolder;
import static utils.FileUtils.unzipFile;

public class FileConfigInitializer {


    public static void start(String toolsPath) throws Exception {
        String configPath = toolsPath + File.separator + "config";
        createFolder(configPath);

        //initCBShell(configPath);
        initExplain(toolsPath, configPath);
        initJSDependencies(toolsPath, configPath);
    }

//NOTE: LEAVE THIS CODE COMMENTED FOR NOW
//    public static void initCBShell(String configPath) throws Exception {
//        String path = configPath + File.separator + "cbshell";
//        Path dest = Paths.get(path);
//
//        if (!Files.exists(dest)) {
//            Log.debug("Copying CBShell autopass.sh script");
//            createFolder(path);
//            copyFile("/tools/cbshell.zip", Paths.get(configPath + File.separator + "cbshell.zip"));
//            unzipFile(configPath + File.separator + "cbshell.zip", configPath);
//            makeFilesExecutable(new File(path));
//        } else {
//            Log.debug("The script for cbshell is already copied to the config");
//        }
//
//        CBFolders.getInstance().setCbShellPath(path);
//    }

    public static void initExplain(String toolsPath, String configPath) throws Exception {

        String path = configPath + File.separator + "explain";

        if (!EXPLAIN_VERSION.equals(getPropertyValue(toolsPath, EXPLAIN_KEY))) {
            Log.info("A new version of Couchbase Explain is available. Removing local version and downloading the new one");
            DependenciesUtil.deleteFolder(path);
        }


        Path dest = Paths.get(path);
        if (!Files.exists(dest)) {
            Log.debug("Copying explain files");
            createFolder(path);
            copyFile("/tools/explain.zip", Paths.get(configPath + File.separator + "explain.zip"));
            unzipFile(configPath + File.separator + "explain.zip", configPath);
            DependenciesUtil.setPropertyValue(toolsPath, EXPLAIN_KEY, EXPLAIN_VERSION);
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


    public static void initJSDependencies(String toolsPath, String configPath) throws Exception {

        String path = configPath + File.separator + "js_dependencies";

        if (!JS_DEPENDENCIES_VERSION.equals(getPropertyValue(toolsPath, JS_DEPENDENCIES_KEY))) {
            Log.info("A new version of Couchbase JS Dependencies is available. Removing local version and downloading the new one");
            DependenciesUtil.deleteFolder(path);
        }


        Path dest = Paths.get(path);
        if (!Files.exists(dest)) {
            Log.debug("Copying js dependencies files");
            createFolder(path);
            copyFile("/tools/js_dependencies.zip", Paths.get(path + File.separator + "js_dependencies.zip"));
            unzipFile(path + File.separator + "js_dependencies.zip", path);
            DependenciesUtil.setPropertyValue(toolsPath, JS_DEPENDENCIES_KEY, JS_DEPENDENCIES_VERSION);
        } else {
            Log.debug("The script for explain is already copied to the config");
        }
        CBFolders.getInstance().setJsDependenciesPath(path);
    }

}
