package com.couchbase.intellij.workbench;

import java.util.*;

/** Per-editor history snapshot. Browsing never discards the draft or edits to a recalled query. */
public final class QueryHistoryNavigator {
    private List<String> entries;
    private final Map<Integer, String> edits = new HashMap<>();
    private String draft;
    private int index;

    public QueryHistoryNavigator(List<String> history, String draft) { reset(history, draft); }

    private void reset(List<String> history, String text) {
        entries = List.copyOf(history);
        draft = text;
        index = entries.size();
        edits.clear();
    }

    public boolean canPrevious() { return index > 0; }
    public boolean canNext() { return index < entries.size(); }
    public boolean isDraft() { return index == entries.size(); }

    public String previous(String current) {
        if (!canPrevious()) return current;
        remember(current);
        return textAt(--index);
    }

    public String next(String current) {
        if (!canNext()) return current;
        remember(current);
        return textAt(++index);
    }

    private void remember(String text) {
        if (isDraft()) draft = text;
        else edits.put(index, text);
    }

    private String textAt(int position) {
        return position == entries.size() ? draft : edits.getOrDefault(position, entries.get(position));
    }

    public String label() {
        if (entries.isEmpty()) return "No history";
        return isDraft() ? "History (" + entries.size() + ")" : "History (" + (index + 1) + "/" + entries.size() + ")";
    }

    /** Locate the recalled query in the current shared history, which other editors may have changed. */
    public int executionIndex(List<String> history) {
        int found = isDraft() ? -1 : history.lastIndexOf(entries.get(index));
        return found < 0 ? history.size() : found;
    }

    public void refresh(List<String> history, String current) {
        remember(current);
        if (entries.equals(history)) return;
        boolean wasDraft = isDraft();
        int selected = wasDraft ? history.size() : history.lastIndexOf(entries.get(index));
        // A capped shared history may have evicted this query. Keep this editor's snapshot until it returns to its draft.
        if (selected < 0) return;
        Map<String, String> byQuery = new HashMap<>();
        edits.forEach((position, text) -> byQuery.put(entries.get(position), text));
        String savedDraft = draft;
        reset(history, savedDraft);
        index = selected;
        for (int i = 0; i < entries.size(); i++) if (byQuery.containsKey(entries.get(i))) edits.put(i, byQuery.get(entries.get(i)));
    }
}
