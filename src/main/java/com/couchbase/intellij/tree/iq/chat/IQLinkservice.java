package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.intellij.openapi.project.Project;

import java.util.List;

public class IQLinkservice extends AbstractChatLink {
    private Project project;
    private IQCredentials credentials;
    private InputContext inputContext;
    private ConversationContext conversationContext;

    public IQLinkservice(Project project, IQCredentials credentials) {
        this.project = project;
        this.credentials = credentials;
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public InputContext getInputContext() {
        return null;
    }

    @Override
    public ConversationContext getConversationContext() {
        return null;
    }

    @Override
    public void pushMessage(String prompt, List<? extends TextContent> textContents) {

    }
}
