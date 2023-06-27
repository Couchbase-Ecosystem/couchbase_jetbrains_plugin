package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.tree.node.FileNodeDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class OpenDocumentDialog extends DialogWrapper {
    private static final int MINIMUM_WIDTH = 300;
    private JPanel panel;
    private JTextField textField;

    private Project project;
    private String bucket;
    private String scope;
    private String collection;

    private JLabel errorLabel;

    private Tree tree;

    protected OpenDocumentDialog(Project project, Tree tree, String bucket, String scope, String collection) {
        super(true);
        this.project = project;
        this.tree = tree;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        init();
        setTitle("Inform the Document Id");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(400, 100));
    }

    public static boolean isValidDocumentId(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return false;
        }
        int maxDocumentIdLength = 250;
        if (documentId.length() > maxDocumentIdLength) {
            return false;
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!documentId.matches(regex)) {
            return false;
        }

        return true;
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
        errorLabel.setBorder(JBUI.Borders.empty(10, 0));
        errorLabel.setForeground(Color.decode("#FF4444"));
        JLabel label = new JLabel("Document Id:");
        textField = new JTextField();

        panel = new JBPanel<>(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(errorLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {

        if (!isValidDocumentId(textField.getText())) {
            errorLabel.setText("The id specified contains invalid characters.");
            panel.revalidate();
            return;
        }

        String fileName = textField.getText() + ".json";
        FileNodeDescriptor descriptor = new FileNodeDescriptor(fileName, bucket, scope, collection, textField.getText(), null);
        DataLoader.loadDocument(project, descriptor, tree, true);

        VirtualFile virtualFile = descriptor.getVirtualFile();
        if (virtualFile != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(virtualFile, true);
        } else {
            System.err.println("virtual file is null");
        }

        super.doOKAction();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(Math.max(preferredSize.width, MINIMUM_WIDTH), preferredSize.height);
    }

}
