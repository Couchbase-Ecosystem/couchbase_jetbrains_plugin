package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.intellij.tree.iq.AbstractCapellaTest;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CapellaIntentProcessorTest extends AbstractCapellaTest {
    @Test
    public void testRespondsWithJson() throws Exception {
        assertJsonResponse(send("list all boolean fields in the airport collection"));
        assertJsonResponse(send("create a collection named 'test'"));
        assertJsonResponse(send("open an airport with id 'UULL'"));
    }
}