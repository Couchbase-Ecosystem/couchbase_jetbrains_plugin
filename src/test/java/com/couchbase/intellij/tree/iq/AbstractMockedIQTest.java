package com.couchbase.intellij.tree.iq;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.chat.ChatLinkService;
import com.couchbase.intellij.tree.iq.chat.ChatLinkState;
import com.couchbase.intellij.tree.iq.chat.ConfigurationPage;
import com.couchbase.intellij.tree.iq.core.CapellaAuth;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.spi.iq.CouchbaseIQServiceProvider;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.workbench.Log;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.mockito.Mockito;

import java.util.UUID;

public abstract class AbstractMockedIQTest extends AbstractIQTest {
    private static final String GPT_MODEL = "gpt-3.5-turbo-0125";
    protected static final String IQ_URL = "%s/v2/organizations/%s/integrations/iq/";
    private int port;
    private MockWebServer server;
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new MockWebServer();
        int port = 42000;
        do {
            try {
                server.start(port);
                break;
            } catch (Exception e) {

            }
        } while (++port < 43000);
        Log.info("Mock IQ server started at port %d", port);

        String domain = String.format("http://localhost:%d", port);
        CapellaApiMethods.setCapellaDomain(domain);
        IQCredentials credentials = Mockito.mock(IQCredentials.class);
        CapellaAuth auth = Mockito.mock(CapellaAuth.class);
        Mockito.when(credentials.getAuth()).thenReturn(auth);
        Mockito.when(auth.getJwt()).thenReturn("fakejwt");
        String orgId = UUID.randomUUID().toString();
        final String iqUrl = String.format(IQ_URL, domain, orgId);
        OpenAISettingsState.OpenAIConfig iqGptConfig = new OpenAISettingsState.OpenAIConfig();
        OpenAISettingsState.getInstance().setGpt4Config(iqGptConfig);
        OpenAISettingsState.getInstance().setEnableInitialMessage(false);
        iqGptConfig.setApiKey(credentials.getAuth().getJwt());
        iqGptConfig.setEnableStreamResponse(false);
        iqGptConfig.setModelName(CouchbaseIQServiceProvider.MODEL);
        iqGptConfig.setApiEndpointUrl(iqUrl);
        iqGptConfig.setEnableCustomApiEndpointUrl(true);
        ConfigurationPage cp = iqGptConfig.withSystemPrompt(IQWindowContent::systemPrompt);
        Log.setLevel(3);
        Log.setPrinter(new Log.StdoutPrinter());

        panel = new ChatPanel(getProject(), iqGptConfig, organizationList, organization, Mockito.mock(ChatPanel.LogoutListener.class), Mockito.mock(ChatPanel.OrganizationListener.class));
        ctx = new ChatLinkState(cp);
    }

    @Override
    protected void tearDown() throws Exception {
        server.close();
    }

    protected void enqueueResponse(String text) {
        JsonObject packet = JsonObject.create();
        packet.put("id", UUID.randomUUID().toString());
        packet.put("object", "chat.completion");
        packet.put("created", (int) System.currentTimeMillis() / 1000);
        packet.put("model", GPT_MODEL);
        JsonArray choices = JsonArray.create();
        JsonObject choice = JsonObject.create();
        choices.add(choice);
        choice.put("index", 0);
        JsonObject message = JsonObject.create();
        message.put("role", "assistant");
        message.put("content", text);
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        packet.put("choices", choices);

        this.server.enqueue(new MockResponse().setResponseCode(200).setBody(packet.toString()));
    }

    protected void enqueueResponse(JsonObject json) {
        enqueueResponse(json.toString());
    }

    protected void enqueueError(int httpCode, int errCode, String error) {
        JsonObject packet = JsonObject.create();
        packet.put("httpStatusCode", httpCode);
        packet.put("code", errCode);
        packet.put("message", error);
        packet.put("hint", "ruserious?");
        this.server.enqueue(new MockResponse().setResponseCode(httpCode).setBody(packet.toString()));
    }
}
