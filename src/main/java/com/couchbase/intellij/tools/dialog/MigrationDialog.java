package com.couchbase.intellij.tools.dialog;

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

    @Override
    protected JComponent createCenterPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createMongoDBPanel(), "1");
        cardPanel.add(createDataSourcePanel(), "2");
        cardPanel.add(createBucketPanel(), "3");

        return cardPanel;
    }

    private JPanel createMongoDBPanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = JBUI.insets(5, 5, 5, 5);

        JPanel mongoDBPanel = new JPanel(new GridBagLayout());
        mongoDBPanel.setBorder(JBUI.Borders.empty(10));

        JLabel mongoDBLabel = new JLabel("MongoDB Connection String");
        mongoDBPanel.add(mongoDBLabel, gbc);

        gbc.gridx = 1;
        TextField mongoDBTextField = new TextField();
        mongoDBTextField.setBackground(JBUI.CurrentTheme.ContextHelp.FOREGROUND);
        mongoDBPanel.add(mongoDBTextField, gbc);

        return mongoDBPanel;
    }

    private JPanel createDataSourcePanel() {
        // Create your Data Source panel here

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = JBUI.insets(5, 5, 5, 5);

        JPanel dataSourcePanel = new JPanel(new GridBagLayout());
        dataSourcePanel.setBorder(JBUI.Borders.empty(10));

        TitledSeparator dataSourceSeparator = new TitledSeparator("Data Source");
        dataSourcePanel.add(dataSourceSeparator, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel databaseLabel = new JLabel("Database");
        dataSourcePanel.add(databaseLabel, gbc);

        gbc.gridx = 1;
        gbc.weighty = 1;

        ComboBox<String> databaseComboBox = new ComboBox<>();
        dataSourcePanel.add(databaseComboBox, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.gridx = 0;

        JLabel collectionsLabel = new JLabel("Collections");
        dataSourcePanel.add(collectionsLabel, gbc);

        gbc.gridx = 1;
        gbc.weighty = 1;

        ComboBox<String> collectionsComboBox = new ComboBox<>();
        dataSourcePanel.add(collectionsComboBox, gbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.gridx = 0;

        JCheckBox migrateCollectionsCheckBox = new JCheckBox("Migrate Collections Indexes and Definitions");
        dataSourcePanel.add(migrateCollectionsCheckBox, gbc);

        return dataSourcePanel;
    }

    private JPanel createBucketPanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = JBUI.insets(5, 5, 5, 5);

        JPanel bucketPanel = new JPanel(new GridBagLayout());
        bucketPanel.setBorder(JBUI.Borders.empty(10));

        TitledSeparator bucketSeparator = new TitledSeparator("Target");
        bucketPanel.add(bucketSeparator, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel bucketLabel = new JLabel("Bucket");
        bucketPanel.add(bucketLabel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weighty = 1;

        ComboBox<String> bucketComboBox = new ComboBox<>();

        gbc.gridx = 1;
        bucketPanel.add(bucketComboBox, gbc);

        JLabel scopeLabel = new JLabel("Scope");

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weighty = 0;
        bucketPanel.add(scopeLabel, gbc);

        ComboBox<String> scopeComboBox = new ComboBox<>();
        
        gbc.gridx = 1;
        bucketPanel.add(scopeComboBox, gbc);



        JCheckBox createMissingCollectionsCheckBox = new JCheckBox("Create Missing Collections");

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weighty = 0;
        bucketPanel.add(createMissingCollectionsCheckBox, gbc);

        return bucketPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> doCancelAction());
        southPanel.add(cancelButton);

        backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            previousPage();
            backButton.setEnabled(currentPage > 1);
            backButton.setVisible(currentPage > 1);
            nextButton.setText(currentPage == 3 ? IMPORT : NEXT);
            validateAndEnableNextButton();

        });
        backButton.setEnabled(currentPage > 1);
        backButton.setVisible(currentPage > 1);
        southPanel.add(backButton);

        nextButton = new JButton(NEXT);
        nextButton.addActionListener(e -> {
            if (currentPage == 3) {
                doOKAction();
            } else {
                nextPage();
                backButton.setEnabled(currentPage > 1);
                backButton.setVisible(currentPage > 1);
                nextButton.setText(currentPage == 3 ? IMPORT : NEXT);
                validateAndEnableNextButton();
            }
        });
        // nextButton.setEnabled(false);
        southPanel.add(nextButton);

        return southPanel;
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