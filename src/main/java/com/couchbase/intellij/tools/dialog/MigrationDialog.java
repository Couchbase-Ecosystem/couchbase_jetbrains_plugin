package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.couchbase.intellij.tree.docfilter.DocumentFilterDialog;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBUI;

import utils.TemplateUtil;

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
        cardPanel.add(wrapPanel(createTargetPanel()), "3");

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

        JLabel infoLabel = new JLabel();

        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><h2>MongoDB Connection URI</h2>\n" +
                        "<div>\n" +
                        "The MongoDB Connection URI is a string that specifies how to connect to a MongoDB database. It contains all the information needed to establish a connection, including:\n"
                        +
                        "<ul>\n" +
                        "    <li>Hostnames or IP addresses of MongoDB servers</li>\n" +
                        "    <li>Port number for the MongoDB server</li>\n" +
                        "    <li>Authentication credentials (username and password)</li>\n" +
                        "    <li>Database name</li>\n" +
                        "    <li>Additional connection options</li>\n" +
                        "</ul>\n" +
                        "Here is an example of a MongoDB Connection URI:\n" +
                        "<pre>\n" +
                        "mongodb://username:password@hostname1:27017,hostname2:27017/databaseName?authSource=admin&ssl=true\n"
                        +
                        "</pre>\n" +
                        "<h3>Components of the MongoDB Connection URI</h3>\n" +
                        "<ul>\n" +
                        "    <li><strong>mongodb://</strong> - Indicates the protocol for MongoDB</li>\n" +
                        "    <li><strong>username:password</strong> - Authentication credentials (optional)</li>\n" +
                        "    <li><strong>@hostname1:27017,hostname2:27017</strong> - Hostnames or IP addresses of MongoDB servers, along with port numbers</li>\n"
                        +
                        "    <li><strong>/databaseName</strong> - Name of the database to connect to</li>\n" +
                        "    <li><strong>?authSource=admin&ssl=true</strong> - Additional connection options (optional)</li>\n"
                        +
                        "</ul>\n" +
                        "</div>\n" +
                        "</html>";

                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(infoLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mongoDBPanel.add(helpPanel, gbc);

        JLabel mongoDBLabel = new JLabel("MongoDB Connection String");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
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

        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><h2>Data Source Information</h2>\n" +
                        "<div>\n" +
                        "The data source panel allows you to specify the source of the data you want to migrate. You can select the database and collections from which you want to migrate data.\n"
                        +
                        "<h3>Database</h3>\n" +
                        "Select the database from which you want to migrate data.\n" +
                        "<h3>Collections</h3>\n" +
                        "Choose the specific collections within the selected database that you want to migrate.\n" +
                        "<h3>Migrate Collections Indexes and Definitions</h3>\n" +
                        "Check this option if you want to migrate the indexes and definitions of the selected collections.\n"
                        +
                        "</div>\n" +
                        "</html>";

                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(infoLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dataSourcePanel.add(helpPanel, gbc);

        TitledSeparator dataSourceSeparator = new TitledSeparator("Data Source");
        gbc.gridx = 0;
        gbc.gridy++;
        dataSourcePanel.add(dataSourceSeparator, gbc);

        JLabel databaseLabel = new JLabel("Database");
        gbc.gridy++;
        gbc.gridwidth = 1;
        dataSourcePanel.add(databaseLabel, gbc);

        ComboBox<String> databaseComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(databaseComboBox, gbc);

        JLabel collectionsLabel = new JLabel("Collections");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        dataSourcePanel.add(collectionsLabel, gbc);

        ComboBox<String> collectionsComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(collectionsComboBox, gbc);

        JCheckBox migrateCollectionsCheckBox = new JCheckBox("Migrate Collections Indexes and Definitions");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dataSourcePanel.add(migrateCollectionsCheckBox, gbc);

        return dataSourcePanel;
    }

    private JPanel createTargetPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.05;
        gbc.weighty = 0;
        gbc.insets = JBUI.insets(5);

        JPanel targetPanel = new JPanel(new GridBagLayout());

        JLabel infoLabel = new JLabel();
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DocumentFilterDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = "<html><h2>Target Information</h2>\n" +
                        "<div>\n" +
                        "The target panel allows you to specify the destination for the migrated data. You can select the target bucket and scope where the data will be migrated to.\n"
                        +
                        "<h3>Bucket</h3>\n" +
                        "Choose the destination bucket for the migration.\n" +
                        "<h3>Scope</h3>\n" +
                        "Select the scope within the chosen bucket where the data will be migrated.\n" +
                        "<h3>Create Missing Collections</h3>\n" +
                        "Check this option if you want to create any missing collections in the selected scope during migration.\n"
                        +
                        "</div>\n" +
                        "</html>";

                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(infoLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        targetPanel.add(helpPanel, gbc);

        TitledSeparator bucketSeparator = new TitledSeparator("Target");
        gbc.gridx = 0;
        gbc.gridy++;
        targetPanel.add(bucketSeparator, gbc);

        JLabel bucketLabel = new JLabel("Bucket");
        gbc.gridy++;
        gbc.gridwidth = 1;
        targetPanel.add(bucketLabel, gbc);

        ComboBox<String> bucketComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        targetPanel.add(bucketComboBox, gbc);

        JLabel scopeLabel = new JLabel("Scope");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        targetPanel.add(scopeLabel, gbc);

        ComboBox<String> scopeComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        targetPanel.add(scopeComboBox, gbc);

        JCheckBox createMissingCollectionsCheckBox = new JCheckBox("Create Missing Collections");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        targetPanel.add(createMissingCollectionsCheckBox, gbc);

        return targetPanel;
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
