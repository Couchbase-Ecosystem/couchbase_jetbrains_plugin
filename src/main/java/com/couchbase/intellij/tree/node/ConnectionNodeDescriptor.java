package com.couchbase.intellij.tree.node;

import com.couchbase.intellij.persistence.SavedCluster;
import com.intellij.openapi.util.IconLoader;

public class ConnectionNodeDescriptor extends NodeDescriptor {

    private SavedCluster savedCluster;
    private boolean isActive;

    public ConnectionNodeDescriptor(String name, SavedCluster savedCluster, boolean isActive) {
        super(name, isActive ? IconLoader.getIcon("/assets/icons/couchbase-active.svg", ConnectionNodeDescriptor.class) : IconLoader.getIcon("/assets/icons/couchbase.svg", ConnectionNodeDescriptor.class));
        this.savedCluster = savedCluster;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if (active) {
            setIcon(IconLoader.getIcon("/assets/icons/couchbase-active.svg", ConnectionNodeDescriptor.class));
        } else {
            setIcon(IconLoader.getIcon("/assets/icons/couchbase.svg", ConnectionNodeDescriptor.class));
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
