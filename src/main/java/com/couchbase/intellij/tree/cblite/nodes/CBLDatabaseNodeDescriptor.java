package com.couchbase.intellij.tree.cblite.nodes;

import com.couchbase.intellij.tree.cblite.storage.SavedCBLDatabase;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.node.NodeDescriptor;
import com.intellij.openapi.util.IconLoader;
import lombok.Getter;
import lombok.Setter;


public class CBLDatabaseNodeDescriptor extends NodeDescriptor {

    @Getter
    @Setter
    private SavedCBLDatabase database;
    @Getter
    private boolean active;
    public CBLDatabaseNodeDescriptor(SavedCBLDatabase database, boolean isActive) {
        super(database.getId(), isActive ? IconLoader.getIcon("/assets/icons/couchbase-active.svg", CBLDatabaseNodeDescriptor.class) : IconLoader.getIcon("/assets/icons/couchbase.svg", CBLDatabaseNodeDescriptor.class));
        this.database = database;
        this.active = isActive;
    }

    public void setActive(boolean active) {
        if (active) {
            setIcon(IconLoader.getIcon("/assets/icons/couchbase-active.svg", ConnectionNodeDescriptor.class));
        } else {
            setIcon(IconLoader.getIcon("/assets/icons/couchbase.svg", ConnectionNodeDescriptor.class));
        }
        this.active = active;
    }
}
