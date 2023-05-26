package com.couchbase.intellij.workbench.error;

public class CouchbaseQueryTimingsServiceError {
    private int dispatchMicros;
    private int totalDispatchMicros;
    private int totalMicros;

    public int getDispatchMicros() {
        return dispatchMicros;
    }

    public void setDispatchMicros(int dispatchMicros) {
        this.dispatchMicros = dispatchMicros;
    }

    public int getTotalDispatchMicros() {
        return totalDispatchMicros;
    }

    public void setTotalDispatchMicros(int totalDispatchMicros) {
        this.totalDispatchMicros = totalDispatchMicros;
    }

    public int getTotalMicros() {
        return totalMicros;
    }

    public void setTotalMicros(int totalMicros) {
        this.totalMicros = totalMicros;
    }
}

