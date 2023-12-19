package com.couchbase.intellij.tools;

public class CBFolders {

    private static CBFolders instance;
    private String cbShellPath;
    private String explainPath;

    private String jsDependenciesPath;

    private CBFolders() {
    }

    public static CBFolders getInstance() {
        if (instance == null) {
            instance = new CBFolders();
        }
        return instance;
    }

    public String getCbShellPath() {
        return cbShellPath;
    }

    public void setCbShellPath(String cbShellPath) {
        this.cbShellPath = cbShellPath;
    }

    public String getExplainPath() {
        return explainPath;
    }

    public void setExplainPath(String explainPath) {
        this.explainPath = explainPath;
    }

    public String getJsDependenciesPath() {
        return jsDependenciesPath;
    }

    public void setJsDependenciesPath(String jsDependenciesPath) {
        this.jsDependenciesPath = jsDependenciesPath;
    }
}
