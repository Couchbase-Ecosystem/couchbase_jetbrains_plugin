package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.text.TextContent;
import com.didalgo.gpt3.ModelType;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class IQLinkState implements ConversationContext {

    List<? extends TextContent> lastPostedCodeFragments = new ArrayList<>();
    @Override
    public void clear() {
        this.lastPostedCodeFragments = new ArrayList<>();
    }

    @Override
    public String getModelPage() {
        return "Capella IQ";
    }

    @Override
    public List<? extends TextContent> getLastPostedCodeFragments() {
        return lastPostedCodeFragments;
    }

    @Override
    public void setLastPostedCodeFragments(List<? extends TextContent> textContents) {
        lastPostedCodeFragments = textContents;
    }

    @Override
    public void addChatMessage(ChatMessage message) {

    }

    @Override
    public ModelType getModelType() {
        return ModelType.TEXT_EMBEDDING_ADA_002;
    }

    @Override
    public List<ChatMessage> getChatMessages(ModelType model, ChatMessage userMessage) {
        return null;
    }
}
