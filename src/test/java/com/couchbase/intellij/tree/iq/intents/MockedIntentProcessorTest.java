package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.AbstractMockedIQTest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;

public class MockedIntentProcessorTest extends AbstractMockedIQTest {

    public void testErrorHandling() throws Exception {
        enqueueError(500, 500, "test error");
        try {
            send("boop");
        } catch (Throwable e) {
            assertCauseMessage(e, "test error");
            return;
        }
        assertFalse(true);
    }

    public void testEmptyJsonResponse() throws Exception {
        mockCluster();
        enqueueResponse("");
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));
        enqueueResponse("{ }");
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));
        enqueueResponse("{\n}");
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));

        JsonObject response = JsonObject.create();
        response.putNull("actions");
        enqueueResponse(response);
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));

        JsonArray actions = JsonArray.create();
        response.put("actions", actions);
        enqueueResponse(response);
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));

        JsonObject action = JsonObject.create();
        actions.add(action);
        enqueueResponse(response);
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("boop")));
    }

    public void testRegularResponses() throws Exception {
        // validate that regular responses are passed correctly
        enqueueResponse("boop");
        ChatCompletionResult response = send("beep");
        assertResponseTextEquals(response, "boop");
    }

    public void testQueryResponses() throws Exception {
        mockCluster();
        enqueueResponse("{\"ids\": [],\"query\": \"SELECT * FROM `airport` WHERE city = 'Washington, DC'\",\"fields\": [],\"collections\": [\"airport\"]}");
        assertEquals(IntentProcessor.PROMPT_ANSWER_USER, intentFeedback(send("beep")));
    }
}
