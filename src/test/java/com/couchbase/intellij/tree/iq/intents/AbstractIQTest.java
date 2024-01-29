package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.IQWindowContent;
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
import java.util.function.Consumer;

public abstract class AbstractIQTest extends BasePlatformTestCase {
    private static final String IQ_URL = System.getenv("CAPELLA_DOMAIN") + "/v2/organizations/%s/integrations/iq/";
    private static final ChatGptHandler handler = new ChatGptHandler();
    private static ConversationContext ctx;
    private static ChatLink link;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IQCredentials credentials = new IQCredentials(System.getenv("IQ_ORG_LOGIN"), System.getenv("IQ_ORG_PASSWD"));
        assertTrue("Please set capella domain and IQ credentials using `CAPELLA_DOMAIN`, `IQ_ORG_ID`, `IQ_ORG_LOGIN`, and `IQ_ORG_PASSWD` envvars", credentials.doLogin());
        String orgId = System.getenv("IQ_ORG_ID");
        final String iqUrl = String.format(IQ_URL, orgId);
        OpenAISettingsState.OpenAIConfig iqGptConfig = new OpenAISettingsState.OpenAIConfig();
        OpenAISettingsState.getInstance().setGpt4Config(iqGptConfig);
        OpenAISettingsState.getInstance().setEnableInitialMessage(false);
        iqGptConfig.setApiKey(credentials.getAuth().getJwt());
        iqGptConfig.setEnableStreamResponse(false);
        iqGptConfig.setModelName("gpt-4");
        iqGptConfig.setApiEndpointUrl(iqUrl);
        iqGptConfig.setEnableCustomApiEndpointUrl(true);
        ConfigurationPage cp = iqGptConfig.withSystemPrompt(IQWindowContent::systemPrompt);
        Log.setLevel(3);
        Log.setPrinter(new Log.StdoutPrinter());

        link = new ChatLinkService(getProject(), null, cp);
        ctx = new ChatLinkState(cp);
    }

    protected void send(String message, Consumer<ChatMessageEvent.ResponseArrived> listener) {
        send(message, false, listener);
    }
    protected void send(String message, boolean isSystem, Consumer<ChatMessageEvent.ResponseArrived> listener) {
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
                throw new RuntimeException("IQ Exchange failed", event.getCause());
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
}
