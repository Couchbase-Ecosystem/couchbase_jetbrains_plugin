/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq;

import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsPanel;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.BrowserContent;
import com.couchbase.intellij.tree.iq.ui.MainPanel;
import com.couchbase.intellij.tree.iq.ui.action.tool.SettingsAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatGptToolWindowFactory implements ToolWindowFactory, DumbAware {
    public static final Key<ModelPage> ACTIVE_TAB = Key.create("didalgo.ChatGptToolWindow.ACTIVE_TAB");

    public static final String GPT35_TURBO_CONTENT_NAME = "GPT-3.5-Turbo";
    public static final String GPT4_CONTENT_NAME = "GPT-4";
    public static final String ONLINE_CHATGPT_CONTENT_NAME = "Online ChatGPT";
    private static final String ACTIVE_CONTENT_KEY = "didalgo.chatgpt.ToolWindow.ACTIVE";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        OpenAISettingsState settings = OpenAISettingsState.getInstance();

        MainPanel gpt35TurboToolWindow = new MainPanel(project, settings.getGpt35Config());
        Content gpt35Turbo = contentFactory.createContent(gpt35TurboToolWindow.init(), GPT35_TURBO_CONTENT_NAME, false);
        gpt35Turbo.putUserData(ACTIVE_TAB, ModelPage.GPT_3_5);
        gpt35Turbo.setCloseable(false);

        MainPanel gpt4ToolWindow = new MainPanel(project, settings.getGpt4Config());
        Content gpt4 = contentFactory.createContent(gpt4ToolWindow.init(), GPT4_CONTENT_NAME, false);
        gpt4.putUserData(ACTIVE_TAB, ModelPage.GPT_4);
        gpt4.setCloseable(false);

        BrowserContent browserToolWindow = new BrowserContent(project);
        Content browser = contentFactory.createContent(browserToolWindow.getContentPanel(), ONLINE_CHATGPT_CONTENT_NAME, false);
        browser.putUserData(ACTIVE_TAB, ModelPage.ONLINE);
        browser.setCloseable(false);

        toolWindow.getContentManager().addContent(gpt35Turbo);
        toolWindow.getContentManager().addContent(gpt4);
        toolWindow.getContentManager().addContent(browser);

        // Set the default component. It require the 1st container
        ModelPage firstContent = ModelPage.of(PropertiesComponent.getInstance().getValue(ACTIVE_CONTENT_KEY, ModelPage.GPT_3_5.name()));
        switch (firstContent.name()) {
            case ModelPage.Of.GPT_3_5 -> project.putUserData(ChatLink.KEY, gpt35TurboToolWindow.getChatLink());
            case ModelPage.Of.GPT_4   -> project.putUserData(ChatLink.KEY, gpt4ToolWindow.getChatLink());
            case ModelPage.Of.ONLINE  -> project.putUserData(ChatLink.KEY, browserToolWindow.getChatLink());
        }

        // Add the selection listener
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                ModelPage page = event.getContent().getUserData(ACTIVE_TAB);
                if (page != null) {
                    switch (page.name()) {
                        case ModelPage.Of.GPT_3_5 -> project.putUserData(ChatLink.KEY, gpt35TurboToolWindow.getChatLink());
                        case ModelPage.Of.GPT_4   -> project.putUserData(ChatLink.KEY, gpt4ToolWindow.getChatLink());
                        case ModelPage.Of.ONLINE  -> project.putUserData(ChatLink.KEY, browserToolWindow.getChatLink());
                    }
                }
                PropertiesComponent.getInstance(project).setValue(ACTIVE_CONTENT_KEY, (page == null)? null: page.name());
            }
        });

        List<AnAction> actionList = new ArrayList<>();
        actionList.add(new SettingsAction(ChatGptBundle.message("action.settings")) {
            @Override
            public Class<? extends Configurable> getPanelToSelect() {
                Content content;
                var contentManager = toolWindow.getContentManagerIfCreated();
                if (contentManager != null && (content = contentManager.getSelectedContent()) != null) {
                    ModelPage page = content.getUserData(ACTIVE_TAB);
                    if (page != null)
                        return OpenAISettingsPanel.getTargetPanelClassForPage(page.name());
                }
                return super.getPanelToSelect();
            }
        });
        toolWindow.setTitleActions(actionList);
    }
}
