package com.couchbase.intellij.tree.docfilter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.intellij.sdk.language.SQLPPFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DocumentFilterDialog extends DialogWrapper {
    private EditorEx editor;

    public DocumentFilterDialog(String collectionName) {
        super(false);
        setTitle("Document Filter for Collection '"+collectionName+"'");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        editor = createEditor();
        JBScrollPane scrollPane = new JBScrollPane(editor.getComponent());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel statusBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBarPanel.add(new JLabel("Status bar"));
        panel.add(statusBarPanel, BorderLayout.SOUTH);

        return panel;
    }

    private EditorEx createEditor() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        EditorEx editor = (EditorEx) editorFactory.createEditor(document, null, SQLPPFileType.INSTANCE, false);
        editor.setCaretEnabled(true);
        editor.setBorder(BorderFactory.createEmptyBorder());
        return editor;
    }
}