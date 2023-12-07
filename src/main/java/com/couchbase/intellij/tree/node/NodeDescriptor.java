package com.couchbase.intellij.tree.node;

import javax.swing.*;

public abstract class NodeDescriptor {
    private String text;
    private Icon icon;

    public NodeDescriptor(String text, Icon icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return getText();
    }
}
