package com.couchbase.intellij.tree.iq.core;

import com.couchbase.intellij.tree.CouchbaseWindowFactory;
import com.couchbase.intellij.tree.iq.ChatGPTHandler;
import com.couchbase.intellij.tree.iq.message.ChatGPTBundle;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.CouchbaseIQPanel;
import com.couchbase.intellij.tree.iq.ui.MessageComponent;
import com.couchbase.intellij.tree.iq.ui.MessageGroupComponent;
import com.couchbase.intellij.tree.iq.util.StringUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;



public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(CouchbaseWindowFactory.ACTIVE_CONTENT);
        doActionPerformed((CouchbaseIQPanel) mainPanel, data);
    }

    private boolean presetCheck(boolean isChatGPTModel) {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (isChatGPTModel) {
            if (StringUtil.isEmpty(instance.accessToken)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure the access token first.",
                                NotificationType.ERROR));
                return false;
            }
            if (instance.enableCustomizeChatGPTUrl && StringUtil.isEmpty(instance.customizeUrl)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure ChatGPT customize server first.",
                                NotificationType.ERROR));
                return false;
            }
        } else {
            if (StringUtil.isEmpty(instance.apiKey)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure a API Key first.",
                                NotificationType.ERROR));
                return false;
            }
            if (instance.enableCustomizeGpt35TurboUrl && StringUtil.isEmpty(instance.gpt35TurboUrl)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure GPT-3.5-Turbo customize server first.",
                                NotificationType.ERROR));
                return false;
            }
        }
        return true;
    }

    public void doActionPerformed(CouchbaseIQPanel couchbaseIQPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
            return;
        }

//        // Check the configuration first
//        if (!presetCheck(couchbaseIQPanel.isChatGPTModel())) {
//            return;
//        }

        // Reset the question container
        couchbaseIQPanel.getSearchTextArea().getTextArea().setText("");
        couchbaseIQPanel.aroundRequest(true);
        Project project = couchbaseIQPanel.getProject();
        MessageGroupComponent contentPanel = couchbaseIQPanel.getContentPanel();

        // Add the message component to container
        MessageComponent question = new MessageComponent(data,true);
        MessageComponent answer = new MessageComponent("Waiting for response...",false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = couchbaseIQPanel.getExecutorService();
            // Request the server.

            ChatGPTHandler chatGPTHandler = project.getService(ChatGPTHandler.class);
            executorService.submit(() -> {
                EventSource handle = chatGPTHandler.handle(couchbaseIQPanel, answer, data);
                couchbaseIQPanel.setRequestHolder(handle);
                contentPanel.updateLayout();
                contentPanel.scrollToBottom();
            });

        } catch (Exception e) {
            answer.setSourceContent(e.getMessage());
            answer.setContent(e.getMessage());
            couchbaseIQPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }
}
