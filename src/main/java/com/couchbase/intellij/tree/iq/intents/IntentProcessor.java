package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.couchbase.intellij.tree.iq.chat.ChatGptHandler;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

public class IntentProcessor {
    private static String secondaryPrompt;
    private static String intentPrompt;

    public Disposable process(ChatLink link, ChatMessage userMessage, String intentJson) {
        JsonObject intents = JsonObject.fromJson(intentJson);
        StringBuilder intentPrompt = new StringBuilder();
        if (ActiveCluster.getInstance() == null || ActiveCluster.getInstance().getCluster() == null) {
            intentPrompt.append("Respond once by only telling the user that, in order to fulfill their request, they first need to connect their IDE plugin to Couchbase cluster using the 'Explorer' tab. Do not provide any other suggestions how to perform requested action in this response.");
        } else if (intents.containsKey("actions")) {
            JsonArray detectedActions = intents.getArray("actions");
            for (int i = 0; i < detectedActions.size(); i++) {
                JsonObject intent = detectedActions.getObject(i);
                String actionClassName = String.format("%s.actions.%s", IntentProcessor.class.getPackageName(), intent.getString("action"));

                try {
                    Class actionClass = Class.forName(actionClassName);
                    Method fire = actionClass.getMethod("fire", JsonObject.class, JsonObject.class);
                    String prompt = (String) fire.invoke(null, intents, intent);
                    if (prompt != null) {
                        intentPrompt.append(prompt);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (intentPrompt.isEmpty()) {
            intentPrompt.append(getSecondaryPrompt());
        }

        var application = ApplicationManager.getApplication();
        var chatCompletionRequestProvider = application.getService(ChatCompletionRequestProvider.class);
        var chatCompletionRequest = chatCompletionRequestProvider.chatCompletionRequest(link.getConversationContext(), userMessage)
                .build();

        // replace previous system prompts
        chatCompletionRequest.getMessages().removeAll(chatCompletionRequest.getMessages().stream()
                        .skip(1)
                        .filter(message -> ChatMessageRole.SYSTEM.value().equals(message.getRole()))
                                .collect(Collectors.toList()));
        chatCompletionRequest.getMessages().add(new ChatMessage(ChatMessageRole.SYSTEM.value(), intentPrompt.toString()));

        ChatMessageEvent.Starting event = ChatMessageEvent.starting(link, userMessage);
        return application.getService(ChatGptHandler.class)
                .handle(link.getConversationContext(), event.initiating(chatCompletionRequest), link.getListener())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private static String getSecondaryPrompt() {
        if (secondaryPrompt == null) {
            try {
                InputStream is = IQWindowContent.class.getResourceAsStream("/iq/secondary_prompt.txt");
                secondaryPrompt = IOUtils.toString(new InputStreamReader(is));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return secondaryPrompt;
    }

    private static String getIntentPrompt() {
        if (intentPrompt == null) {
            try {
                InputStream is = IQWindowContent.class.getResourceAsStream("/iq/intent_prompt.txt");
                intentPrompt = IOUtils.toString(new InputStreamReader(is));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return intentPrompt;
    }
}
