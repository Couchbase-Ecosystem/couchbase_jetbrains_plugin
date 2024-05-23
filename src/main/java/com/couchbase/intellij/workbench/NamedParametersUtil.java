package com.couchbase.intellij.workbench;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.persistence.storage.NamedParamsStorage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NamedParametersUtil {

    public static Map<String, String> getAllNamedParameters(Project project) {
        Map<String, String> map = new HashMap<>();
        map.putAll(readProjectNamedParameters(project));
        map.putAll(NamedParamsStorage.getInstance().getValue().getParams());
        return map;
    }

    public static Map<String, String> readProjectNamedParameters(Project project) {
        Map<String, String> propertiesMap = new HashMap<>();

        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return propertiesMap;
        }
        VirtualFile propertiesFile = baseDir.findChild(".cbNamedParams.properties");
        if (propertiesFile == null || !propertiesFile.exists()) {
            return propertiesMap;
        }

        try (InputStream inputStream = propertiesFile.getInputStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);

            for (String name : properties.stringPropertyNames()) {
                propertiesMap.put(name, properties.getProperty(name));
            }
        } catch (IOException e) {
            Log.error("An error occurred while trying to read the .cbNamedParams.properties file", e); // Handle the
            // exception as needed
        }

        return propertiesMap;
    }

    public static Object parseValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        } else {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e1) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e2) {
                    try {
                        return JsonArray.fromJson(value);
                    } catch (Exception e3) {
                        try {
                            return JsonObject.fromJson(value);
                        } catch (Exception e4) {
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            return value;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(parseValue("1.0"));
    }
}
