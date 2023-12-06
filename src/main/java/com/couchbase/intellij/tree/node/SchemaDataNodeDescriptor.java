package com.couchbase.intellij.tree.node;

import com.intellij.icons.AllIcons;
import lombok.Getter;
import utils.ColorHelper;

public class SchemaDataNodeDescriptor extends TooltipNodeDescriptor {

    @Getter
    private String path;

    @Getter
    private String reference;

    private String key;
    private String value;

    public SchemaDataNodeDescriptor(String key, String value, String tooltip, String path, String reference) {
        super(getHtmlLabel(key, value, reference), tooltip, reference != null ? AllIcons.CodeWithMe.CwmInvite : null);
        this.key = key;
        this.value = value;
        this.path = path;
        this.reference = reference;
    }

    private static String getReferenceHtml(String reference) {
        if (reference == null) {
            return "";
        }
        String[] split = reference.split("\\.");
        String prefix = split[0] + "." + split[1] + ".";
        return "<small>&nbsp;&nbsp;<span style='color: gray;'>→&nbsp;" + reference.replace(prefix, "") + "</span></small>";
    }

    private static String getHtmlLabel(String key, String value, String reference) {
        return "<html><strong style='color: " + ColorHelper.getKeywordColor() + "'>" + key + ":</strong> " + value
                + getReferenceHtml(reference) + "</html>";
    }

    public void setReference(String reference) {
        this.reference = reference;
        setIcon(reference != null ? AllIcons.CodeWithMe.CwmInvite : null);
        setText(getHtmlLabel(key, value, reference));
    }
}
