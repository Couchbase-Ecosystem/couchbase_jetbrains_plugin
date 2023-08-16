package com.couchbase.intellij.workbench.error;

public class CouchbaseQueryError {
    private int code;
    private String message;
    private boolean retry;

    public CouchbaseQueryError() {

    }

    public CouchbaseQueryError(int code, String message, boolean retry) {
        this.code = code;
        this.message = message;
        this.retry = retry;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

}
