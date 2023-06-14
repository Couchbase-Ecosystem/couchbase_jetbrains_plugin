package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.openapi.util.IconLoader;

public class ConnectionNodeDescriptor extends NodeDescriptor {

    private SavedCluster savedCluster;
    private boolean isActive;

    public ConnectionNodeDescriptor(String name, SavedCluster savedCluster, boolean isActive) {
        super(name, isActive ? IconLoader.findIcon("./assets/icons/couchbase-active.svg") : IconLoader.findIcon("./assets/icons/couchbase.svg"));
        this.savedCluster = savedCluster;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if (active) {
            setIcon(IconLoader.findIcon("./assets/icons/couchbase-active.svg"));
        } else {
            setIcon(IconLoader.findIcon("./assets/icons/couchbase.svg"));
        }
        isActive = active;
    }

    public SavedCluster getSavedCluster() {
        return savedCluster;
    }

    public void setSavedCluster(SavedCluster savedCluster) {
        this.savedCluster = savedCluster;
    }
}
