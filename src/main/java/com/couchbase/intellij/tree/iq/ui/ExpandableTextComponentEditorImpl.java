/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui;

import com.intellij.openapi.editor.textarea.TextComponentEditorImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.JTextComponent;

public class ExpandableTextComponentEditorImpl extends TextComponentEditorImpl {

    public ExpandableTextComponentEditorImpl(Project project, @NotNull JTextComponent textComponent) {
        super(project, textComponent);
    }

    @Override
    public boolean isOneLineMode() {
        return false;
    }
}
