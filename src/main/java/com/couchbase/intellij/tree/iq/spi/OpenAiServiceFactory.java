package com.couchbase.intellij.tree.iq.spi;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.intellij.openapi.application.ApplicationManager;
import com.theokanning.openai.service.OpenAiService;

public class OpenAiServiceFactory {

    public OpenAiService create(String group) {
        return create(group, OpenAISettingsState.getInstance());
    }

    public OpenAiService create(String group, OpenAISettingsState settings) {
        var completionUrl = settings.getConfigurationPage(group).getApiEndpointUrl();
        return ApplicationManager.getApplication().getService(OpenAiServiceProviderRegistry.class)
                .getProviderForCompletionUrl(completionUrl)
                .createService(group, settings);
    }
}
