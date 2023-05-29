package com.couchbase.intellij.persistence;

import java.util.List;

public class QueryHistory {

    private List<String> history;

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
