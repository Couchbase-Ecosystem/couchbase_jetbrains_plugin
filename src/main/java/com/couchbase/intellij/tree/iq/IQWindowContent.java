package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.persistence.storage.IQStorage;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.tree.iq.ui.LoginPanel;
import com.couchbase.intellij.tree.iq.ui.action.editor.ActionsUtil;
import com.couchbase.intellij.workbench.Log;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IQWindowContent extends JPanel implements LoginPanel.Listener, ChatPanel.LogoutListener, ChatPanel.OrganizationListener {
    private static final Supplier<String> IQ_URL = () -> CapellaApiMethods.getCapellaDomain() + "/v2/organizations/%s/integrations/iq/";
    private final Project project;
    private IQCredentials credentials = new IQCredentials();
    private CapellaOrganizationList organizationList;
    private OpenAISettingsState.OpenAIConfig iqGptConfig;
    private static String cachedPrompt;
    private static AtomicReference<IQWindowContent> instance = new AtomicReference<>();
    private ChatPanel chatPanel;

    public IQWindowContent(@NotNull Project project) {
        setLayout(new GridBagLayout());
        this.project = project;
        instance.set(this);
//        // SQLPP highlighter in response code blocks marked as ```sqlpp
////        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
////        atmf.putMapping("text/sqlpp", SqlppTokenMaker.class.getCanonicalName());
//
//        if (!credentials.getCredentials().isEmpty() && credentials.checkAuthStatus()) {
//            onLogin(credentials);
//        } else {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Loading iQ Credentials", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                onLogout(null);
            }
        });

    }

    public static Optional<IQWindowContent> getInstance() {
        return Optional.ofNullable(instance.get());
    }

    public static void setClusterContext(String bucket, String scope) {
        ActiveCluster.getInstance().setQueryContext(new QueryContext(bucket, scope));
        cachedPrompt = null;
    }

    public static void clearClusterContext() {
        ActiveCluster.getInstance().setQueryContext(null);
        cachedPrompt = null;
    }

    public QueryContext getClusterContext() {
        return ActiveCluster.getInstance().getQueryContext().getValue();
    }

    @Override
    public void onLogin(IQCredentials credentials) {
        this.credentials = credentials;
        this.removeAll();
        try {
            this.organizationList = CapellaApiMethods.loadOrganizations(credentials.getAuth());
            if (organizationList.getData().isEmpty()) {
                Notifications.Bus.notify(new Notification(ChatGptBundle.message("group.id"), "No Capella organizations found", "At least one organization is required to use Couchbase IQ. No organizations found.", NotificationType.ERROR));
                onLogout(null);
                return;
            }

            if (this.organizationList != null) {
                this.organizationList = this.organizationList.getOnlyIqEnabledOrgs();
            }

            CapellaOrganization activeOrg = organizationList.getData().stream().map(org -> org.getData()).filter(data -> credentials.checkIqIsEnabled(data.getId())).filter(data -> credentials.checkTermsAccepted(data.getId())).findFirst().orElse(null);
            String orgId = IQStorage.getInstance().getState().getActiveOrganization();
            if (orgId != null) {
                activeOrg = organizationList.getData().stream().filter(org -> orgId.equalsIgnoreCase(org.getData().getId())).map(CapellaOrganizationList.Entry::getData).findFirst().orElse(activeOrg);
            }

            if (activeOrg == null) {
                Notifications.Bus.notify(new Notification(ChatGptBundle.message("group.id"), "No Capella organizations with iQ enabled found", "At least one organization with enabled iQ feature and accepted terms and conditions is required to use Couchbase IQ. No organizations found.", NotificationType.ERROR));
                onLogout(null);
            } else {
                this.onOrgSelected(activeOrg);
            }
        } catch (Exception e) {
            Log.error("Failed to initialize IQ", e);
            Notifications.Bus.notify(new Notification(ChatGptBundle.message("group.id"), "Something went wrong while trying to login into Capella", "Failed to login, please try again later", NotificationType.ERROR));
            onLogout(e);
        }
    }

    @Override
    public boolean onLogout(@Nullable Throwable reason) {
        this.removeAll();
        this.add(new LoginPanel(credentials, this));
        this.updateUI();
        return true;
    }

    @Override
    public void onOrgSelected(CapellaOrganization organization) {
        if (!credentials.checkIqIsEnabled(organization.getId())) {
            Notifications.Bus.notify(new Notification(ChatGptBundle.message("group.id"), "Unable to use this organization", "Capella iQ is not enabled for this organization.", NotificationType.ERROR));
            onLogout(null);
            return;
        }

        if (!credentials.checkTermsAccepted(organization.getId())) {
            Notifications.Bus.notify(new Notification(ChatGptBundle.message("group.id"), "Unable to use this organization", "Capella iQ terms of use have not been accepted for this organization. Please accept terms of use in Capella", NotificationType.ERROR));
            onLogout(null);
            return;
        }
        this.removeAll();
        this.updateUI();
        SwingUtilities.invokeLater(() -> {
            IQStorage.getInstance().getState().setActiveOrganization(organization.getId());
            final String iqUrl = String.format(IQ_URL.get(), organization.getId());
            iqGptConfig = new OpenAISettingsState.OpenAIConfig();
            OpenAISettingsState.getInstance().setGpt4Config(iqGptConfig);
            OpenAISettingsState.getInstance().setEnableInitialMessage(true);
            iqGptConfig.setApiKey(credentials.getAuth().getJwt());
            iqGptConfig.setEnableStreamResponse(false);
            iqGptConfig.setModelName("gpt-4");
            iqGptConfig.setApiEndpointUrl(iqUrl);
            iqGptConfig.setEnableCustomApiEndpointUrl(true);
            chatPanel = new ChatPanel(project, iqGptConfig.withSystemPrompt(IQWindowContent::systemPrompt), organizationList, organization, this, this);
            ActionsUtil.refreshActions();
            this.add(chatPanel);
            this.updateUI();
        });
    }

    public static String systemPrompt() {
        try {
            if (cachedPrompt == null) {
                InputStream is = IQWindowContent.class.getResourceAsStream("/iq/intent_prompt.txt");
                cachedPrompt = IOUtils.toString(is, StandardCharsets.UTF_8);
                String collections = ActiveCluster.getInstance().getChildren().stream().flatMap(b -> b.getChildren().stream()).flatMap(s -> s.getChildren().stream()).map(c -> c.getName()).distinct().collect(Collectors.joining(", "));
                cachedPrompt = cachedPrompt.replaceAll("\\$\\{collections\\}", collections);
                if (ActiveCluster.getInstance() != null) {
                    QueryContext context = ActiveCluster.getInstance().getQueryContext().getValue();
                    if (context != null) {
                        cachedPrompt = String.format("%s\n Use this couchbase cluster context to address all collections: `%s`.`%s`", cachedPrompt, context.getBucket(), context.getScope());
                    }
                }
            }
            return cachedPrompt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

}
