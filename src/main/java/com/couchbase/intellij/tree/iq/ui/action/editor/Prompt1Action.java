package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class Prompt1Action extends AbstractEditorAction {

    public Prompt1Action() {
        super(() -> OpenAISettingsState.getInstance().prompt1Name);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt1Value;
        super.actionPerformed(e);
    }

}
