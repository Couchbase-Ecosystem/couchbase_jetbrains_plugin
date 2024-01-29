package com.couchbase.intellij.tree.iq.ui.context.stack;

import com.couchbase.intellij.tree.iq.chat.InputContext;
import com.couchbase.intellij.tree.iq.chat.InputContextChangeEvent;
import com.couchbase.intellij.tree.iq.chat.InputContextEntry;
import com.couchbase.intellij.tree.iq.chat.InputContextListener;
import com.couchbase.intellij.tree.iq.event.ListenerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultInputContext implements InputContext {
    private final List<InputContextEntry> entries;
    private final List<InputContextEntry> unmodifiableEntries;
    private final ListenerList<InputContextListener> listeners = ListenerList.of(InputContextListener.class);

    public DefaultInputContext() {
        this.entries = Collections.synchronizedList(new ArrayList<>());
        this.unmodifiableEntries = Collections.unmodifiableList(entries);
    }

    @Override
    public void addListener(InputContextListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(InputContextListener listener) {
        listeners.removeListener(listener);
    }

    @Override
    public void addEntry(InputContextEntry entry) {
        entries.add(entry);
        fireContextChanged();
    }

    @Override
    public void removeEntry(InputContextEntry entry) {
        entries.remove(entry);
        fireContextChanged();
    }

    @Override
    public List<InputContextEntry> getEntries() {
        return unmodifiableEntries;
    }

    @Override
    public boolean isEmpty() {
        return getEntries().isEmpty();
    }

    @Override
    public void clear() {
        entries.clear();
        fireContextChanged();
    }

    protected void fireContextChanged() {
        InputContextChangeEvent event = new InputContextChangeEvent(this);
        listeners.fire().contextChanged(event);
    }
}