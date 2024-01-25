package com.couchbase.intellij.tree.iq.intents;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntentProcessorTest extends AbstractIQTest {
    @Test
    public void testRespondsWithJson() throws Exception {
        send("list all boolean fields in the airport collection", this::assertJsonResponse);
        send("create a collection named 'test'", this::assertJsonResponse);
        send("open an airport with id 'UULL'", this::assertJsonResponse);
    }

}