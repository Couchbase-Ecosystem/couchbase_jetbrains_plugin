package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CreateCollectionSettings;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.tree.node.CollectionsNodeDescriptor;
import com.couchbase.intellij.tree.node.ScopeNodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.time.Duration;

public class NewCollectionDialog extends DialogWrapper {

    private final String bucketName;
    private final String scopeName;
    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private final Project project;
    private JTextField textField;
    private JLabel errorLabel;
    private JSpinner maxTTL;
    private DialogOperation operation;

    protected NewCollectionDialog(Project project, String bucket, String scope, DefaultMutableTreeNode clickedNode, Tree tree) {
        super(project);
        this.project = project;
        this.bucketName = bucket;
        this.scopeName = scope;
        this.clickedNode = clickedNode;
        this.tree = tree;
        init();
        setTitle("Create new Collection");
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Collection's name:");
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        textField = new JTextField(20);
        textField.setName("newCollectionName");
        nameLabel.setLabelFor(textField);
        panel.add(textField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Max Time-To-Live"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        maxTTL = new JSpinner(new SpinnerNumberModel(0, 0, 2147483648L, 1));
        maxTTL.setName("newCollectionMaxTtl");

        panel.add(TemplateUtil.createComponentWithBalloon(maxTTL,
                                                          "The maximum time-to-live (TTL) for all documents in this collection in seconds. If enabled and a document is mutated with no TTL or a TTL greater than the maximum, its TTL will be set to the collection TTL." + "The largest TTL allowed is 2147483647 seconds. A 0 value means TTL is disabled.\n" + "\n" + "NOTE: if collection-level TTL is set, bucket-level TTL is ignored.\n"),
                  gbc);


        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setName("newCollectionError");
        errorLabel.setForeground(com.intellij.util.ui.UIUtil.getErrorForeground());
        panel.add(errorLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {

        if (textField.getText().isEmpty()) {
            errorLabel.setText("Please enter a name for the collection");
            return;
        }

        if (!textField.getText().matches("[a-zA-Z0-9_]+")) {
            errorLabel.setText("Special characters are not allowed in collection creation");
            return;
        }

        String collectionName = textField.getText();
        ActiveCluster active = ActiveCluster.getInstance();
        long ttl = ((Number) maxTTL.getValue()).longValue();
        DialogOperation pending = new DialogOperation(); operation = pending;
        com.intellij.openapi.util.Disposer.register(getDisposable(), pending::cancel);
        setOKActionEnabled(false); textField.setEnabled(false); maxTTL.setEnabled(false);
        errorLabel.setText("Creating collection…");
        com.couchbase.intellij.database.ClusterUiTask.submit(project, active, "Creating Couchbase collection", connection -> {
            pending.checkCancelled(); connection.requireWritable();
            CollectionManager manager = connection.sdk().bucket(bucketName).collections();
            if (manager.getScope(scopeName).collections().stream().anyMatch(col -> col.name().equals(collectionName)))
                throw new IllegalArgumentException("Collection with name " + collectionName + " already exists");
            pending.beginMutation(connection, () -> getCancelAction().setEnabled(false));
            if (ttl <= 0) manager.createCollection(scopeName, collectionName);
            else manager.createCollection(scopeName, collectionName, CreateCollectionSettings.createCollectionSettings().maxExpiry(Duration.ofSeconds(ttl)));
            return collectionName;
        }, name -> {
            if (clickedNode != null && clickedNode.getRoot() == tree.getModel().getRoot()) {
                if (clickedNode.getUserObject() instanceof ScopeNodeDescriptor) {
                    for (int i = 0; i < clickedNode.getChildCount(); i++) {
                        var child = (DefaultMutableTreeNode) clickedNode.getChildAt(i);
                        if (child.getUserObject() instanceof CollectionsNodeDescriptor) { reloadShowing(child, name); break; }
                    }
                } else reloadShowing(clickedNode, name);
            }
            if (!isDisposed()) close(OK_EXIT_CODE);
        }, error -> {
            if (isDisposed()) return;
            operation = null; getCancelAction().setEnabled(true);
            errorLabel.setText(error.getMessage() == null ? "Could not create the collection. Try again." : error.getMessage());
            setOKActionEnabled(true); textField.setEnabled(true); maxTTL.setEnabled(true);
        });
    }

    @Override public void doCancelAction() {
        if (operation == null || operation.cancel()) super.doCancelAction();
    }

    /** Reloads the list, making sure the new collection is visible even if it falls beyond the loaded pages. */
    private void reloadShowing(DefaultMutableTreeNode collectionsNode, String collectionName) {
        CollectionsNodeDescriptor desc = (CollectionsNodeDescriptor) collectionsNode.getUserObject();
        desc.addSearchAdded(collectionName);
        DataLoader.listCollections(collectionsNode, tree, Math.max(desc.getPagedLoaded(), CollectionListing.PAGE_SIZE));
    }

}
