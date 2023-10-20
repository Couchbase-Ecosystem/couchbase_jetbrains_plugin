/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.core;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.action.editor.ActionsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class StartUpActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        OpenAISettingsState.getInstance();
        ActionsUtil.refreshActions();
    }
}
