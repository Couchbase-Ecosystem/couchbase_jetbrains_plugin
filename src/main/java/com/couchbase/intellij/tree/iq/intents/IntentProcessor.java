package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.chat.ChatGptHandler;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.Method;

public class IntentProcessor {
    public Disposable process(ChatLink link, ChatMessage userMessage, String intentJson) {
        JsonObject intent = JsonObject.fromJson(intentJson);
        StringBuilder intentPrompt = new StringBuilder();
        if (intent.containsKey("actions")) {
            JsonArray detectedActions = intent.getArray("actions");
            for (int i = 0; i < detectedActions.size(); i++) {
                String actionClassName = String.format("%s.actions.%s", IntentProcessor.class.getPackageName(), detectedActions.get(i));
                try {
                    Class actionClass = Class.forName(actionClassName);
                    Method fire = actionClass.getMethod("fire", JsonObject.class);
                    String prompt = (String) fire.invoke(null, intent);
                    if (prompt != null) {
                        intentPrompt.append(prompt);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        var application = ApplicationManager.getApplication();
        var chatCompletionRequestProvider = application.getService(ChatCompletionRequestProvider.class);
        var chatCompletionRequest = chatCompletionRequestProvider.chatCompletionRequest(link.getConversationContext(), userMessage)
                .build();

        // replace default system prompt
        chatCompletionRequest.getMessages().set(0, new ChatMessage(ChatMessageRole.SYSTEM.value(), intentPrompt.toString()));

        ChatMessageEvent.Starting event = ChatMessageEvent.starting(link, userMessage);
        return application.getService(ChatGptHandler.class)
                .handle(link.getConversationContext(), event.initiating(chatCompletionRequest), link.getListener())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
