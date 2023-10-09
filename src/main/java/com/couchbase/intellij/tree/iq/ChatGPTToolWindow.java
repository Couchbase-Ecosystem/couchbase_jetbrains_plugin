package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.ui.CouchbaseIQPanel;
import com.couchbase.intellij.tree.iq.util.MyUIUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ChatGPTToolWindow {

    private final CouchbaseIQPanel panel;

    public ChatGPTToolWindow(@NotNull Project project) {
        panel = new CouchbaseIQPanel(project, true);
    }

    public JPanel getContent() {
        return panel.init();
    }

    public CouchbaseIQPanel getPanel() {
        return panel;
    }

    public void registerKeystrokeFocus() {
        MyUIUtil.registerKeystrokeFocusForInput(panel.getSearchTextArea().getTextArea());
    }
}
