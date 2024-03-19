package com.couchbase.intellij.tree.iq;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.tree.iq.chat.*;
import com.couchbase.intellij.tree.iq.intents.IntentProcessor;
import com.couchbase.intellij.tree.iq.intents.actions.ActionInterface;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.utils.Subscribable;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.theokanning.openai.completion.chat.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class AbstractIQTest extends BasePlatformTestCase {
    protected static final String IQ_URL = System.getenv("CAPELLA_DOMAIN") + "/v2/organizations/%s/integrations/iq/";
    private static final ChatGptHandler handler = new ChatGptHandler();
    protected static ConversationContext ctx;
    protected static ChatPanel panel;
    protected static IntentProcessor processor = new IntentProcessor();
    protected static CapellaOrganization organization;

    protected static CapellaOrganizationList organizationList;

    protected static void mockCluster() {
        Cluster cluster = Mockito.mock(Cluster.class);
        ActiveCluster activeCluster = Mockito.mock(ActiveCluster.class);
        QueryContext context = Mockito.mock(QueryContext.class);
        Mockito.when(context.getBucket()).thenReturn("default");
        Mockito.when(context.getScope()).thenReturn("_default");
        Mockito.when(activeCluster.getQueryContext()).thenReturn(new Subscribable<>(context));
        Mockito.when(activeCluster.getCluster()).thenReturn(cluster);
        ActiveCluster.setInstance(activeCluster);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        organization = new CapellaOrganization();
        organization.setId("orgid");
        organization.setName("test");
        CapellaOrganization.IQ iq = new CapellaOrganization.IQ();
        iq.setEnabled(true);
        CapellaOrganization.Other other = new CapellaOrganization.Other();
        other.setIsTermsAcceptedForOrg(true);
        iq.setOther(other);
        organization.setIq(iq);
        CapellaOrganizationList.Entry entry = new CapellaOrganizationList.Entry();
        entry.setData(organization);
        organizationList = new CapellaOrganizationList();
        organizationList.setData(Arrays.asList(entry));
    }

    protected ChatCompletionResult send(String message) {
        return send(message, false);
    }
    protected ChatCompletionResult send(String message, boolean isSystem) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        assertNotNull(ctx);
        assertNotNull(panel);
        ChatMessage chatMessage = new ChatMessage(
                isSystem ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(),
                message
        );
        ChatMessageEvent.Starting event = ChatMessageEvent.starting(AbstractIQTest.panel.getChatLink(), chatMessage);
        ctx.addChatMessage(chatMessage);
        List<ChatMessage> messages = ctx.getChatMessages(ctx.getModelType(), chatMessage);
        if (isSystem) {
            messages.add(chatMessage);
        }
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .build();
        return (ChatCompletionResult) handler.handle(AbstractIQTest.ctx, event.initiating(request), Mockito.mock(ChatMessageListener.class)).blockingLast();
    }

    protected String getResponse(ChatCompletionResult response) {
        assertEquals(1, response.getChoices().size());
        return response.getChoices().get(0).getMessage().getContent();
    }

    protected JsonObject getJson(ChatCompletionResult response) {
        return JsonObject.fromJson(getResponse(response));
    }

    protected void assertJsonResponse(ChatCompletionResult response) {
        String message = getResponse(response);
        assertTrue(message.startsWith("{"));
    }

    protected void assertNotJson(ChatCompletionResult response) {
        assertFalse(getResponse(response).trim().charAt(0) == '{');
    }

    protected List<JsonObject> getIntents(ChatCompletionResult response, Class<? extends ActionInterface> action) {
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

    protected void assertResponseTextEquals(ChatCompletionResult actual, String expected) {
        assertEquals(expected, getResponse(actual));
    }

    protected String intentFeedback(ChatCompletionResult result) {
        String response = getResponse(result);
        JsonObject intents = JsonObject.create();
        if (response != "") {
            intents = JsonObject.fromJson(response);
        }
        return processor.generateIntentReturnPrompt(panel, result.getChoices().get(0).getMessage(), intents);
    }
}
