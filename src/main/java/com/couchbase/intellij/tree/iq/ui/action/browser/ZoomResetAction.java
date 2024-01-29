package com.couchbase.intellij.tree.iq.ui.action.browser;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.jcef.JBCefBrowserBase;
import org.jetbrains.annotations.NotNull;

public class ZoomResetAction extends JBCefBrowserAction {

    private final double defaultZoomLevel;

    public ZoomResetAction(JBCefBrowserBase browser) {
        super(browser, () -> "Restore Default Zoom Level", AllIcons.Actions.SetDefault);
        this.defaultZoomLevel = browser.getZoomLevel();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        getJBCefBrowserBase().setZoomLevel(defaultZoomLevel);
    }
}
