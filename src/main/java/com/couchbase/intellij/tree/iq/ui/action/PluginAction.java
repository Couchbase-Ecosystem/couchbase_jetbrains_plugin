package com.couchbase.intellij.tree.iq.ui.action;

import com.couchbase.intellij.tree.iq.message.ChatGPTBundle;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class PluginAction extends DumbAwareAction {

    public PluginAction() {
        super(() -> ChatGPTBundle.message("action.plugins"), AllIcons.Nodes.Plugin);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("https://plugins.jetbrains.com/organizations/obiscr");
    }
}
