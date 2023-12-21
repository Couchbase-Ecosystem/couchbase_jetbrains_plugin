package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.GetAllQueryIndexesOptions;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.couchbase.intellij.tree.iq.chat.ChatGptHandler;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.workbench.Log;
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

    public Disposable process(ChatPanel chat, ChatMessage userMessage, JsonObject intents) {
        final ChatLink link = chat.getChatLink();
        StringBuilder intentPrompt = new StringBuilder();
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster == null || activeCluster.getCluster() == null) {
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

        if (activeCluster != null) {
            if (intents.containsKey("collections")) {
                JsonArray collections = intents.getArray("collections");
                appendCollectionSchema(collections, intentPrompt);
                appendCollectionIndexes(collections, intentPrompt);
            }
        }

        if (intentPrompt.isEmpty()) {
            intentPrompt.append(getSecondaryPrompt());
        } else {
            intentPrompt.append("Now answer the user's question given this additional information and then continue working according to the original system prompt");
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

        Log.info(String.format("IQ intent prompt: %s", intentPrompt.toString()));
        chat.getQuestion().addIntentPrompt(intentPrompt.toString());

        ChatMessageEvent.Starting event = ChatMessageEvent.starting(link, userMessage);
        return application.getService(ChatGptHandler.class)
                .handle(link.getConversationContext(), event.initiating(chatCompletionRequest), link.getListener())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void appendCollectionIndexes(JsonArray collections, StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            Cluster cluster = activeCluster.getCluster();
            if (cluster != null) {
                for (int i = 0; i < collections.size(); i++) {
                    String collectionName = collections.getString(i);
                    JsonObject collecitonIndexes = JsonObject.create();
                    if (collectionName != null && !collectionName.isBlank()) {
                        activeCluster.getChildren().forEach(bucket -> {
                            bucket.getChildren().forEach(scope -> {
                                scope.getChildren().stream()
                                        .filter(collection -> collectionName.equalsIgnoreCase(collection.getName()))
                                        .forEach(collection -> {
                                                    Collection c = cluster.bucket(bucket.getName()).scope(scope.getName()).collection(collectionName);
                                                    c.queryIndexes().getAllIndexes().forEach(index -> {
                                                        collecitonIndexes.put(index.name(), index.indexKey());
                                                    });
                                                    intentPrompt.append(String.format("The following is the list of indexes and included in them fields on collection '%s' in bucket '%s' and scope '%s': %s\n", collectionName, collecitonIndexes.toString(), bucket.getName(), scope.getName()));
                                        });
                            });
                        });
                    }
                }
            }
        }
    }

    private void appendCollectionRelations(JsonArray collections, StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            for (int i = 0; i < collections.size(); i++) {

            }
        }
    }

    private void appendCollectionSchema(JsonArray collections, StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            for (int i = 0; i < collections.size(); i++) {
                String collectionName = collections.getString(i);
                JsonArray structures = JsonArray.create();
                activeCluster.getChildren().forEach(bucket -> {
                    bucket.getChildren().forEach(scope -> {
                        scope.getChildren().stream()
                                .filter(collection -> collectionName.equals(collection.getName()))
                                .forEach(collection -> {
                                    structures.add(collection.toJson());
                                    intentPrompt.append(String.format("The following is the list of possible document structures inside collection '%s' in bucket '%s' and scope '%s': %s\n", collectionName, structures.toString(), bucket.getName(), scope.getName()));
                                });
                    });
                });
            }
        }
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
