package com.couchbase.intellij.workbench.error;

import com.couchbase.client.core.error.CouchbaseException;
import com.google.gson.JsonSyntaxException;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CouchbaseQueryErrorUtilTest {

    private static final String RESULT_JSON = "{\"errors\":[{\"code\":3000,\"message\":\"syntax error\",\"retry\":false}],"
            + "\"completed\":true,\"coreId\":\"core-1\",\"httpStatus\":400,\"idempotent\":true,"
            + "\"lastDispatchedFrom\":\"10.0.0.1\",\"lastDispatchedTo\":\"10.0.0.2\",\"requestId\":17,"
            + "\"requestType\":\"QueryRequest\",\"retried\":2,\"retryReasons\":[\"busy\"],"
            + "\"service\":{\"bucket\":\"b\",\"operationId\":\"op\",\"scope\":\"s\",\"statement\":\"SELEC\",\"type\":\"parsing\"},"
            + "\"timeoutMs\":75000,"
            + "\"timings\":{\"dispatchMicros\":1,\"totalDispatchMicros\":2,\"totalMicros\":3}}";

    @Test
    public void parsesStringMessageIntoSingleError() {
        CouchbaseQueryResultError result = CouchbaseQueryErrorUtil.parseQueryError("boom");

        assertNotNull(result.getErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("boom", result.getErrors().get(0).getMessage());
        assertEquals(0, result.getErrors().get(0).getCode());
        assertFalse(result.getErrors().get(0).isRetry());
    }

    @Test
    public void stringMessageWithQuotesBreaksJsonParsing() {
        // NOTE: possible bug: the message is spliced into a JSON literal without escaping, so a quote
        // in the message makes Gson throw. Pinned as current behavior.
        try {
            CouchbaseQueryErrorUtil.parseQueryError("unexpected \"token\"");
            fail("expected JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            // pinned
        }
    }

    @Test
    public void parsesCouchbaseExceptionJsonAfterFirstBrace() {
        CouchbaseException ex = new CouchbaseException("Query failed: " + RESULT_JSON);

        CouchbaseQueryResultError result = CouchbaseQueryErrorUtil.parseQueryError(ex);

        assertEquals(1, result.getErrors().size());
        CouchbaseQueryError err = result.getErrors().get(0);
        assertEquals(3000, err.getCode());
        assertEquals("syntax error", err.getMessage());
        assertFalse(err.isRetry());
    }

    @Test
    public void mapsTopLevelAndNestedFields() {
        CouchbaseQueryResultError result = CouchbaseQueryErrorUtil.parseQueryError(
                new CouchbaseException("x " + RESULT_JSON));

        assertTrue(result.isCompleted());
        assertEquals("core-1", result.getCoreId());
        assertEquals(400, result.getHttpStatus());
        assertTrue(result.isIdempotent());
        assertEquals("10.0.0.1", result.getLastDispatchedFrom());
        assertEquals("10.0.0.2", result.getLastDispatchedTo());
        assertEquals(17, result.getRequestId());
        assertEquals("QueryRequest", result.getRequestType());
        assertEquals(2, result.getRetried());
        assertEquals(1, result.getRetryReasons().size());
        assertEquals(75000L, result.getTimeoutMs());

        CouchbaseQueryServiceError service = result.getService();
        assertEquals("b", service.getBucket());
        assertEquals("op", service.getOperationId());
        assertEquals("s", service.getScope());
        assertEquals("SELEC", service.getStatement());
        assertEquals("parsing", service.getType());

        CouchbaseQueryTimingsServiceError timings = result.getTimings();
        assertEquals(1, timings.getDispatchMicros());
        assertEquals(2, timings.getTotalDispatchMicros());
        assertEquals(3, timings.getTotalMicros());
    }

    @Test
    public void parsesExecutionExceptionJsonAfterFirstBrace() {
        ExecutionException ex = new ExecutionException("Query failed: " + RESULT_JSON, null);

        CouchbaseQueryResultError result = CouchbaseQueryErrorUtil.parseQueryError(ex);

        assertEquals("syntax error", result.getErrors().get(0).getMessage());
    }

    @Test
    public void missingServiceAndTimingsAreNull() {
        CouchbaseQueryResultError result = CouchbaseQueryErrorUtil.parseQueryError(
                new CouchbaseException("{\"errors\":[{\"code\":1,\"message\":\"m\"}]}"));

        assertNull(result.getService());
        assertNull(result.getTimings());
        assertNull(result.getRetryReasons());
        assertFalse(result.isCompleted());
    }

    @Test
    public void exceptionWithoutJsonThrows() {
        // NOTE: possible bug: indexOf("{") returns -1 when the message has no JSON, and substring(-1) throws.
        try {
            CouchbaseQueryErrorUtil.parseQueryError(new CouchbaseException("no json here"));
            fail("expected StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            // pinned
        }
    }
}
