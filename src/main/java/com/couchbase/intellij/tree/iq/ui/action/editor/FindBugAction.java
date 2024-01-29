package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.ChatGptBundle;

public class FindBugAction extends GenericEditorAction {
    public FindBugAction() {
        super(() -> ChatGptBundle.message("action.code.wrong.menu"), "Find the bug in this code");
    }
}
