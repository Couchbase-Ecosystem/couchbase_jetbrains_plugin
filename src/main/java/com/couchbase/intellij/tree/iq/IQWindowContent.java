/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.couchbase.intellij.tree.iq.ui.LoginPanel;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IQWindowContent extends JPanel implements LoginPanel.Listener, ChatPanel.LogoutListener {
    private final Project project;

    public IQWindowContent(@NotNull Project project) {
        this.project = project;

        IQCredentials credentials = new IQCredentials();
        if (credentials.getCredentials().isEmpty()) {
            onLogout(null);
        } else {
            onLogin(credentials);
        }

    }

    @Override
    public void onLogin(IQCredentials credentials) {
        this.removeAll();
        this.add(new ChatPanel(project, credentials, this));
    }

    @Override
    public void onLogout(@Nullable Throwable reason) {
        this.removeAll();
        this.add(new LoginPanel(this));
    }
}
