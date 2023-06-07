package com.couchbase.intellij.tools;

import java.util.List;

public class ToolSpec {

    private String url;
    private String installationPath;
    private List<String> toolsList;

    public ToolSpec(String url, String installationPath, List<String> toolsList) {
        this.url = url;
        this.installationPath = installationPath;
        this.toolsList = toolsList;
    }

    public String getUrl() {
        return url;
    }

    public String getInstallationPath() {
        return installationPath;
    }

    public List<String> getToolsList() {
        return toolsList;
    }
}
