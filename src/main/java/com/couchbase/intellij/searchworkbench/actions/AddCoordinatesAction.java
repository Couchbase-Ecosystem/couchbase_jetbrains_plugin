package com.couchbase.intellij.searchworkbench.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class AddCoordinatesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        if (file == null || !file.getName().endsWith(".cbs.json")) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();

        String documentText = editor.getDocument().getText();
        JsonCoordinateIdentifier identifier = new JsonCoordinateIdentifier();
        JsonFilterBlock block = identifier.getCoordinateObject(documentText, offset);

        CoordinatesDialog dialog = new CoordinatesDialog(editor, block);
        dialog.show();

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean isEnabled = false;
        if (editor != null) {
            VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
            if (file != null && file.getName().endsWith(".cbs.json")) {
                isEnabled = true;
            }
        }
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }
}