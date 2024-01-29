package com.couchbase.intellij.tree.iq.ui.context.stack;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class ListItemDescriptorAdapter<T> implements ListItemDescriptor<T> {
    @Nullable
    @Override
    public String getCaptionAboveOf(T value) {
        return null;
    }

    @Nullable
    @Override
    public String getTooltipFor(T value) {
        return null;
    }

    @Override
    public Icon getIconFor(T value) {
        return null;
    }

    @Override
    public boolean hasSeparatorAboveOf(T value) {
        return false;
    }
}