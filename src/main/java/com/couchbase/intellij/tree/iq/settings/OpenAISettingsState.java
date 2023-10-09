
package com.couchbase.intellij.tree.iq.settings;

import com.couchbase.intellij.tree.CouchbaseWindowFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.obiscr.OpenAIProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@State(
        name = "com.obiscr.chatgpt.settings.OpenAISettingsState",
        storages = @Storage("ChatGPTSettingsPlugin.xml")
)
public class OpenAISettingsState implements PersistentStateComponent<OpenAISettingsState> {

  public String customizeUrl = "";

  public String readTimeout = "50000";
  public String connectionTimeout = "50000";
  public Boolean enableProxy = false;
  public Boolean enableAvatar = true;
  public SettingConfiguration.SettingProxyType proxyType =
          SettingConfiguration.SettingProxyType.DIRECT;

  public String proxyHostname = "";
  public String proxyPort = "10000";

  public String accessToken = "";
  public String expireTime = "";
  public String imageUrl = "https://cdn.auth0.com/avatars/me.png";
  public String apiKey = "";
  public Map<Integer,String> contentOrder = new HashMap<>(){{
    put(2, CouchbaseWindowFactory.GPT35_TRUBO_CONTENT_NAME);
  }};

  public Boolean enableLineWarp = true;

  @Deprecated
  public List<String> customActionsPrefix = new ArrayList<>();

  public String chatGptModel = "text-davinci-002-render-sha";
  public String gpt35Model = "gpt-3.5-turbo";
  public Boolean enableContext = false;
  public String assistantApiKey = "";
  public Boolean enableTokenConsumption = false;
  public Boolean enableGPT35StreamResponse = false;
  public String gpt35TurboUrl = "https://api.openai.com/v1/chat/completions";

  public Boolean enableProxyAuth = false;
  public String proxyUsername = "";
  public String proxyPassword = "";

  public Boolean enableCustomizeGpt35TurboUrl = false;
  public Boolean enableCustomizeChatGPTUrl = false;

  public String gpt35RoleText = "You are a helpful language assistant";

  public String prompt1Name = "Find Bug";
  public String prompt1Value = "Find the bug in the code below:";
  public String prompt2Name = "Optimize Code";
  public String prompt2Value = "Optimize this code:";
  public String prompt3Name = "My Default";
  public String prompt3Value = "My Default prompt:";

  @Tag("customPrompts")
  public Map<String, String> customPrompts = new HashMap<>();

  public static OpenAISettingsState getInstance() {
    return ApplicationManager.getApplication().getService(OpenAISettingsState.class);
  }

  @Nullable
  @Override
  public OpenAISettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void reload() {
    loadState(this);
  }

  public Proxy getProxy() {
    Proxy proxy = null;
    if (enableProxy) {
      Proxy.Type type = proxyType ==
              SettingConfiguration.SettingProxyType.HTTP ? Proxy.Type.HTTP :
              proxyType == SettingConfiguration.SettingProxyType.SOCKS ? Proxy.Type.SOCKS :
                      Proxy.Type.DIRECT;
      proxy = new OpenAIProxy(proxyHostname, Integer.parseInt(proxyPort),
              type).build();
    }
    return proxy;
  }
}
