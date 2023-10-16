/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui.context.stack;

import com.couchbase.intellij.tree.iq.chat.InputContextEntry;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.util.Optional;
import java.util.function.ToIntFunction;

public class TextInputContextEntry implements InputContextEntry {
    private final TextContent textContent;
    private final Icon icon;
    private final String text;
    private volatile int tokenCount = -1;

    public TextInputContextEntry(Icon icon, String text, TextContent textContent) {
        this.icon = icon;
        this.text = text;
        this.textContent = textContent;
    }

    @Override
    public Optional<TextContent> getTextContent() {
        return Optional.of(textContent);
    }

    public final Icon getIcon() {
        return icon;
    }

    public final String getText() {
        return text;
    }

    public int getOrComputeTokenCount(ToIntFunction<TextInputContextEntry> calculator) {
        var tokenCount = this.tokenCount;
        if (tokenCount < 0 && calculator != null) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> setTokenCount(calculator.applyAsInt(this)));
        }
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    private boolean pinned;

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

}
