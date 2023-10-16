package com.couchbase.intellij.tree.iq.core;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class ConversationManager {

    private String parentMessageId = UUID.randomUUID().toString();
    private String conversationId = "";

    public static ConversationManager getInstance(@NotNull Project project) {
        return project.getService(ConversationManager.class);
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
