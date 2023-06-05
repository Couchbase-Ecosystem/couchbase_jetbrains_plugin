package com.couchbase.intellij.workbench.explain;

import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;

import javax.swing.*;
import java.awt.*;

public class HtmlPanel extends JPanel {
    private JBCefBrowser jbCefBrowser;

    public HtmlPanel() {
        super(new BorderLayout());
        JBCefClient jbCefClient = JBCefApp.getInstance().createClient();
        jbCefBrowser = new JBCefBrowser(jbCefClient, null);
        add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
    }

    public void loadHTML(String html) {
        jbCefBrowser.loadHTML(html);
    }

    public void loadURL(String url) {
        jbCefBrowser.loadURL(url);
    }
}