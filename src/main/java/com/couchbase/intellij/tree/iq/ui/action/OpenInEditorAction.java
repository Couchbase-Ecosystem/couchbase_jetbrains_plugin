package com.couchbase.intellij.tree.iq.ui.action;

import com.couchbase.intellij.tree.iq.text.TextContent;
import com.couchbase.intellij.tree.iq.ui.context.stack.TextInputContextEntry;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

public class OpenInEditorAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var content = e.getData(PlatformDataKeys.SELECTED_ITEM);
        if (content instanceof TextInputContextEntry codeContent && project != null) {
            codeContent.getTextContent().ifPresent(textContent -> {
                var file = new LightVirtualFile(codeContent.getText() + ".md", TextContent.toString(textContent));
                var openDesc = new OpenFileDescriptor(project, file);
                var editor = FileEditorManager.getInstance(project).openTextEditor(openDesc, true);
                if (editor instanceof EditorEx editorEx)
                    editorEx.getDocument().setReadOnly(true);
            });
        }
    }
}
