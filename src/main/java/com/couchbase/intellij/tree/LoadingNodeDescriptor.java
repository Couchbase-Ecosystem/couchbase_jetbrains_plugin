package com.couchbase.intellij.tree;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;

public class LoadingNodeDescriptor extends NodeDescriptor {

    public LoadingNodeDescriptor() {
        super("Loading...", null);
    }
}
