package com.couchbase.intellij.tree;

public class TooltipNodeDescriptor extends NodeDescriptor{

    private String tooltip;
    public TooltipNodeDescriptor(String text, String tooltip) {
        super(text,null);
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
