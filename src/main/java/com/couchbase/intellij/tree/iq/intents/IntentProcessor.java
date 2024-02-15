package com.couchbase.intellij.tree.iq.intents;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.GetAllQueryIndexesOptions;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.CollectionRelationships;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tree.RelationshipSettingsManager;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.couchbase.intellij.tree.iq.chat.ChatGptHandler;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.couchbase.intellij.tree.iq.intents.actions.ActionInterface;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlinx.html.P;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.Action;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IntentProcessor {
    private static String secondaryPrompt;
    private static String intentPrompt;
    private static Map<String, ActionInterface> loadedActions = new HashMap<>();

    private ActionInterface getAction(String name) {
        if (!loadedActions.containsKey(name)) {
            String actionClassName = String.format("%s.actions.%s", IntentProcessor.class.getPackageName(), name);
            try {
                Class actionClass = Class.forName(actionClassName);
                if (!ActionInterface.class.isAssignableFrom(actionClass)) {
                    throw new RuntimeException(String.format("Action class '%s' does not implement '%s'", actionClassName, ActionInterface.class.getCanonicalName()));
                } else {
                    ActionInterface action = (ActionInterface) actionClass.getConstructor().newInstance();
                    loadedActions.put(name, action);
                    return action;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return loadedActions.get(name);
    }

    public Disposable process(ChatPanel chat, ChatMessage userMessage, JsonObject intents) {
        final ChatLink link = chat.getChatLink();
        StringBuilder intentPrompt = new StringBuilder();
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        QueryContext windowContext = IQWindowContent.getInstance().map(IQWindowContent::getClusterContext).orElse(null);
        if (activeCluster == null || activeCluster.getCluster() == null) {
            intentPrompt.append("Respond once by only telling the user that, in order to fulfill their request, they first need to connect their IDE plugin to Couchbase cluster using the 'Explorer' tab. Do not provide any other suggestions how to perform requested action in this response.");
        } else if (intents.containsKey("actions")) {
            JsonArray detectedActions = intents.getArray("actions");
            for (int i = 0; i < detectedActions.size(); i++) {
                JsonObject intent = null;
                if (detectedActions.get(i) instanceof String) {
                    intent = JsonObject.create();
                    intent.put("action", detectedActions.getString(i));
                } else {
                    intent = detectedActions.getObject(i);
                }
                ActionInterface action = getAction(intent.getString("action"));
                if (action != null) {
                    String bucketName = null, scopeName = null;
                    if (intent.containsKey("bucketName")) {
                        bucketName = intent.getString("bucketName");
                        if (intent.containsKey("scopeName")) {
                            scopeName = intent.getString("scopeName");
                        }
                    } else if (windowContext != null) {
                        bucketName = windowContext.getBucket();
                        scopeName = windowContext.getScope();
                    }
                    String prompt = action.fire(chat.getProject(), bucketName, scopeName, intents, intent);
                    if (prompt != null) {
                        intentPrompt.append(prompt);
                    }
                }
            }
        }

        if (activeCluster != null) {
            if (intents.containsKey("collections")) {
                JsonArray collections = intents.getArray("collections");
                appendCollectionSchema(collections, intentPrompt);
                appendCollectionIndexes(collections, intentPrompt);
                appendCollectionRelations(collections, intentPrompt);
            }
        }

        intentPrompt.append("Now answer the user's question given this additional information and instructions and then continue working according to the original system prompt");
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

    private void appendCollectionIndexes(@NotNull JsonArray collections, @NotNull StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            Cluster cluster = activeCluster.getCluster();
            if (cluster != null) {
                for (int i = 0; i < Math.min(collections.size(), 3); i++) {
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
                                                    intentPrompt.append(String.format("Indexes on collection '%s.%s.%s': %s\n", bucket.getName(), scope.getName(), collectionName, collecitonIndexes.toString()));
                                        });
                            });
                        });
                    }
                }
            }
        }
    }

    private void appendCollectionRelations(@NotNull JsonArray collections, @NotNull StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            RelationshipStorage relationshipStorage = RelationshipStorage.getInstance();
            if (relationshipStorage != null) {
                CollectionRelationships relationshipHolder = relationshipStorage.getValue();
                if (relationshipHolder != null) {
                    Map<String, Map<String, String>> allRelationships = relationshipHolder.getRelationships();
                    if (allRelationships != null) {
                        Map<String, String> clusterRelationships = allRelationships.get(activeCluster.getId());
                        if (clusterRelationships != null) {
                            for (int i = 0; i < Math.min(collections.size(), 3); i++) {
                                String collectionName = collections.getString(i);
                                activeCluster.getChildren().forEach(bucket -> {
                                    bucket.getChildren().forEach(scope -> {
                                        scope.getChildren().stream()
                                                .filter(collection -> collectionName.equals(collection.getName()))
                                                .forEach(collection -> {
                                                    String collectionRef = String.format("%s.%s.%s", bucket.getName(), scope.getName(), collection.getName());
                                                    JsonObject collectionRelationships = JsonObject.create();
                                                    clusterRelationships.entrySet().stream()
                                                            .filter(e -> e.getKey().startsWith(collectionRef) || e.getValue().startsWith(collectionRef))
                                                            .forEach(e -> {
                                                                collectionRelationships.put(e.getKey(), e.getValue());
                                                            });

                                                    if (!collectionRelationships.isEmpty()) {
                                                        intentPrompt.append(String.format("relationships for collection '%s.%s.%s': %s\n", bucket.getName(), scope.getName(), collectionName, collectionRelationships));
                                                    }
                                                });
                                    });
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    private void appendCollectionSchema(@NotNull JsonArray collections, @NotNull StringBuilder intentPrompt) {
        ActiveCluster activeCluster = ActiveCluster.getInstance();
        if (activeCluster != null) {
            for (int i = 0; i < Math.min(collections.size(), 3); i++) {
                String collectionName = collections.getString(i);
                JsonArray structures = JsonArray.create();
                activeCluster.getChildren().forEach(bucket -> {
                    bucket.getChildren().forEach(scope -> {
                        scope.getChildren().stream()
                                .filter(collection -> collectionName.equals(collection.getName()))
                                .forEach(collection -> {
                                    structures.add(collection.toJson());
                                    intentPrompt.append(String.format("schema for collection `%s`.`%s`.`%s`: %s\n", bucket.getName(), scope.getName(), collectionName, structures.toString()));
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
