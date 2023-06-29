package com.couchbase.intellij.tools;

import java.util.Map;

public class ToolSpec {

    private final String url;
    private final String installationPath;
    private final Map<CBTools.Type, String> toolsMap;

    public ToolSpec(String url, String installationPath, Map<CBTools.Type, String> toolsMap) {
        this.url = url;
        this.installationPath = installationPath;
        this.toolsMap = toolsMap;
    }

    public String getUrl() {
        return url;
    }

    public String getInstallationPath() {
        return installationPath;
    }

    public Map<CBTools.Type, String> getToolsMap() {
        return toolsMap;
    }
}
