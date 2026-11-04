package com.couchbase.intellij.tree;

import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.StringSelection;

/** Clipboard helpers for the explorer tree's copy actions. */
public final class TreeCopy {
    private TreeCopy() {}

    public static void toClipboard(String text) {
        CopyPasteManager.getInstance().setContents(new StringSelection(text));
    }

    /** The keyspace as SQL++ expects it, each part backticked: {@code `bucket`.`scope`.`collection`}. */
    public static String qualifiedName(String bucket, String scope, String collection) {
        return "`" + bucket + "`.`" + scope + "`.`" + collection + "`";
    }
}
