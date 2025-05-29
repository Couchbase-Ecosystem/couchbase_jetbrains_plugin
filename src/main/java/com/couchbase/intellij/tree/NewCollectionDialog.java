package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.DataLoader;
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
import java.util.List;
import java.util.stream.Collectors;

public class NewCollectionDialog extends DialogWrapper {

    private final String bucketName;
    private final String scopeName;
    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private JTextField textField;
    private JLabel errorLabel;
    private JSpinner maxTTL;

    protected NewCollectionDialog(Project project, String bucket, String scope, DefaultMutableTreeNode clickedNode, Tree tree) {
        super(project);
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
        panel.add(textField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Max Time-To-Live"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        maxTTL = new JSpinner(new SpinnerNumberModel(0, 0, 2147483648L, 1));

        panel.add(TemplateUtil.createComponentWithBalloon(maxTTL,
                                                          "The maximum time-to-live (TTL) for all documents in this collection in seconds. If enabled and a document is mutated with no TTL or a TTL greater than the maximum, its TTL will be set to the collection TTL." + "The largest TTL allowed is 2147483647 seconds. A 0 value means TTL is disabled.\n" + "\n" + "NOTE: if collection-level TTL is set, bucket-level TTL is ignored.\n"),
                  gbc);


        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
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
        List<CollectionSpec> collections = ActiveCluster.getInstance().get().bucket(bucketName).collections()
                                                        .getAllScopes().stream()
                                                        .filter(scope -> scope.name().equals(scopeName))
                                                        .flatMap(scope -> scope.collections().stream())
                                                        .collect(Collectors.toList());

        for (CollectionSpec collection : collections) {
            if (collection.name().equals(collectionName)) {
                // If it does, show error message
                errorLabel.setText("Collection with name " + collectionName + " already exists");
                return;
            }
        }

        CollectionManager colManager = ActiveCluster.getInstance().get().bucket(bucketName).collections();

        Double ttl = Double.parseDouble(maxTTL.getValue().toString());
        if (ttl.longValue() <= 0) {
            colManager.createCollection(CollectionSpec.create(collectionName, scopeName));
        } else {
            colManager.createCollection(
                    CollectionSpec.create(collectionName, scopeName, Duration.ofSeconds(ttl.longValue())));
        }

        Object userObject = clickedNode.getUserObject();
        if (userObject instanceof ScopeNodeDescriptor) {
            int childCount = clickedNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) clickedNode.getChildAt(i);
                Object childUserObject = childNode.getUserObject();

                if (childUserObject instanceof CollectionsNodeDescriptor) {
                    DataLoader.listCollections(childNode, tree);
                    break;
                }
            }


        } else {
            DataLoader.listCollections(clickedNode, tree);
        }
        super.doOKAction();
    }

}
