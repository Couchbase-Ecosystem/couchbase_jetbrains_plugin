package com.couchbase.intellij.tree;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.workbench.EditorOpening;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

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
    private DialogOperation operation;

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
// Relaxing constraints according to https://github.com/couchbaselabs/couchbase_jetbrains_plugin/issues/172
//        String regex = "^[A-Za-z0-9_.-]+$";
//        if (!documentId.matches(regex)) {
//            return false;
//        }

        return true;
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        errorLabel = new JLabel("");
        errorLabel.setName("documentIdError");
        errorLabel.setBorder(JBUI.Borders.empty(10, 0));
        errorLabel.setForeground(com.intellij.util.ui.UIUtil.getErrorForeground());
        JLabel label = new JLabel(createDocument ? "New Document Id:" : "Document Id:");
        textField = new JBTextField(30);
        textField.setName("documentId");
        label.setLabelFor(textField);
        generateStub = new JCheckBox("Generate template document");
        generateStub.setName("generateDocumentTemplate");

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
            generateStub.setToolTipText("Use the available schema, or start with an empty document if no schema is available");
            formPanel.add(generateStub, gc);
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

        ActiveCluster active = ActiveCluster.getInstance();
        String id = textField.getText();
        boolean template = generateStub.isSelected();
        DialogOperation pending = new DialogOperation(); operation = pending;
        com.intellij.openapi.util.Disposer.register(getDisposable(), pending::cancel);
        setOKActionEnabled(false); textField.setEnabled(false); generateStub.setEnabled(false);
        errorLabel.setText(createDocument ? "Creating document…" : "Opening document…");
        com.couchbase.intellij.database.ClusterUiTask.submit(project, active, "Opening Couchbase document", connection -> {
            pending.checkCancelled();
            var target = connection.sdk().bucket(bucket).scope(scope).collection(collection);
            boolean exists = target.exists(id).exists();
            pending.checkCancelled();
            if (!exists) {
                if (!createDocument) throw new IllegalArgumentException("There is no document with the specified id.");
                JsonObject content = JsonObject.create();
                if (template) {
                    content = connection.active().getCachedCollection(bucket, scope, collection)
                            .map(CouchbaseCollection::generateCachedDocument).orElseGet(JsonObject::create);
                    if (content.containsKey("id")) content.put("id", id);
                    else if (content.containsKey("ID")) content.put("ID", id);
                }
                pending.beginMutation(connection, () -> getCancelAction().setEnabled(false));
                try { target.insert(id, content); }
                catch (com.couchbase.client.core.error.DocumentExistsException concurrentCreate) {
                    // Open the existing document without replacing another writer's content.
                }
            }
            return DataLoader.readDocument(project, connection, bucket, scope, collection, id);
        }, file -> {
            if (isDisposed()) return;
            close(OK_EXIT_CODE);
            EditorOpening.openFile(project, file);
        }, error -> {
            if (isDisposed()) return;
            operation = null; getCancelAction().setEnabled(true);
            errorLabel.setText(error.getMessage() == null ? "Could not open the document. Try again." : error.getMessage());
            setOKActionEnabled(true); textField.setEnabled(true); generateStub.setEnabled(true);
        });
    }

    @Override public void doCancelAction() {
        if (operation == null || operation.cancel()) super.doCancelAction();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(Math.max(preferredSize.width, MINIMUM_WIDTH), preferredSize.height);
    }


}
