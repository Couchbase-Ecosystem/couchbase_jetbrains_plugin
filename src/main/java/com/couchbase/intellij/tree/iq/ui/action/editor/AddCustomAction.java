package com.couchbase.intellij.tree.iq.ui.action.editor;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.util.StringUtil;
import com.intellij.CommonBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Wuzi
 */
public class AddCustomAction extends AnAction {

    private final Runnable runnable;
    public AddCustomAction(Runnable runnable) {
        super(() -> "Add Custom Prompt", AllIcons.Actions.AddList);
        this.runnable = runnable;
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Set the file type
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getProject().putUserData(CustomAction.ACTIVE_FILE_TYPE, virtualFile.getFileType());

        // Set the file prompt
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        e.getProject().putUserData(CustomAction.ACTIVE_PROMPT, selectedText);

        new CustomActionDialog(e.getProject(),runnable).show();
    }

    static class CustomActionDialog extends DialogWrapper {
        private final Runnable runnable;
        private final JBTextField nameField = new JBTextField();
        private final ExpandableTextField valueField = new ExpandableTextField();
        private final Project project;
        private JPanel panel;
        private Editor editor;

        public CustomActionDialog(@Nullable Project project, Runnable runnable) {
            super(project);
            this.project = project;
            setTitle("New Custom Prompt");
            setResizable(false);
            init();
            setOKActionEnabled(true);
            this.runnable = runnable;
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            panel = new JPanel();
            panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
            panel.setBorder(JBUI.Borders.empty(10));
            panel.add(createItemPanel());
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
        protected Action @NotNull [] createActions() {
            myOKAction = new DialogWrapperAction("Send") {
                @Override
                protected void doAction(ActionEvent e) {
                    project.putUserData(CustomAction.ACTIVE_PREFIX, valueField.getText());
                    project.putUserData(CustomAction.ACTIVE_PROMPT, editor.getDocument().getText());
                    runnable.run();
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            DialogWrapperAction mySendAndSaveAction = new DialogWrapperAction("Send And Save") {
                @Override
                protected void doAction(ActionEvent e) {
                    project.putUserData(CustomAction.ACTIVE_PREFIX, valueField.getText());
                    project.putUserData(CustomAction.ACTIVE_PROMPT, editor.getDocument().getText());
                    runnable.run();
                    if (!StringUtil.isEmpty(valueField.getText())) {
                        Map<String, String> customPrompts = OpenAISettingsState.getInstance().customPrompts;
                        customPrompts.put(StringUtil.isEmpty(nameField.getText()) ? valueField.getText() :
                                nameField.getText(), valueField.getText());
                    }
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            myCancelAction = new DialogWrapperAction(CommonBundle.getCancelButtonText()) {
                @Override
                protected void doAction(ActionEvent e) {
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(myOKAction);
            actions.add(mySendAndSaveAction);
            actions.add(myCancelAction);
            return actions.toArray(new Action[0]);
        }

        private JPanel createItemPanel() {
            JPanel basePanel = new JPanel(new BorderLayout());
            JPanel promptPanel = new NonOpaquePanel(new GridLayout(2,1));

            JPanel namePanel = new JPanel(new BorderLayout());
            JBLabel promptNameLabel = new JBLabel("Prompt name: ");
            promptNameLabel.setIcon(AllIcons.General.ContextHelp);
            promptNameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            promptNameLabel.setToolTipText("<html>The name displayed in the menu, and it should be as short as possible.");
            promptNameLabel.setBorder(JBUI.Borders.emptyBottom(5));
            namePanel.add(promptNameLabel, BorderLayout.NORTH);
            nameField.getEmptyText().setText("Type prompt name here, if empty, the prompt value will be used");
            namePanel.add(nameField, BorderLayout.CENTER);

            JPanel valuePanel = new JPanel(new BorderLayout());
            JBLabel promptValueLabel = new JBLabel("Prompt value: ");
            promptValueLabel.setIcon(AllIcons.General.ContextHelp);
            promptValueLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            promptValueLabel.setToolTipText("<html>When asking a question, the prompt content sent to AI.");
            promptValueLabel.setBorder(JBUI.Borders.emptyBottom(5));
            valuePanel.add(promptValueLabel,BorderLayout.NORTH);
            valueField.getEmptyText().setText("Type new custom prompt here");
            valueField.setFont(nameField.getFont());
            valuePanel.add(valueField,BorderLayout.CENTER);

            promptPanel.add(namePanel);
            promptPanel.add(valuePanel);

            basePanel.add(promptPanel,BorderLayout.NORTH);

            JPanel codePanel = new NonOpaquePanel(new BorderLayout());
            JBLabel codeLabel = new JBLabel("Code block:");
            codeLabel.setBorder(JBUI.Borders.empty(10,0,5,0));
            codePanel.add(codeLabel,BorderLayout.NORTH);
            EditorFactory editorFactory = EditorFactory.getInstance();
            editor = editorFactory.createEditor(new DocumentImpl((String) project.
                    getUserData(CustomAction.ACTIVE_PROMPT)),project,
                    (FileType) project.getUserData(CustomAction.ACTIVE_FILE_TYPE),false);
            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    editor.getComponent().repaint();
                }
            });
            EditorSettings editorSettings = editor.getSettings();
            editorSettings.setVirtualSpace(false);
            editorSettings.setLineMarkerAreaShown(false);
            editorSettings.setIndentGuidesShown(true);
            editorSettings.setLineNumbersShown(true);
            editorSettings.setFoldingOutlineShown(false);
            editorSettings.setAdditionalColumnsCount(3);
            editorSettings.setAdditionalLinesCount(3);
            editorSettings.setCaretRowShown(false);
            editorSettings.setAnimatedScrolling(true);
            codePanel.setPreferredSize(new Dimension(600,400));
            codePanel.add(editor.getComponent(),BorderLayout.CENTER);
            basePanel.add(codePanel,BorderLayout.CENTER);
            return basePanel;
        }
    }
}
