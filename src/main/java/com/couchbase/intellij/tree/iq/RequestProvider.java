package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.core.TokenManager;
import com.couchbase.intellij.tree.iq.core.builder.OfficialBuilder;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.CouchbaseIQPanel;
import com.intellij.openapi.project.Project;

import java.util.Map;


public class RequestProvider {

    //public static final String OFFICIAL_CONVERSATION_URL = "https://ai.fakeopen.com/api/conversation";
    public static final String OFFICIAL_GPT35_TURBO_URL = "https://api.openai.com/v1/chat/completions";
    private Project myProject;
    private String url;
    private String data;
    private Map<String, String> header;

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public RequestProvider create(CouchbaseIQPanel couchbaseIQPanel, String question) {
        myProject = couchbaseIQPanel.getProject();
        RequestProvider provider = new RequestProvider();

        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (couchbaseIQPanel.isChatGPTModel()) {
            if (instance.enableCustomizeChatGPTUrl) {
                provider.url = instance.customizeUrl;
            } else {
                provider.url = OFFICIAL_GPT35_TURBO_URL;
            }
            provider.header = TokenManager.getInstance().getChatGPTHeaders();
            provider.data = OfficialBuilder.buildChatGPT(myProject,question).toString();
        } else {
            if (instance.enableCustomizeGpt35TurboUrl) {
                provider.url = instance.gpt35TurboUrl;
            } else {
                provider.url = OFFICIAL_GPT35_TURBO_URL;
            }
            provider.header = TokenManager.getInstance().getGPT35TurboHeaders();
            if (instance.enableContext) {
                provider.data = OfficialBuilder.buildGpt35Turbo(question, couchbaseIQPanel.getContentPanel()).toString();
            } else {
                provider.data = OfficialBuilder.buildGpt35Turbo(question).toString();
            }
        }
        return provider;
    }
}
