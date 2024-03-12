package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.AbstractMockedIQTest;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;

public class MockedIntentProcessorTest extends AbstractMockedIQTest {

    public void testErrorHandling() throws Exception {
        enqueueError(500, 500, "test error");
        try {
            send("boop", responseArrived -> {
            });
        } catch (Throwable e) {
            assertCauseMessage(e, "test error");
            return;
        }
        assertFalse(true);
    }

    public void testEmptyJsonResponse() throws Exception {
        enqueueResponse("");
        send("boop", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });
        enqueueResponse("{ }");
        send("boop", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });
        enqueueResponse("{\n}");
        send("boop", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });

        JsonObject response = JsonObject.create();
        response.putNull("actions");
        enqueueResponse(response);
        send("boop me!", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });

        JsonArray actions = JsonArray.create();
        response.put("actions", actions);
        enqueueResponse(response);
        send("boop me!", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });

        JsonObject action = JsonObject.create();
        actions.add(action);
        enqueueResponse(response);
        send("boop me!", responseArrived -> {
            assertResponseTextEquals(responseArrived, ChatPanel.FAKE_CONFUSION);
        });

        // validate that regular responses are passed correctly
        enqueueResponse("boop");
        send("beep", responseArrived -> {
            assertResponseTextEquals(responseArrived, "boop");
        });
    }

}
