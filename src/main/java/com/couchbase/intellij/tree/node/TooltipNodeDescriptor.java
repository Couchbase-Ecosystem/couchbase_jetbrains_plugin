package com.couchbase.intellij.tree.node;

import javax.swing.*;

public class TooltipNodeDescriptor extends NodeDescriptor {

    private String tooltip;

    public TooltipNodeDescriptor(String text, String tooltip) {
        super(text, null);
        this.tooltip = tooltip;
    }

    public TooltipNodeDescriptor(String text, String tooltip, Icon icon) {
        super(text, icon);
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
