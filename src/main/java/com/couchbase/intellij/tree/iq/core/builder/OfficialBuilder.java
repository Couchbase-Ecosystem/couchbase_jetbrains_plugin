package com.couchbase.intellij.tree.iq.core.builder;

import com.couchbase.intellij.tree.iq.core.ConversationManager;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.MessageGroupComponent;
import com.couchbase.intellij.tree.iq.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class OfficialBuilder {

    public static JsonObject buildChatGPT(@NotNull Project project, String text) {
        JsonObject result = new JsonObject();
        result.addProperty("action","next");

        JsonArray messages = new JsonArray();
        JsonObject message0 = new JsonObject();
        message0.addProperty("id", UUID.randomUUID().toString());
        message0.addProperty("role", "user");

        JsonObject content = new JsonObject();
        content.addProperty("content_type","text");
        JsonArray parts = new JsonArray();
        parts.add(text);
        content.add("parts",parts);

        JsonObject author = new JsonObject();
        author.addProperty("role", "user");
        message0.add("content", content);
        message0.add("author", author);
        messages.add(message0);
        result.add("messages", messages);

        result.addProperty("parent_message_id", ConversationManager.getInstance(project).getParentMessageId());
        String conversationId = ConversationManager.getInstance(project).getConversationId();
        if (StringUtil.isNotEmpty(conversationId)) {
            result.addProperty("conversation_id",conversationId);
        }
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.addProperty("model",settingsState.chatGptModel);
        return result;
    }

    public static JsonObject buildGpt35Turbo(String text) {
        JsonObject result = new JsonObject();
        result.addProperty("model","gpt-3.5-turbo");
        JsonArray messages = new JsonArray();
        JsonObject message0 = new JsonObject();
        message0.addProperty("role","user");
        message0.addProperty("content",text);
        messages.add(message0);
        result.add("messages",messages);
        if (OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
            result.addProperty("stream",true);
        }
        return result;
    }

    public static JsonObject buildGpt35Turbo(String text, MessageGroupComponent component) {
        JsonObject result = new JsonObject();
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.addProperty("model",settingsState.gpt35Model);
        component.getMessages().add(userMessage(text));
        result.add("messages",component.getMessages());
        if (OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
            result.addProperty("stream",true);
        }
        return result;
    }

    private static JsonObject message(String role, String text) {
        JsonObject message = new JsonObject();
        message.addProperty("role",role);
        message.addProperty("content",text);
        return message;
    }

    public static JsonObject userMessage(String text) {
        return message("user",text);
    }

    public static JsonObject systemMessage(String text) {
        return message("system",text);
    }

    public static JsonObject assistantMessage(String text) {
        return message("assistant",text);
    }
}
