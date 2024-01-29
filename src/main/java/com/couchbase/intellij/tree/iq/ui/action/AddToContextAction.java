package com.couchbase.intellij.tree.iq.ui.action;

import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.text.CodeFragmentFactory;
import com.couchbase.intellij.tree.iq.text.TextFragment;
import com.couchbase.intellij.tree.iq.text.TextFragmentUtils;
import com.couchbase.intellij.tree.iq.ui.context.stack.TextInputContextEntry;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.util.IconUtil;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class AddToContextAction extends AnAction {

    private static final DataKey<JBPopup> DOCUMENTATION_POPUP_KEY = DataKey.create("documentation.popup");

    public AddToContextAction() {
        super("ChatGPT: Add to Context", "Adds selected code as part of next ChatGPT prompt", null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Enable the action only when an editor or other usable content is available
        DataContext dataContext = event.getDataContext();
        event.getPresentation().setEnabled(
                dataContext.getData(CommonDataKeys.EDITOR) != null || dataContext.getData(DOCUMENTATION_POPUP_KEY) != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        JBPopup popup = e.getData(DOCUMENTATION_POPUP_KEY);
        if (popup != null)
            handlePopupData(project, popup);

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null && editor.getVirtualFile() != null)
            handleEditorData(project, editor);
    }

    private void handleEditorData(Project project, Editor editor) {
        var icon = IconUtil.getIcon(editor.getVirtualFile(), 0, project);
        var text = editor.getVirtualFile().getPresentableName();
        TextInputContextEntry entry = new TextInputContextEntry(icon, text, CodeFragmentFactory.create(editor));

        addToContext(project, entry);
    }

    private void handlePopupData(Project project, JBPopup popup) {
        TextFragment textFragment = TextFragmentUtils.scrapContent(popup.getContent());

        var icon = AllIcons.Actions.ShowAsTree;
        var text = "[" + LocalDateTime.now().withNano(0) + "]";
        TextInputContextEntry entry = new TextInputContextEntry(icon, text, textFragment);

        addToContext(project, entry);
    }

    protected void addToContext(Project project, TextInputContextEntry entry) {
        ChatLink.forProject(project).getInputContext().addEntry(entry);
    }
}
