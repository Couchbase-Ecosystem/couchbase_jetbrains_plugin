/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.ChatGptBundle;
import com.couchbase.intellij.tree.iq.chat.ChatLink;
import com.couchbase.intellij.tree.iq.settings.CustomAction;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.text.CodeFragment;
import com.intellij.CommonBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.OptionAction;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomPromptAction extends GenericEditorAction {

    public CustomPromptAction() {
        super(ChatGptBundle.message("action.ask"), ChatGptBundle.message("action.ask.desc"), AllIcons.Actions.Run_anything);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var fileExtension = "java";

        var editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            @SuppressWarnings("RedundantCast")
            var file = ((EditorEx) editor).getVirtualFile();
            if (file != null) {
                fileExtension = StringUtils.defaultIfEmpty(file.getExtension(), "");
            }

            var dialog = new CustomActionDialog(e.getProject(), CodeFragment.of(getSelectedTextFromEditor(editor)), fileExtension);
            dialog.show();
        }
    }

    protected String getSelectedTextFromEditor(Editor editor) {
        String selectedText = editor.getSelectionModel().getSelectedText();
        return StringUtils.defaultIfEmpty(selectedText, "");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }

    static class CustomActionDialog extends DialogWrapper {
        private JPanel panel;
        private final JBTextField question = new JBTextField();
        private final Project project;
        private EditorFactory editorFactory;
        private Editor editor;
        private final CodeFragment selected;
        private final String fileExtension;

        public CustomActionDialog(@Nullable Project project, CodeFragment selected, String fileExtension) {
            super(project);
            this.project = project;
            this.selected = selected;
            this.fileExtension = fileExtension;
            setTitle("New Custom Action");
            setResizable(true);
            init();
            setOKActionEnabled(true);
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            panel = new JPanel();
            panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
            panel.setBorder(JBUI.Borders.empty(10));
            panel.add(createItemPanel());
            SwingUtilities.invokeLater(question::requestFocusInWindow);
            return panel;
        }

        @Override
        public JComponent getPreferredFocusedComponent() {
            return panel;
        }

        @Override
        protected @NotNull DialogStyle getStyle() {
            return DialogStyle.COMPACT;
        }

        @Override
        protected JButton createJButtonForAction(Action action) {
            return super.createJButtonForAction(action);
        }

        @Override
        protected Action @NotNull [] createActions() {
            myOKAction = new SendAction();
            myOKAction.putValue(DialogWrapper.DEFAULT_ACTION, true);

            myCancelAction = new DialogWrapperAction(CommonBundle.getCancelButtonText()) {
                @Override
                protected void doAction(ActionEvent e) {
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            ArrayList<Action> actions = new ArrayList<>();
            actions.add(myOKAction);
            actions.add(myCancelAction);
            return actions.toArray(new Action[0]);
        }

        private JPanel createItemPanel() {
            JPanel basePanel = new JPanel(new BorderLayout());

            JPanel codePanel = new NonOpaquePanel(new BorderLayout());
            JBLabel codeLabel = new JBLabel("Code block:");
            codeLabel.setBorder(JBUI.Borders.empty(10,0,5,0));
            codePanel.add(codeLabel,BorderLayout.NORTH);
            editorFactory = EditorFactory.getInstance();
            FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(fileExtension);
            LightVirtualFile virtualFile = new LightVirtualFile(UUID.randomUUID() + "." + fileExtension, selected.content());
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document == null)
                document = editorFactory.createDocument(selected.content());
            editor = editorFactory.createEditor(document, project, virtualFile, false, EditorKind.PREVIEW);
            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    editor.getComponent().repaint();
                }
            });

            String originalGroupId = ((EditorEx)this.editor).getContextMenuGroupId();
            AnAction originalGroup = (originalGroupId == null) ? null : ActionManager.getInstance().getAction(originalGroupId);
            DefaultActionGroup group = new DefaultActionGroup();
            if (originalGroup instanceof ActionGroup)
                group.addAll(((ActionGroup)originalGroup).getChildren(null));

            EditorEx editorEx = (EditorEx) editor;
            editorEx.installPopupHandler(new ContextMenuPopupHandler.Simple(group));
            editorEx.setColorsScheme(EditorColorsManager.getInstance().getSchemeForCurrentUITheme());
            EditorSettings editorSettings = editor.getSettings();
            editorSettings.setVirtualSpace(false);
            editorSettings.setLineMarkerAreaShown(false);
            editorSettings.setIndentGuidesShown(true);
            editorSettings.setLineNumbersShown(true);
            editorSettings.setFoldingOutlineShown(true);
            editorSettings.setAdditionalColumnsCount(3);
            editorSettings.setAdditionalLinesCount(3);
            editorSettings.setCaretRowShown(true);
            editorSettings.setAnimatedScrolling(true);
            codePanel.setPreferredSize(new Dimension(600,400));
            codePanel.add(editor.getComponent(), BorderLayout.CENTER);
            basePanel.add(codePanel, BorderLayout.CENTER);

            JPanel prefixPanel = new NonOpaquePanel(new BorderLayout());
            JBLabel prefixLabel = new JBLabel("Command: ");
            prefixLabel.setBorder(JBUI.Borders.emptyBottom(5));
            prefixPanel.add(prefixLabel, BorderLayout.NORTH);
            question.getEmptyText().setText("Type your prompt here");
            prefixPanel.add(question, BorderLayout.CENTER);
            prefixPanel.setBorder(JBUI.Borders.empty(5,0));
            basePanel.add(prefixPanel, BorderLayout.SOUTH);

            return basePanel;
        }

        private class SendAction extends DialogWrapperAction implements OptionAction {

            public SendAction() {
                super("Send");
            }

            @Override
            protected void doAction(ActionEvent e) {
                ChatLink.forProject(project).pushMessage(question.getText(), List.of(CodeFragment.of(editor.getDocument().getText())));
                dispose();
                close(OK_EXIT_CODE);
            }

            @Override
            public Action @NotNull [] getOptions() {
                return new Action[] { new SendAndSaveAction() };
            }
        }

        private class SendAndSaveAction extends DialogWrapperAction {
            public SendAndSaveAction() {
                super("Send And Save");
            }

            @Override
            protected void doAction(ActionEvent e) {
                String defaultName = question.getText();
                String name = Messages.showInputDialog(project, "Enter a name for the custom action:", "Save Custom Action", null, defaultName, null);
                if (name != null && !name.isEmpty()) {
                    ChatLink.forProject(project).pushMessage(question.getText(), List.of(CodeFragment.of(editor.getDocument().getText())));
                    if (!StringUtils.isEmpty(question.getText())) {
                        List<CustomAction> customActionsPrefix = OpenAISettingsState.getInstance().getCustomActionsPrefix();
                        customActionsPrefix.add(new CustomAction(name, question.getText()));
                        ActionsUtil.refreshActions();
                    }
                    dispose();
                    close(OK_EXIT_CODE);
                }
            }
        }
    }
}
