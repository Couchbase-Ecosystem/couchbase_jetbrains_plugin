package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.icons.ChatGPTIcons;
import com.couchbase.intellij.tree.iq.message.ChatGPTBundle;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CustomAction extends AbstractEditorAction {

    public static final Key ACTIVE_PREFIX = Key.create("ActivePrefix");
    public static final Key ACTIVE_PROMPT = Key.create("ActivePrompt");
    public static final Key ACTIVE_FILE_TYPE = Key.create("ActiveFileType");

    public CustomAction() {
        super(() -> ChatGPTBundle.message("action.code.custom.action"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Runnable runnable = () -> {
            doActionPerformed(e);
        };

        ListPopup actionGroupPopup = JBPopupFactory.getInstance().createActionGroupPopup("Custom Prompt Popups",
                new CustomPrefixActionGroup(runnable), e.getDataContext(), true, null, Integer.MAX_VALUE);
        actionGroupPopup.showInBestPositionFor(e.getData(CommonDataKeys.EDITOR));
    }

    static class CustomPrefixActionGroup extends ActionGroup {
        private final Runnable runnable;
        public CustomPrefixActionGroup(Runnable runnable) {
            initialization();
            this.runnable = runnable;
        }

        private List<AnAction> initialization(){
            List<AnAction> anActionList = new ArrayList<>();
            anActionList.add(new AddCustomAction(runnable));
            anActionList.add(new Separator());
            return anActionList;
        }

        @Override
        public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            List<AnAction> anActionList = initialization();
            for (Map.Entry<String, String> entry : OpenAISettingsState.getInstance().customPrompts.entrySet()) {
                String prefix = entry.getKey();
                String prompt = entry.getValue();
                anActionList.add(new CustomActionItem(prefix, prompt, this.runnable));
            }
            return anActionList.toArray(AnAction[]::new);
        }
    }

    static class CustomActionItem extends AnAction {

        private final Runnable runnable;
        private final String prompt;
        public CustomActionItem(String prefix, String prompt, Runnable runnable) {
            super(() -> prefix, ChatGPTIcons.TOOL_WINDOW);
            this.runnable = runnable;
            this.prompt = prompt;
        }
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            e.getProject().putUserData(ACTIVE_PREFIX, prompt);
            runnable.run();
        }
    }
}
