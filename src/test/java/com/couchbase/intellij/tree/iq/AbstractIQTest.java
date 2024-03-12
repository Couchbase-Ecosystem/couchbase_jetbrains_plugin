package com.couchbase.intellij.tree.iq;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.chat.ChatExchangeAbortException;
import com.couchbase.intellij.tree.iq.chat.ChatGptHandler;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.chat.ChatLinkService;
import com.couchbase.intellij.tree.iq.chat.ChatLinkState;
import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.chat.ChatMessageListener;
import com.couchbase.intellij.tree.iq.chat.ConfigurationPage;
import com.couchbase.intellij.tree.iq.chat.ConversationContext;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.intents.actions.ActionInterface;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.workbench.Log;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractIQTest extends BasePlatformTestCase {
    protected static final String IQ_URL = System.getenv("CAPELLA_DOMAIN") + "/v2/organizations/%s/integrations/iq/";
    private static final ChatGptHandler handler = new ChatGptHandler();
    protected static ConversationContext ctx;
    protected static ChatLink link;

    protected void send(String message, Consumer<ChatMessageEvent.ResponseArrived> listener) {
        send(message, false, listener);
    }
    protected void send(String message, boolean isSystem, Consumer<ChatMessageEvent.ResponseArrived> listener) {
        assertNotNull(ctx);
        assertNotNull(link);
        ChatMessage chatMessage = new ChatMessage(
                isSystem ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(),
                message
        );
        ChatMessageEvent.Starting event = ChatMessageEvent.starting(AbstractIQTest.link, chatMessage);
        ctx.addChatMessage(chatMessage);
        List<ChatMessage> messages = ctx.getChatMessages(ctx.getModelType(), chatMessage);
        if (isSystem) {
            messages.add(chatMessage);
        }
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .build();
        handler.handle(AbstractIQTest.ctx, event.initiating(request), new ChatMessageListener() {
            @Override
            public void exchangeStarting(ChatMessageEvent.Starting event) throws ChatExchangeAbortException {

            }

            @Override
            public void exchangeStarted(ChatMessageEvent.Started event) {

            }

            @Override
            public void responseArriving(ChatMessageEvent.ResponseArriving event) {

            }

            @Override
            public void responseArrived(ChatMessageEvent.ResponseArrived event) {
                listener.accept(event);
            }

            @Override
            public void responseCompleted(ChatMessageEvent.ResponseArrived event) {

            }

            @Override
            public void exchangeFailed(ChatMessageEvent.Failed event) {
                throw new RuntimeException("IQ Exchange failed", event == null ? null : event.getCause());
            }

            @Override
            public void exchangeCancelled(ChatMessageEvent.Cancelled event) {

            }
        }).blockingLast();
    }

    protected String getResponse(ChatMessageEvent.ResponseArrived response) {
        assertEquals(1, response.getResponseChoices().size());
        return response.getResponseChoices().get(0).getContent();
    }

    protected JsonObject getJson(ChatMessageEvent.ResponseArrived response) {
        return JsonObject.fromJson(getResponse(response));
    }

    protected void assertJsonResponse(ChatMessageEvent.ResponseArrived response) {
        String message = getResponse(response);
        assertTrue(message.startsWith("{"));
    }

    protected void assertNotJson(ChatMessageEvent.ResponseArrived response) {
        assertFalse(getResponse(response).trim().charAt(0) == '{');
    }

        protected List<JsonObject> getIntents(ChatMessageEvent.ResponseArrived response, Class<? extends ActionInterface> action) {
        List<JsonObject> results = new ArrayList<>();
        JsonObject json = getJson(response);
        assertInstanceOf(json.get("actions"), JsonArray.class);
        JsonArray actions = json.getArray("actions");
        for (int i = 0; i < actions.size(); i++) {
            assertInstanceOf(actions.get(i), JsonObject.class);
            JsonObject intent = actions.getObject(i);
            assertInstanceOf(intent.get("action"), String.class);
            if (intent.getString("action").equals(action.getSimpleName())) {
                results.add(intent);
            }
        }
        return results;
    }

    public void assertCauseMessage(Throwable e, String msg) {
        while (e != null && e.getCause() != e) {
            if (e.getMessage().equals(msg)) {
                return;
            }
            if (e.getCause() == e) {
                break;
            }
            e = e.getCause();
        }
        throw new AssertionError("Exception was not caused by an error with message '" + msg + "'");
    }

    protected void assertResponseTextEquals(ChatMessageEvent.ResponseArrived responseArrived, String fakeConfusion) {
        assertTrue(responseArrived.getResponseChoices().stream()
                .filter(Objects::nonNull)
                .anyMatch(choice -> choice.getContent() != null && choice.getContent().equals(fakeConfusion)));
    }
}
