package com.couchbase.intellij.tree.iq.spi;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.theokanning.openai.service.OpenAiService;

public interface OpenAiServiceProvider {

    boolean supportsEndpoint(String url);

    OpenAiService createService(String group, OpenAISettingsState settings);
}
