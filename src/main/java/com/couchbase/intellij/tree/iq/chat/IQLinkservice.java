package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.couchbase.intellij.tree.iq.ui.context.stack.DefaultInputContext;
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
        this.inputContext = new DefaultInputContext();
        this.conversationContext = new IQLinkState();
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public InputContext getInputContext() {
        return inputContext;
    }

    @Override
    public ConversationContext getConversationContext() {
        return conversationContext;
    }

    @Override
    public void pushMessage(String prompt, List<? extends TextContent> textContents) {

    }
}
