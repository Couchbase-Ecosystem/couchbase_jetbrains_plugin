/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public class ToolWindowLocator {
    public static final String TOOL_WINDOW_ID = "com.didalgo.ChatGPT";

    public static ToolWindow locate(Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    }

    public static void ensureActivated(Project project) {
        var chatGPT = locate(project);
        if (chatGPT == null) {
            throw new AssertionError("Unable to find " + TOOL_WINDOW_ID + " Tool Window");
        }
        if (!chatGPT.isActive()) {
            chatGPT.activate(null);
        }
    }
}
