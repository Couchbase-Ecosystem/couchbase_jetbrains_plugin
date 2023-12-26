package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.intellij.tree.iq.CapellaApiMethods;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.couchbase.intellij.tree.iq.chat.*;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.didalgo.gpt3.ModelType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntentProcessorTest extends BasePlatformTestCase {
    private static final String IQ_URL = System.getenv("CAPELLA_DOMAIN") + "/v2/organizations/%s/integrations/iq/";

    private static final ChatGptHandler handler = new ChatGptHandler();
    private static ConversationContext ctx;
    private static ChatLink link;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IQCredentials credentials = new IQCredentials(System.getenv("IQ_ORG_LOGIN"), System.getenv("IQ_ORG_PASSWD"));
        assertTrue("Please set capella domain and IQ credentials using `CAPELLA_DOMAIN`, `IQ_ORG_ID`, `IQ_ORG_LOGIN`, and `IQ_ORG_PASSWD` envvars", credentials.checkAuthStatus());
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

        link = new ChatLinkService(getProject(), null,  iqGptConfig);
        ctx = new TestConversationContext(iqGptConfig.withSystemPrompt(IQWindowContent::systemPrompt));
    }

    private void send(String message, Consumer<ChatMessageEvent.ResponseArrived> listener) throws InterruptedException {
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        ChatMessageEvent.Starting event = ChatMessageEvent.starting(this.link, userMessage);
        ChatCompletionRequestProvider ccrp = new ChatCompletionRequestProvider();
        ChatCompletionRequest request = ccrp.chatCompletionRequest(this.ctx, userMessage)
                        .build();
        handler.handle(this.ctx, event.initiating(request), new ChatMessageListener() {
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

    @Test
    public void testJson() throws Exception {
        send("list all boolean fields in the airport collection", response -> {
            assertEquals(1, response.getResponseChoices().size());
            assertTrue(response.getUserMessage().toString().startsWith("{"));
        });
    }

    private static class TestConversationContext implements ConversationContext {

        private final ConfigurationPage config;

        public TestConversationContext(ConfigurationPage configurationPage) {
            this.config = configurationPage;
        }

        @Override
        public void clear() {

        }

        @Override
        public String getModelPage() {
            return config.getModelPage();
        }

        @Override
        public List<? extends TextContent> getLastPostedCodeFragments() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public void setLastPostedCodeFragments(List<? extends TextContent> textContents) {

        }

        @Override
        public void addChatMessage(ChatMessage message) {

        }

        @Override
        public ModelType getModelType() {
            String modelName = config.getModelName();
            return ModelType.forModel(modelName).orElseThrow();
        }

        @Override
        public List<ChatMessage> getChatMessages(ModelType model, ChatMessage userMessage) {
            return Arrays.asList(userMessage);
        }
    }
}