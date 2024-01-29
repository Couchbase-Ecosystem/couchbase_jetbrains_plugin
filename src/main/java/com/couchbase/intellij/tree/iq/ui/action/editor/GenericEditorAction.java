package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.text.CodeFragmentFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.function.Supplier;

public class GenericEditorAction extends AbstractEditorAction {

    private final String prompt;

    public GenericEditorAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText, String prompt) {
        super(dynamicText, () -> prompt);
        this.prompt = prompt;
    }

    public GenericEditorAction(String text, String prompt, Icon icon) {
        super(text, prompt, icon);
        this.prompt = prompt;
    }

    @Override
    protected void actionPerformed(Project project, Editor editor, String selectedText) {
        ChatLink.forProject(project).pushMessage(prompt, List.of(CodeFragmentFactory.create(editor, selectedText)));
    }
}
