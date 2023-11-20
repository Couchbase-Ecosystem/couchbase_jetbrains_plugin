package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.MutableDocument;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;


public class CBLOpenDocumentDialog extends DialogWrapper {
    private static final int MINIMUM_WIDTH = 300;
    private JPanel panel;
    private JBTextField textField;

    private final Project project;
    private final String scope;
    private final String collection;

    private JLabel errorLabel;

    private final boolean createDocument;

    private final Tree tree;

    private final DefaultMutableTreeNode clickedNode;

    protected CBLOpenDocumentDialog(boolean createDocument, Project project, Tree tree, DefaultMutableTreeNode clickedNode, String scope, String collection) {
        super(true);
        this.createDocument = createDocument;
        this.project = project;
        this.tree = tree;
        this.scope = scope;
        this.collection = collection;
        this.clickedNode = clickedNode;
        init();
        if (createDocument) {
            setTitle("New Document");
        } else {
            setTitle("Open Document");
        }

        setResizable(false);
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
        JLabel label = new JLabel(createDocument ? "New Document Id:" : "Document Id:");
        textField = new JBTextField(30);


        panel = new JBPanel<>(new BorderLayout());

        JPanel formPanel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = JBUI.insets(5);

        gc.gridy = 0;
        gc.gridx = 0;
        gc.weightx = 0.4;
        formPanel.add(label, gc);

        gc.gridy = 0;
        gc.gridx = 1;
        gc.weightx = 0.6;
        formPanel.add(textField, gc);


        panel.add(formPanel, BorderLayout.NORTH);
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

        com.couchbase.lite.Document doc = null;
        try {
            doc = ActiveCBLDatabase.getInstance().getDatabase()
                    .getScope(scope)
                    .getCollection(collection)
                    .getDocument(textField.getText());

        } catch (CouchbaseLiteException e) {
            errorLabel.setText("An error occurred while trying to load the document");
            Log.error("Failed to load document with id " + textField.getText(), e);
            return;
        }

        if (!createDocument) {
            if (doc == null) {
                errorLabel.setText("There is no document with the specified id.");
                panel.revalidate();
                return;
            }

        } else if (createDocument && doc == null) {

            try {
                MutableDocument newDoc = new MutableDocument(textField.getText());
                newDoc.setJSON("{ }");
                ActiveCBLDatabase.getInstance().getDatabase().getScope(scope).getCollection(collection).save(newDoc);

                TreePath treePath = new TreePath(clickedNode.getPath());
                tree.collapsePath(treePath);
                tree.expandPath(treePath);

            } catch (CouchbaseLiteException e) {
                Log.error("Failed to load document with id " + textField.getText(), e);
                ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred while creating the document.", "Couchbase Plugin Error"));
                return;
            }
        }

        String fileName = textField.getText() + ".json";
        CBLFileNodeDescriptor descriptor = new CBLFileNodeDescriptor(fileName, scope, collection, textField.getText(), null);
        CBLDataLoader.loadDocument(project, descriptor, tree);

        VirtualFile virtualFile = descriptor.getVirtualFile();
        if (virtualFile != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(virtualFile, true);
        } else {
            Log.debug("virtual file is null");
        }

        super.doOKAction();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(Math.max(preferredSize.width, MINIMUM_WIDTH), preferredSize.height);
    }

}
