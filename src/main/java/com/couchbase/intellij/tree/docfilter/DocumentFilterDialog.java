package com.couchbase.intellij.tree.docfilter;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.QueryFilter;
import com.couchbase.intellij.tree.node.CollectionNodeDescriptor;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.intellij.sdk.language.SQLPPFileType;
import org.jetbrains.annotations.Nullable;
import utils.ColorHelper;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DocumentFilterDialog extends DialogWrapper {
    private final String collectionName;
    private final String bucket;
    private final String scope;
    private final Tree tree;
    private final DefaultMutableTreeNode clickedNode;
    private final boolean hasIndex;
    private EditorEx editor;
    private JLabel resultsLabel;
    private JLabel errorLabel;
    private JBTextField startingDocumentIdField;
    private JBTextField endingDocumentIdField;
    private JSpinner offsetField;
    private JRadioButton queryRadioButton;
    private JRadioButton keyValueRadioButton;
    private QueryFilter queryFilter;

    public DocumentFilterDialog(Tree tree, DefaultMutableTreeNode clickedNode, String bucket, String scope, String collectionName, boolean hasIndex) {
        super(false);
        setTitle("Apply Filter for Collection '" + collectionName + "'");
        this.collectionName = collectionName;
        this.scope = scope;
        this.bucket = bucket;
        this.tree = tree;
        this.hasIndex = hasIndex;
        this.clickedNode = clickedNode;
        this.queryFilter = QueryFilterUtil.getQueryFilter(bucket, scope, collectionName);

        init();
    }


    public static String addLineBreaks(String input) {
        StringBuilder output = new StringBuilder();
        int count = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            output.append(c);
            count++;

            if (count >= 100 && Character.isWhitespace(c)) {
                output.append("<br>");
                count = 0;
            }
        }

        return output.toString();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        String fontKeywordColor = ColorHelper.getKeywordColor();
        String fontStringColor = ColorHelper.getStringColor();

        JPanel mainPanel = new JPanel(new BorderLayout());

        queryRadioButton = new JRadioButton("Query");
        keyValueRadioButton = new JRadioButton("Key-Value");

        ButtonGroup group = new ButtonGroup();
        group.add(queryRadioButton);
        group.add(keyValueRadioButton);

        JLabel filterLabel = new JLabel("Filter Type:");
        filterLabel.setBorder(JBUI.Borders.emptyRight(15));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(filterLabel);
        radioPanel.add(queryRadioButton);
        radioPanel.add(keyValueRadioButton);

        mainPanel.add(radioPanel, BorderLayout.NORTH);

        JPanel queryPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><div style='margin: 10 10 10 10'>Complete the query with filters you would like to apply: <br>" + "<strong>Ex:</strong> country = <span style='color:" + fontStringColor + "'>'United States'</span><br>" + "<ul> <li>If you don't provide an <span style='color:" + fontKeywordColor + "'>ORDER BY</span>, the documents will be sorted by the document's id.</li>" + "<li>Your filter should not include a <span style='color:" + fontKeywordColor + "'>LIMIT</span> or <span style='color:" + fontKeywordColor + "'>OFFSET</span></div></html>";
                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });

        infoPanel.add(infoLabel, BorderLayout.EAST);
        queryPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel labelPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(
                "<html><pre><strong style=\"color:" + fontKeywordColor + ";\">SELECT</strong>" + " meta().id <strong style=\"color:" + fontKeywordColor + "\">FROM</strong> `" + collectionName + "` <strong style=color:\"" + fontKeywordColor + "\">WHERE</strong></pre></html>");
        label.setBorder(JBUI.Borders.emptyLeft(45));
        label.setVerticalAlignment(JLabel.TOP);
        labelPanel.add(label, BorderLayout.CENTER);
        queryPanel.add(labelPanel, BorderLayout.CENTER);

        editor = createEditor();
        JScrollPane scrollPane = new JBScrollPane(editor.getComponent());
        queryPanel.add(scrollPane, BorderLayout.SOUTH);

        JPanel keyValuePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        startingDocumentIdField = new JBTextField(20);
        endingDocumentIdField = new JBTextField(20);
        offsetField = new JSpinner(
                new SpinnerNumberModel(queryFilter == null ? 0 : queryFilter.getOffset(), 0, 2147483648L, 1));


        if (queryFilter != null) {
            startingDocumentIdField.setText(
                    queryFilter.getDocumentStartKey() != null ? queryFilter.getDocumentStartKey() : "");
            endingDocumentIdField.setText(
                    queryFilter.getDocumentEndKey() != null ? queryFilter.getDocumentEndKey() : "");
            offsetField.setValue(Double.valueOf(queryFilter.getOffset()));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.4;
        keyValuePanel.add(new JLabel("Starting Document ID"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.6;
        keyValuePanel.add(startingDocumentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.4;
        keyValuePanel.add(new JLabel("Ending Document ID"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.6;
        keyValuePanel.add(endingDocumentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.4;
        keyValuePanel.add(new JLabel("Offset"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.6;
        keyValuePanel.add(offsetField, gbc);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.add(queryPanel, "Query");
        contentPanel.add(keyValuePanel, "Key-Value");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        queryRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, "Query");
            }
        });

        keyValueRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, "Key-Value");
            }
        });

        if (!ActiveCluster.getInstance().hasQueryService() || !hasIndex) {
            keyValueRadioButton.setSelected(true);
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, "Key-Value");
            queryRadioButton.setVisible(false);

        } else {

            if (queryFilter == null || (queryFilter.getQuery() != null && !queryFilter.getQuery().isEmpty())) {
                queryRadioButton.setSelected(true);
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, "Query");
            } else {
                keyValueRadioButton.setSelected(true);
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, "Key-Value");
            }
        }

        return mainPanel;
    }


    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout());


        resultsLabel = new JLabel("");
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.add(resultsLabel, BorderLayout.NORTH);
        notificationPanel.add(errorLabel, BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new BorderLayout());

        resultsPanel.add(notificationPanel, BorderLayout.WEST);
        panel.add(resultsPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            close(0);
        });
        buttonPanel.add(cancelButton);

        JButton testButton = new JButton("Test");
        testButton.addActionListener(e -> {
            String filter = editor.getDocument().getText();

            try {
                int results;
                if (keyValueRadioButton.isSelected()) {
                    if (!validateKVFilters()) {
                        resultsLabel.setText("");
                        return;
                    }

                    results = CouchbaseRestAPI.listKVDocuments(bucket, scope, collectionName,
                                                               ((Double) offsetField.getValue()).intValue(), 10,
                                                               "".equals(startingDocumentIdField.getText()
                                                                                                .trim()) ? null : startingDocumentIdField
                                                                       .getText().trim(), "".equals(
                                                              endingDocumentIdField.getText().trim()) ? null : endingDocumentIdField.getText()
                                                                                                                                    .trim())
                                              .size();
                } else {
                    if (!validateFilterKeywords(filter)) {
                        resultsLabel.setText("");
                        return;
                    }
                    results = validateQueryFilters(bucket, scope, collectionName, filter);
                }

                if (results == 0) {
                    errorLabel.setText("");
                    resultsLabel.setText("The query was successful, but it returned 0 results.");
                } else {
                    errorLabel.setText("");
                    resultsLabel.setText("The query ran successfully.");
                }
            } catch (Exception ex) {
                resultsLabel.setText("");
                try {
                    CouchbaseQueryResultError err = CouchbaseQueryErrorUtil.parseQueryError((CouchbaseException) ex);
                    if (!err.getErrors().isEmpty()) {
                        errorLabel.setText("<html>" + addLineBreaks(err.getErrors().get(0).getMessage()) + "</html>");
                    } else {
                        throw new IllegalStateException();
                    }
                } catch (Exception ee) {
                    errorLabel.setText("An error occurred while running the query");
                }
            }
        });
        buttonPanel.add(testButton);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {

            try {
                if (keyValueRadioButton.isSelected()) {
                    if (!validateKVFilters()) {
                        resultsLabel.setText("");
                        return;
                    }

                    CouchbaseRestAPI.listKVDocuments(bucket, scope, collectionName,
                                                     ((Double) offsetField.getValue()).intValue(), 10, "".equals(
                                    startingDocumentIdField.getText().trim()) ? null : startingDocumentIdField.getText()
                                                                                                              .trim(),
                                                     "".equals(endingDocumentIdField.getText()
                                                                                    .trim()) ? null : endingDocumentIdField
                                                             .getText().trim()).size();
                } else {
                    String filter = editor.getDocument().getText();
                    if (!validateFilterKeywords(filter)) {
                        resultsLabel.setText("");
                        return;
                    }
                    validateQueryFilters(bucket, scope, collectionName, filter);
                }
            } catch (Exception ex) {
                resultsLabel.setText("");
                try {
                    CouchbaseQueryResultError err = CouchbaseQueryErrorUtil.parseQueryError((CouchbaseException) ex);
                    if (!err.getErrors().isEmpty()) {
                        errorLabel.setText("<html>" + addLineBreaks(err.getErrors().get(0).getMessage()) + "</html>");
                    } else {
                        throw new IllegalStateException();
                    }
                    return;
                } catch (Exception ee) {
                    errorLabel.setText("An error occurred while running the query");
                    return;
                }
            }

            if (queryFilter == null) {
                queryFilter = new QueryFilter();
            }

            if (keyValueRadioButton.isSelected()) {
                queryFilter.setQuery(null);
                queryFilter.setDocumentStartKey(
                        startingDocumentIdField.getText().trim().isEmpty() ? null : startingDocumentIdField.getText()
                                                                                                           .trim());
                queryFilter.setDocumentEndKey(
                        endingDocumentIdField.getText().trim().isEmpty() ? null : endingDocumentIdField.getText()
                                                                                                       .trim());
                queryFilter.setOffset(((Double) offsetField.getValue()).intValue());
            } else {
                queryFilter.setQuery(editor.getDocument().getText());
                queryFilter.setDocumentStartKey(null);
                queryFilter.setDocumentEndKey(null);
                queryFilter.setOffset(0);
            }

            try {
                QueryFilterUtil.saveQueryFilter(bucket, scope, collectionName, queryFilter);
            } catch (Exception ee) {
                errorLabel.setText("An error occurred while trying to save the filter");
                return;
            }

            CollectionNodeDescriptor coll = (CollectionNodeDescriptor) clickedNode.getUserObject();
            coll.setQueryFilter(queryFilter);
            TreePath treePath = new TreePath(clickedNode.getPath());
            tree.collapsePath(treePath);
            tree.expandPath(treePath);
            close(0);

        });
        buttonPanel.add(applyButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void init() {
        super.init();
        setSize(650, 350);
    }

    private EditorEx createEditor() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        String content = queryFilter != null ? queryFilter.getQuery() : null;
        if (content == null) {
            content = "";
        }
        Document document = editorFactory.createDocument(content);
        editor = (EditorEx) editorFactory.createEditor(document, null, SQLPPFileType.INSTANCE, false);
        editor.setCaretEnabled(true);
        editor.setBorder(BorderFactory.createEmptyBorder());
        return editor;
    }

    private boolean validateFilterKeywords(String filter) {
        if (filter.trim().isEmpty()) {
            errorLabel.setText("The query can't be empty");
            return false;
        }
        String[] tokens = filter.split(" ");
        for (String token : tokens) {
            if ("OFFSET".equalsIgnoreCase(token.trim()) || "LIMIT".equalsIgnoreCase(token.trim())) {
                errorLabel.setText("The filters should not contain LIMIT and OFFSET");
                return false;
            }
        }
        return true;
    }

    private boolean validateKVFilters() {
        if (endingDocumentIdField.getText().trim().isEmpty() && startingDocumentIdField.getText().trim()
                                                                                       .isEmpty() && ((Double) offsetField.getValue()).intValue() == 0) {
            errorLabel.setText("You must specify at least one filtering value");
            return false;
        }

        if (((Double) offsetField.getValue()).intValue() > 1000) {
            errorLabel.setText("The offset should be less than 1000");
            return false;
        }
        return true;
    }

    private int validateQueryFilters(String bucket, String scope, String collectionName, String filter) {

        String query = "SELECT * FROM `" + collectionName + "` WHERE " + filter + (SQLPPQueryUtils.hasOrderBy(
                filter) ? "" : " ORDER BY meta().id") + " LIMIT 10 OFFSET 0";
        return ActiveCluster.getInstance().get().bucket(bucket).scope(scope).query(query).rowsAsObject().size();
    }

}


