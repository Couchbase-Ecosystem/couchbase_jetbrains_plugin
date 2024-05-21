package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.chat.ChatLinkService;
import com.couchbase.intellij.tree.iq.chat.ChatLinkState;
import com.couchbase.intellij.tree.iq.chat.ConfigurationPage;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.spi.iq.CouchbaseIQServiceProvider;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.workbench.Log;
import org.mockito.Mockito;

public abstract class AbstractCapellaTest extends AbstractIQTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assertTrue("Please set Capella domain using `CAPELLA_DOMAIN` envvar", System.getenv().containsKey("CAPELLA_DOMAIN"));
        CapellaApiMethods.setCapellaDomain(System.getenv("CAPELLA_DOMAIN"));
        IQCredentials credentials = new IQCredentials(System.getenv("IQ_ORG_LOGIN"), System.getenv("IQ_ORG_PASSWD"));
        assertTrue("Please set IQ credentials using `IQ_ORG_ID`, `IQ_ORG_LOGIN`, and `IQ_ORG_PASSWD` envvars", credentials.doLogin());
        String orgId = System.getenv("IQ_ORG_ID");
        assertTrue("Please set IQ org id using `IQ_ORG_ID` envvar", orgId != null && orgId.length() > 0);
        final String iqUrl = String.format(IQ_URL, orgId);
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

        ChatLinkService link = new ChatLinkService(getProject(), null, cp);
        panel  = new ChatPanel(getProject(), iqGptConfig, Mockito.mock(CapellaOrganizationList.class), Mockito.mock(CapellaOrganization.class), Mockito.mock(ChatPanel.LogoutListener.class), Mockito.mock(ChatPanel.OrganizationListener.class));
        ctx = new ChatLinkState(cp);
    }

}
