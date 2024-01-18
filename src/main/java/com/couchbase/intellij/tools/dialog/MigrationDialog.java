package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBUI;

public class MigrationDialog extends DialogWrapper {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton backButton;
    private JButton nextButton;
    private int currentPage = 1;
    protected JPanel southPanel;

    protected static final String IMPORT = "Import";
    protected static final String NEXT = "Next";
    protected static final String BACK = "Back";
    protected static final String CANCEL = "Cancel";

    public MigrationDialog() {
        super(true);
        init();
        setTitle("Data Migration");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
        setOKButtonText(IMPORT);
    }

    private JPanel wrapPanel(JPanel innerPanel) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(innerPanel, BorderLayout.NORTH);
        return outerPanel;
    }

    @Override
    protected JComponent createCenterPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(wrapPanel(createMongoDBPanel()), "1");
        cardPanel.add(wrapPanel(createDataSourcePanel()), "2");
        cardPanel.add(wrapPanel(createBucketPanel()), "3");

        return cardPanel;
    }

    private JPanel createMongoDBPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.05;
        gbc.weighty = 0;
        gbc.insets = JBUI.insets(5);

        JPanel mongoDBPanel = new JPanel(new GridBagLayout());

        JLabel mongoDBLabel = new JLabel("MongoDB Connection String");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mongoDBPanel.add(mongoDBLabel, gbc);

        TextField mongoDBTextField = new TextField();
        mongoDBTextField.setBackground(JBUI.CurrentTheme.ContextHelp.FOREGROUND);
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        mongoDBPanel.add(mongoDBTextField, gbc);

        return mongoDBPanel;
    }

    private JPanel createDataSourcePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.05;
        gbc.weighty = 0;
        gbc.insets = JBUI.insets(5);

        JPanel dataSourcePanel = new JPanel(new GridBagLayout());

        TitledSeparator dataSourceSeparator = new TitledSeparator("Data Source");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dataSourcePanel.add(dataSourceSeparator, gbc);

        JLabel databaseLabel = new JLabel("Database");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        dataSourcePanel.add(databaseLabel, gbc);

        ComboBox<String> databaseComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(databaseComboBox, gbc);

        JLabel collectionsLabel = new JLabel("Collections");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.05;
        dataSourcePanel.add(collectionsLabel, gbc);

        ComboBox<String> collectionsComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(collectionsComboBox, gbc);

        JCheckBox migrateCollectionsCheckBox = new JCheckBox("Migrate Collections Indexes and Definitions");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dataSourcePanel.add(migrateCollectionsCheckBox, gbc);

        return dataSourcePanel;
    }

    private JPanel createBucketPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.05;
        gbc.weighty = 0;
        gbc.insets = JBUI.insets(5);

        JPanel bucketPanel = new JPanel(new GridBagLayout());

        TitledSeparator bucketSeparator = new TitledSeparator("Target");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        bucketPanel.add(bucketSeparator, gbc);

        JLabel bucketLabel = new JLabel("Bucket");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bucketPanel.add(bucketLabel, gbc);

        ComboBox<String> bucketComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        bucketPanel.add(bucketComboBox, gbc);

        JLabel scopeLabel = new JLabel("Scope");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.05;
        bucketPanel.add(scopeLabel, gbc);

        ComboBox<String> scopeComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        bucketPanel.add(scopeComboBox, gbc);

        JCheckBox createMissingCollectionsCheckBox = new JCheckBox("Create Missing Collections");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        bucketPanel.add(createMissingCollectionsCheckBox, gbc);

        return bucketPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            if (currentPage == 1) {
                doCancelAction();
            } else {
                previousPage();
                updateButtonText();
                validateAndEnableNextButton();
            }
        });

        nextButton = new JButton(NEXT);
        nextButton.addActionListener(e -> {
            if (currentPage == 3) {
                doOKAction();
            } else {
                nextPage();
                updateButtonText();
                validateAndEnableNextButton();
            }
        });

        updateButtonText();
        southPanel.add(backButton);
        southPanel.add(nextButton);

        return southPanel;
    }

    protected void updateButtonText() {
        backButton.setText(currentPage == 1 ? CANCEL : BACK);
        nextButton.setText(currentPage == 3 ? IMPORT : NEXT);
    }

    protected void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
        }
    }

    protected void nextPage() {
        if (currentPage < 3) {
            currentPage++;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
        }
    }

    protected void validateAndEnableNextButton() {
        nextButton.setEnabled(true);
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

}
