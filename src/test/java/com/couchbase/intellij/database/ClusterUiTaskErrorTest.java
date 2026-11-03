package com.couchbase.intellij.database;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClusterUiTaskErrorTest {
    @Test
    public void errorsReachTheFailureCallbackAsExceptions() {
        AssertionError error = new AssertionError("boom");
        Exception reported = ClusterUiTask.asException(error);
        assertSame(error, reported.getCause());
        assertTrue(reported.getMessage().contains("boom"));
        IllegalArgumentException exception = new IllegalArgumentException();
        assertSame(exception, ClusterUiTask.asException(exception));
    }
}
