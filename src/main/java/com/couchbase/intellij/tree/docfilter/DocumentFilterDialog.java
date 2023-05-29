package com.couchbase.intellij.tree.docfilter;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.intellij.color.ColorHelper;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;
import com.couchbase.intellij.tree.node.CollectionNodeDescriptor;
import com.couchbase.intellij.workbench.SQLPPQueryUtils;
import com.couchbase.intellij.workbench.error.CouchbaseQueryErrorUtil;
import com.couchbase.intellij.workbench.error.CouchbaseQueryResultError;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import org.intellij.sdk.language.SQLPPFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DocumentFilterDialog extends DialogWrapper {
    private EditorEx editor;
    private String collectionName;
    private String bucket;
    private String scope;

    private Tree tree;

    private JLabel resultsLabel;
    private JLabel errorLabel;

    private DefaultMutableTreeNode clickedNode;

    public DocumentFilterDialog(Tree tree, DefaultMutableTreeNode clickedNode, String bucket, String scope, String collectionName) {
        super(false);
        setTitle("Apply Filter for Collection '" + collectionName + "'");
        this.collectionName = collectionName;
        this.scope = scope;
        this.bucket = bucket;
        this.tree = tree;
        this.clickedNode = clickedNode;

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        String fontKeywordColor = ColorHelper.getKeywordColor();
        String fontStringColor = ColorHelper.getStringColor();

        JPanel panel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.findIcon("assets/icons/information_big.svg"));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><div style='margin: 10 10 10 10'>Complete the query with filters you would like to apply: <br>" +
                        "<strong>Ex:</strong> country = <span style='color:" + fontStringColor + "'>'United States'</span><br>" +
                        "<ul> <li>If you don't provide an <span style='color:" + fontKeywordColor + "'>ORDER BY</span>, the documents will be sorted by the document's id.</li>" +
                        "<li>Your filter should not include a <span style='color:" + fontKeywordColor + "'>LIMIT</span> or <span style='color:" + fontKeywordColor + "'>OFFSET</span></div></html>";
                showGotItTooltip(e.getComponent(), content);
            }
        });

        infoPanel.add(infoLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.NORTH);

        JPanel labelPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html><pre><strong style=\"color:" + fontKeywordColor + ";\">SELECT</strong>" +
                " meta().id <strong style=\"color:" + fontKeywordColor + "\">FROM</strong> `" + collectionName + "` <strong style=color:\""
                + fontKeywordColor + "\">WHERE</strong></pre></html>");
        label.setBorder(new EmptyBorder(0, 45, 0, 0));
        label.setVerticalAlignment(JLabel.TOP);
        labelPanel.add(label, BorderLayout.CENTER);
        panel.add(labelPanel, BorderLayout.CENTER);

        editor = createEditor();
        JScrollPane scrollPane = new JBScrollPane(editor.getComponent());
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
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
            if (!validateFilterKeywords(filter)) {
                resultsLabel.setText("");
                return;
            }

            try {
                int results = validateQueryFilters(bucket, scope, collectionName, filter);
                if (results == 0) {
                    errorLabel.setText("");
                    resultsLabel.setText("The query was successful, but it returned 0 results.");
                } else {
                    errorLabel.setText("");
                    resultsLabel.setText("The query ran successfully.");
                }
            } catch (CouchbaseException ex) {
                resultsLabel.setText("");
                try {
                    CouchbaseQueryResultError err = CouchbaseQueryErrorUtil.parseQueryError(ex);
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

            String filter = editor.getDocument().getText();
            if (!validateFilterKeywords(filter)) {
                resultsLabel.setText("");
                return;
            }

            try {
                validateQueryFilters(bucket, scope, collectionName, filter);
            } catch (CouchbaseException ex) {
                resultsLabel.setText("");
                try {
                    CouchbaseQueryResultError err = CouchbaseQueryErrorUtil.parseQueryError(ex);
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

            QueryFiltersStorage.getInstance().getValue()
                    .saveQueryFilter(
                            ActiveCluster.getInstance().getId(),
                            bucket,
                            scope,
                            collectionName,
                            filter);

            CollectionNodeDescriptor coll = (CollectionNodeDescriptor) clickedNode.getUserObject();
            coll.setQueryFilter(filter);
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
        setSize(700, 300);
    }

    private EditorEx createEditor() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        String content = QueryFiltersStorage.getInstance().getValue().getQueryFilter(
                ActiveCluster.getInstance().getId(),
                bucket,
                scope,
                collectionName);
        if (content == null) {
            content = "";
        }
        Document document = editorFactory.createDocument(content);
        editor = (EditorEx) editorFactory.createEditor(document, null, SQLPPFileType.INSTANCE, false);
        editor.setCaretEnabled(true);
        editor.setBorder(BorderFactory.createEmptyBorder());
        return editor;
    }


    private static void showGotItTooltip(Component component, String tooltipText) {
        Point screenPoint = component.getLocationOnScreen();
        Point tooltipPoint = new Point(screenPoint.x + component.getWidth(), screenPoint.y);

        Balloon balloon = JBPopupFactory.getInstance()
                .createBalloonBuilder(new JLabel(tooltipText))
                .setFillColor(UIUtil.getToolTipBackground())
                .setBorderColor(JBColor.GRAY)
                .setAnimationCycle(200)
                .setCloseButtonEnabled(true)
                .setHideOnClickOutside(true)
                .setHideOnKeyOutside(true)
                .createBalloon();

        balloon.show(new RelativePoint(tooltipPoint), Balloon.Position.above);
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

    private int validateQueryFilters(String bucket, String scope, String collectionName, String filter) {

        String query = "SELECT * FROM `" + collectionName + "` WHERE "
                + filter
                + (SQLPPQueryUtils.hasOrderBy(filter) ? "" : " ORDER BY meta().id")
                + " LIMIT 10 OFFSET 0";
        return ActiveCluster.getInstance().get()
                .bucket(bucket)
                .scope(scope)
                .query(query).rowsAsObject().size();
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

}


