package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.IQStorage;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.tree.iq.ui.LoginPanel;
import com.couchbase.intellij.tree.iq.ui.action.editor.ActionsUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

public class IQWindowContent extends JPanel implements LoginPanel.Listener, ChatPanel.LogoutListener, ChatPanel.OrganizationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(IQWindowContent.class);
    private static final String IQ_URL = CapellaApiMethods.CAPELLA_DOMAIN + "/v2/organizations/%s/integrations/iq/";
    private final Project project;
    private IQCredentials credentials = new IQCredentials();
    private CapellaOrganizationList organizationList;
    private OpenAISettingsState.OpenAIConfig iqGptConfig;
    private String cachedPrompt;

    public IQWindowContent(@NotNull Project project) {
        setLayout(new GridBagLayout());
        this.project = project;

        if (!credentials.getCredentials().isEmpty() && credentials.checkAuthStatus()) {
            onLogin(credentials);
        } else {
            onLogout(null);
        }

    }

    @Override
    public void onLogin(IQCredentials credentials) {
        this.credentials = credentials;
        this.removeAll();
        try {
            this.organizationList = CapellaApiMethods.loadOrganizations(credentials.getAuth());
            if (organizationList.getData().isEmpty()) {
                Notifications.Bus.notify(
                        new Notification(
                                ChatGptBundle.message("group.id"),
                                "No Capella organizations found",
                                "At least one organization is required to use Couchbase IQ. No organizations found.",
                                NotificationType.ERROR
                        )
                );
                onLogout(null);
                return;
            }

            CapellaOrganization activeOrg = organizationList.getData().get(0).getData();
            String orgId = IQStorage.getInstance().getState().getActiveOrganization();
            if (orgId != null) {
                activeOrg = organizationList.getData().stream()
                        .filter(org -> orgId.equalsIgnoreCase(org.getData().getId()))
                        .map(CapellaOrganizationList.Entry::getData)
                        .findFirst()
                        .orElse(activeOrg);
            }
            this.onOrgSelected(activeOrg);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize IQ", e);
            Notifications.Bus.notify(
                    new Notification(
                            ChatGptBundle.message("group.id"),
                            "Something went wrong",
                            "Failed to login, please try again later",
                            NotificationType.ERROR
                    )
            );
            onLogout(e);
        }
    }

    @Override
    public boolean onLogout(@Nullable Throwable reason) {
        if (reason instanceof HttpException && ((HttpException) reason).code() == 401) {
            if (credentials.checkAuthStatus()) {
                iqGptConfig.setApiKey(credentials.getAuth().getJwt());
                return false;
            }
        }

        this.removeAll();
        this.add(new LoginPanel(credentials, this));
        this.updateUI();
        return true;
    }

    @Override
    public void onOrgSelected(CapellaOrganization organization) {
        this.removeAll();
        this.updateUI();
        SwingUtilities.invokeLater(() -> {
            IQStorage.getInstance().getState().setActiveOrganization(organization.getId());
            final String iqUrl = String.format(IQ_URL, organization.getId());
            iqGptConfig = new OpenAISettingsState.OpenAIConfig();
            OpenAISettingsState.getInstance().setGpt4Config(iqGptConfig);
            OpenAISettingsState.getInstance().setEnableInitialMessage(false);
            iqGptConfig.setApiKey(credentials.getAuth().getJwt());
            iqGptConfig.setEnableStreamResponse(false);
            iqGptConfig.setModelName("gpt-4");
            iqGptConfig.setApiEndpointUrl(iqUrl);
            iqGptConfig.setEnableCustomApiEndpointUrl(true);
            iqGptConfig.withSystemPrompt(this::systemPrompt);
            ChatPanel chatPanel = new ChatPanel(project, iqGptConfig.withSystemPrompt(this::systemPrompt), organizationList, organization, this, this);
            ActionsUtil.refreshActions();
            this.add(chatPanel);
            this.updateUI();
        });
    }

    public String systemPrompt() {
        try {
            if (cachedPrompt == null) {
                InputStream is = IQWindowContent.class.getResourceAsStream("/iq/intent_prompt.txt");
                cachedPrompt = IOUtils.toString(is);
                String collections = ActiveCluster.getInstance().getChildren().stream()
                        .flatMap(b -> b.getChildren().stream())
                        .flatMap(s -> s.getChildren().stream())
                        .map(c -> c.getName())
                        .distinct()
                        .collect(Collectors.joining(", "));
                cachedPrompt = cachedPrompt.replaceAll("\\$\\{collections\\}", collections);
            }
            return cachedPrompt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
