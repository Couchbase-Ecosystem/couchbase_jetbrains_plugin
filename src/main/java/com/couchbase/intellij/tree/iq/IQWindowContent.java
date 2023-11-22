/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.LoginPanel;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.tree.iq.ui.OrgSelectionPanel;
import com.intellij.designer.LightFillLayout;
import com.intellij.openapi.project.Project;
import com.intellij.ui.dsl.gridLayout.GridLayout;
import kotlinx.html.I;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.HttpException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class IQWindowContent extends JPanel implements LoginPanel.Listener, ChatPanel.LogoutListener, OrgSelectionPanel.Listener {
    private static final String IQ_URL = "https://api.dev.nonprod-project-avengers.com/v2/organizations/%s/integrations/iq/";
    private final Project project;
    private IQCredentials credentials = new IQCredentials();
    private CapellaOrganizationList organizationList;
    private OpenAISettingsState.OpenAIConfig iqGptConfig;

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
            this.add(new OrgSelectionPanel(project, organizationList, this));
            this.updateUI();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        credentials.clear();
        this.add(new LoginPanel(this));
        this.updateUI();
        return true;
    }

    @Override
    public void onOrgSelected(CapellaOrganization organization) {
        this.removeAll();
        this.updateUI();
        SwingUtilities.invokeLater(() -> {
            final String iqUrl = String.format(IQ_URL, organization.getId());
            iqGptConfig = new OpenAISettingsState.OpenAIConfig();
            OpenAISettingsState.getInstance().setGpt4Config(iqGptConfig);
            OpenAISettingsState.getInstance().setEnableInitialMessage(false);
            iqGptConfig.setApiKey(credentials.getAuth().getJwt());
            iqGptConfig.setEnableStreamResponse(false);
            iqGptConfig.setModelName("gpt-4");
            iqGptConfig.setApiEndpointUrl(iqUrl);
            iqGptConfig.setEnableCustomApiEndpointUrl(true);
            ChatPanel chatPanel = new ChatPanel(project, iqGptConfig, organizationList, organization, this, this);
            this.add(chatPanel);
            this.updateUI();
        });
    }
}
