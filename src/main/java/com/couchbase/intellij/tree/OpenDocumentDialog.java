package com.couchbase.intellij.tree;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.tree.node.FileNodeDescriptor;
import com.couchbase.intellij.workbench.CustomSqlFileEditor;
import com.couchbase.intellij.workbench.Log;
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
import java.awt.*;
import java.util.Objects;


public class OpenDocumentDialog extends DialogWrapper {
    private static final int MINIMUM_WIDTH = 300;
    private JPanel panel;
    private JBTextField textField;

    private Project project;
    private String bucket;
    private String scope;
    private String collection;

    private JLabel errorLabel;

    private JCheckBox generateStub;

    private boolean createDocument;

    private Tree tree;

    protected OpenDocumentDialog(boolean createDocument, Project project, @Nullable Tree tree, String bucket, String scope, String collection) {
        super(true);
        this.createDocument = createDocument;
        this.project = project;
        this.tree = tree;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        init();
        if (createDocument) {
            setTitle("New Document");
        } else {
            setTitle("Open Document");
        }

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
        JLabel label = new JLabel(createDocument ? "New Document Id:" : "Document Id:");
        textField = new JBTextField(30);
        generateStub = new JCheckBox("Generate template document");

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

        if (createDocument && ActiveCluster.getInstance().hasQueryService()) {
            gc.gridy = 1;
            gc.gridx = 1;
            gc.weightx = 1;
            try {
                if (DataLoader.stubsAvailable(bucket, scope, collection)) {
                    formPanel.add(generateStub, gc);
                }
            } catch (Exception e) {
                Log.debug("Stubs not available for collection " + collection);
            }
        }

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

        ExistsResult result = ActiveCluster.getInstance().get().bucket(bucket).scope(scope).collection(collection).exists(textField.getText());

        if (!createDocument) {
            if (!result.exists()) {
                errorLabel.setText("There is no document with the specified id.");
                panel.revalidate();
                return;
            }
        } else {

            //the use might have specified a document that already exists, in this case we just open the document
            if (!result.exists()) {
                String documentContent = "{}";
                if (generateStub != null && generateStub.isSelected()) {
                    try {
                        documentContent = ActiveCluster.getInstance().getChild(bucket)
                                .flatMap(bucket -> bucket.getChild(scope))
                                .flatMap(scope -> scope.getChild(collection))
                                .map(col -> ((CouchbaseCollection) col).generateDocument())
                                .filter(Objects::nonNull)
                                .peek(o -> {
                                    if (o.containsKey("id")) {
                                        o.put("id", textField.getText());
                                    } else if (o.containsKey("ID")) {
                                        o.put("ID", textField.getText());
                                    }
                                })
                                .map(JsonObject::toString)
                                .findFirst().orElse(documentContent);
                    } catch (Exception e) {
                        Log.debug("Failed to create the template for the document", e);
                    }
                }

                try {
                    ActiveCluster.getInstance().getCluster().bucket(bucket)
                            .scope(scope).collection(collection)
                            .insert(textField.getText(), JsonObject.fromJson(documentContent));
                } catch (Exception e) {
                    Log.error("Failed to create the document", e);
                    Messages.showMessageDialog("An error occurred while creating the document", "Couchbase Plugin Error", Messages.getErrorIcon());
                }
            }
        }

        CustomSqlFileEditor.openDocument(project, bucket, scope, collection, textField.getText(), tree);

        super.doOKAction();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(Math.max(preferredSize.width, MINIMUM_WIDTH), preferredSize.height);
    }

}
