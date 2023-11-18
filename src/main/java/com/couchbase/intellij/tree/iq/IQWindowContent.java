/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.ui.LoginPanel;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.couchbase.intellij.tree.iq.ui.OrgSelectionPanel;
import com.intellij.openapi.project.Project;
import kotlinx.html.I;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class IQWindowContent extends JPanel implements LoginPanel.Listener, ChatPanel.LogoutListener, OrgSelectionPanel.Listener {
    private final Project project;
    private IQCredentials credentials = new IQCredentials();

    public IQWindowContent(@NotNull Project project) {
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
            this.add(new OrgSelectionPanel(project, credentials.getAuth(), this));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLogout(@Nullable Throwable reason) {
        this.removeAll();
        this.add(new LoginPanel(this));
    }

    @Override
    public void onOrgSelected(CapellaOrganization organization) {
        this.removeAll();
        this.add(new ChatPanel(project, credentials, organization, this));
    }
}
