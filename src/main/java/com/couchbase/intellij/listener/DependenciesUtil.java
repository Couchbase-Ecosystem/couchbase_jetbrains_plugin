package com.couchbase.intellij.listener;

import com.couchbase.intellij.workbench.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Properties;

public class DependenciesUtil {

    public static final String VERSION_FILE = "cb_tool_versions.properties";
    public static final String TOOLS_KEY = "tools";
    public static final String SHELL_KEY = "shell";
    public static final String CBIMPORT_EXPORT_KEY = "cbimport_export";
    public static final String EXPLAIN_KEY = "explain";

    public static final String TOOLS_VERSION = "7.2";
    public static final String SHELL_VERSION = "1";
    public static final String CBIMPORT_EXPORT_VERSION = "7.2";
    public static final String EXPLAIN_VERSION = "1";

    public static final String MERMAID_KEY = "mermaid";
    public static final String MERMAID_VERSION = "1";

    private static void createFileWithProperties(File filePath) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(TOOLS_KEY, "");
        properties.setProperty(SHELL_KEY, "");
        properties.setProperty(CBIMPORT_EXPORT_KEY, "");
        properties.setProperty(EXPLAIN_KEY, "");

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            properties.store(out, "CB Tool Versions");
        } catch (IOException e) {
            Log.error("Failed to create the config file, this might be a bug", e);
            throw e;
        }
    }

    public static String getPropertyValue(String directoryPath, String propertyName) {
        File filePath = new File(directoryPath, VERSION_FILE);

        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            properties.load(in);
            String value = properties.getProperty(propertyName);
            return (value != null && !value.trim().isEmpty()) ? value : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void setPropertyValue(String directoryPath, String propertyName, String value) throws Exception {
        File filePath = new File(directoryPath, VERSION_FILE);

        Properties properties = new Properties();
        if (filePath.exists()) {
            try (FileInputStream in = new FileInputStream(filePath)) {
                properties.load(in);
            } catch (IOException e) {
                Log.error("Error reading the properties file: " + e.getMessage(), e);
                throw e;
            }
        }

        properties.setProperty(propertyName, value);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            properties.store(out, "CB Tool Versions");
        } catch (IOException e) {
            System.err.println("Error occurred while setting the property: " + e.getMessage());
            Log.error("Error occurred while setting the property: " + e.getMessage(), e);
            throw e;
        }
    }

    public static void createVersioningFile(String directoryPath) throws Exception {
        File filePath = new File(directoryPath, VERSION_FILE);
        if (!filePath.exists()) {
            createFileWithProperties(filePath);
        }
    }

    public static void deleteFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (IOException e) {
            Log.error("An error occurred while deleting a folder: ", e);
        }
    }
}
