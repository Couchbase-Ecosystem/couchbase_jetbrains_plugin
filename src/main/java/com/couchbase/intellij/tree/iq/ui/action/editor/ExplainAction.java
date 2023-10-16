/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.ChatGptBundle;

public class ExplainAction extends GenericEditorAction {
    public ExplainAction() {
        super(() -> ChatGptBundle.message("action.code.explain.menu"), "Explain this code");
    }
}
