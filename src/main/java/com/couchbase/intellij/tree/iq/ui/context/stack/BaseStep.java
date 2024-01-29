package com.couchbase.intellij.tree.iq.ui.context.stack;

public abstract class BaseStep<T> implements StackStep<T>, SpeedSearchFilter<T> {

    @Override
    public boolean isSpeedSearchEnabled() {
        return false;
    }

    @Override
    public SpeedSearchFilter<T> getSpeedSearchFilter() {
        return this;
    }

    @Override
    public String getIndexedString(T value) {
        return getTextFor(value);
    }

    public abstract String getTextFor(T value);

    @Override
    public boolean isAutoSelectionEnabled() {
        return true;
    }

}
