package com.couchbase.intellij.persistence;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "TreeModelState",
    storages = {
        @Storage(
            value = "treemodel.xml"
        )
    }
)
public class TreeModelStorage implements PersistentStateComponent<TreeModel> {
    private TreeModel dataModel = new TreeModel();

    @Nullable
    @Override
    public TreeModel getState() {
        return dataModel;
    }

    @Override
    public void loadState(@NotNull TreeModel state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    // Add any additional methods or logic for interacting with the settings
}