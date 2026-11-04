package com.couchbase.intellij.tools;

public class CBTool {

    // Written by the download thread, read by action updates.
    private volatile ToolStatus status;
    private volatile String path;

    public CBTool() {
        this.status = ToolStatus.NOT_AVAILABLE;
    }

    public ToolStatus getStatus() {
        return status;
    }

    public void setStatus(ToolStatus status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAvailable() {
        return status == ToolStatus.AVAILABLE;
    }
}
