package com.couchbase.intellij.tree.cblite;

import com.couchbase.intellij.tree.cblite.nodes.CBLIndexesNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CBLCreateIndexDialog extends DialogWrapper {

    private final DefaultMutableTreeNode clickedNode;
    private final Tree tree;
    private JBTextField textField;

    private JBTextField attributesField;
    private JLabel errorLabel;
    private Project project;

    protected CBLCreateIndexDialog(Project project, DefaultMutableTreeNode
            clickedNode, Tree tree) {
        super(project);
        this.clickedNode = clickedNode;
        this.tree = tree;
        init();
        getOKAction().putValue(Action.NAME, "Create Index");
        setTitle("Create New Couchbase Lite Index");
        getPeer().getWindow().setMinimumSize(new Dimension(550, 200));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(550, 200);
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Index name:");

        panel.add(TemplateUtil.createComponentWithBalloon(nameLabel, "The name of the index that will be created. You can't specify a name that already exists or that contains special chars"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textField = new JBTextField();
        panel.add(textField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(TemplateUtil.createComponentWithBalloon(new JLabel("Attribute Names:"), "Comma-Separated attribute names"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        attributesField = new JBTextField();
        attributesField.getEmptyText().setText("Comma-separated attribute's name list");

        panel.add(attributesField, gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        main.add(panel, BorderLayout.CENTER);

        return main;
    }

    @Override
    protected void doOKAction() {

        List<String> attributeNames = null;

        List<String> errors = new ArrayList<>();
        if (textField.getText().isBlank()) {
            errors.add("Inform the index name");
        }

        if (attributesField.getText().isBlank()) {
            errors.add("Inform the attributes that will be indexed");
        } else {
            attributeNames = Arrays.stream(attributesField.getText().split(","))
                    .toList()
                    .stream()
                    .map(e -> e.trim())
                    .collect(Collectors.toList());
        }

        if (!errors.isEmpty()) {
            String errorMsg = String.join("<br>", errors);
            errorLabel.setText("<html>" + errorMsg + "</html>");
            return;
        }

        CBLIndexesNodeDescriptor indexesNode = (CBLIndexesNodeDescriptor) clickedNode.getUserObject();


        final List<String> attrs = attributeNames;
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                CBLDataLoader.createIndex(indexesNode.getScope(), indexesNode.getCollection(), textField.getText(), attrs);
                tree.collapsePath(new TreePath(clickedNode.getPath()));
                tree.expandPath(new TreePath(clickedNode.getPath()));
            } catch (Exception e) {
                Log.error("Failed to create the cblite index", e);
                Messages.showErrorDialog("Failed to create the index specified.", "Index Error");
            }
        });


        close(DialogWrapper.CANCEL_EXIT_CODE);
        super.doOKAction();
    }

}
