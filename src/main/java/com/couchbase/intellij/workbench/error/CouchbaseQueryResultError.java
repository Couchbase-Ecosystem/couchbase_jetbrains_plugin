package com.couchbase.intellij.workbench.error;

import java.util.ArrayList;
import java.util.List;

public class CouchbaseQueryResultError {

    private boolean completed;
    private String coreId;
    private int httpStatus;
    private boolean idempotent;
    private String lastDispatchedFrom;
    private String lastDispatchedTo;
    private int requestId;
    private String requestType;
    private int retried;
    private List<String> retryReasons;
    private List<CouchbaseQueryError> errors = new ArrayList<>();
    private CouchbaseQueryServiceError service;
    private long timeoutMs;
    private CouchbaseQueryTimingsServiceError timings;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCoreId() {
        return coreId;
    }

    public void setCoreId(String coreId) {
        this.coreId = coreId;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }

    public String getLastDispatchedFrom() {
        return lastDispatchedFrom;
    }

    public void setLastDispatchedFrom(String lastDispatchedFrom) {
        this.lastDispatchedFrom = lastDispatchedFrom;
    }

    public String getLastDispatchedTo() {
        return lastDispatchedTo;
    }

    public void setLastDispatchedTo(String lastDispatchedTo) {
        this.lastDispatchedTo = lastDispatchedTo;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getRetried() {
        return retried;
    }

    public void setRetried(int retried) {
        this.retried = retried;
    }

    public List<String> getRetryReasons() {
        return retryReasons;
    }

    public void setRetryReasons(List<String> retryReasons) {
        this.retryReasons = retryReasons;
    }

    public List<CouchbaseQueryError> getErrors() {
        return errors;
    }

    public void setErrors(List<CouchbaseQueryError> errors) {
        this.errors = errors;
    }

    public CouchbaseQueryServiceError getService() {
        return service;
    }

    public void setService(CouchbaseQueryServiceError service) {
        this.service = service;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public CouchbaseQueryTimingsServiceError getTimings() {
        return timings;
    }

    public void setTimings(CouchbaseQueryTimingsServiceError timings) {
        this.timings = timings;
    }
}
