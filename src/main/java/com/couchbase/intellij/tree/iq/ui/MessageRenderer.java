package com.couchbase.intellij.tree.iq.ui;

import javax.swing.text.EditorKit;

public interface MessageRenderer {

    EditorKit getEditorKit();

    int getWidth();

    Object getClientProperty(Object key);
}
