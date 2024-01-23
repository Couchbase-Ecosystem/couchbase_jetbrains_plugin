package com.couchbase.intellij.tools.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBMigrate;
import com.couchbase.intellij.tools.CBMigrate.CBMigrateCommandBuilder;
import com.couchbase.intellij.tree.docfilter.DocumentFilterDialog;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import utils.TemplateUtil;

public class MigrationDialog extends DialogWrapper {

    private Project project;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton backButton;
    private JButton nextButton;
    private int currentPage = 1;

    protected JPanel mainPanel;
    protected JPanel universalErrorPanel;
    protected JPanel southPanel;

    protected static final String IMPORT = "Import";
    protected static final String NEXT = "Next";
    protected static final String BACK = "Back";
    protected static final String CANCEL = "Cancel";

    protected JBTextField mongoDBTextField;

    protected ComboBox<String> databaseComboBox;
    protected ComboBox<String> collectionsComboBox;
    protected ComboBox<String> targetBucketCombo;
    protected ComboBox<String> targetScopeCombo;

    protected List<ScopeSpec> targetScopeItems;

    protected JBLabel universalErrorLabel;

    public MigrationDialog(Project project) {
        super(true);
        this.project = project;
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

        mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(wrapPanel(createMongoDBPanel()), "1");
        cardPanel.add(wrapPanel(createDataSourcePanel()), "2");
        cardPanel.add(wrapPanel(createTargetPanel()), "3");

        mainPanel.add(cardPanel, BorderLayout.NORTH);

        universalErrorPanel = new JPanel();
        universalErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        universalErrorLabel = new JBLabel();
        universalErrorLabel.setForeground(Color.decode("#FF4444"));
        universalErrorLabel.setVisible(false);
        universalErrorPanel.add(universalErrorLabel);
        mainPanel.add(universalErrorPanel, BorderLayout.SOUTH);

        addListeners();
        return cardPanel;
    }

    private JPanel createMongoDBPanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
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

        mongoDBTextField = new JBTextField();
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

        databaseComboBox = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(databaseComboBox, gbc);

        JLabel collectionsLabel = new JLabel("Collections");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        dataSourcePanel.add(collectionsLabel, gbc);

        collectionsComboBox = new ComboBox<>();
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

        targetBucketCombo = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;

        Set<String> bucketSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
        String[] buckets = bucketSet.toArray(new String[0]);
        targetBucketCombo.setModel(new DefaultComboBoxModel<>(buckets));
        targetPanel.add(targetBucketCombo, gbc);

        JLabel scopeLabel = new JLabel("Scope");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        targetPanel.add(scopeLabel, gbc);

        targetScopeCombo = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        targetBucketCombo.setSelectedIndex(targetBucketCombo.getItemCount() > 0 ? 0 : -1);
        targetPanel.add(targetScopeCombo, gbc);

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

    protected void addListeners() {
        mongoDBTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                MongoDBConnection connection = new MongoDBConnection(mongoDBTextField.getText());
                databaseComboBox
                        .setModel(new DefaultComboBoxModel<>(connection.getDatabaseNames().toArray(new String[0])));

                // fire an event to update the collections combo box
                if (databaseComboBox.getItemCount() > 0) {
                    databaseComboBox.setSelectedIndex(0);
                }
            }

        });

        databaseComboBox.addActionListener(e -> {
            MongoDBConnection connection = new MongoDBConnection(mongoDBTextField.getText());
            collectionsComboBox.setModel(new DefaultComboBoxModel<>(connection
                    .getCollectionNames(Objects.requireNonNull(databaseComboBox.getSelectedItem()).toString())
                    .toArray(new String[0])));
        });

        targetBucketCombo.addActionListener(e -> {
            String selectedBucket = (String) targetBucketCombo.getSelectedItem();
            if (selectedBucket != null) {
                targetScopeItems = new ArrayList<>();
                targetScopeItems.addAll(ActiveCluster.getInstance().get().bucket(selectedBucket)
                        .collections().getAllScopes());

                Set<String> scopeSet = targetScopeItems.stream()
                        .map(ScopeSpec::name)
                        .collect(Collectors.toSet());
                targetScopeCombo.removeAllItems();

                String[] scopes = scopeSet.toArray(new String[0]);
                targetScopeCombo.setModel(new DefaultComboBoxModel<>(scopes));
            }
        });
    }

    private static class MongoDBConnection {
        private MongoClient mongoClient;

        public MongoDBConnection(String connectionString) {
            try {

                ServerApi serverApi = ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build();
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .serverApi(serverApi)
                        .build();
                // Create a new client and connect to the server
                MongoClient tempMongoClient = MongoClients.create(settings);

                // Send a ping to confirm a successful connection
                MongoDatabase database = tempMongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                Log.info("Pinged your deployment. You successfully connected to MongoDB!");
                this.mongoClient = tempMongoClient;

            } catch (IllegalArgumentException e) {
                Log.error("Invalid MongoDB Connection URI: " + connectionString);
                this.mongoClient = null;
            } catch (Exception e) {
                Log.error("Error connecting to MongoDB: " + e.getMessage());
                this.mongoClient = null;
            }
        }

        public List<String> getDatabaseNames() {
            try {
                return mongoClient.listDatabaseNames().into(new ArrayList<>());
            } catch (Exception e) {
                Log.error("Error fetching database names: " + e.getMessage());
                return new ArrayList<>();
            }
        }

        public List<String> getCollectionNames(String databaseName) {
            try {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                return database.listCollectionNames().into(new ArrayList<>());
            } catch (Exception e) {
                Log.error("Error fetching collection names: " + e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    @Override
    protected void doOKAction() {
        try {
            String mongoDBConnectionString = mongoDBTextField.getText();
            String databaseName = (String) databaseComboBox.getSelectedItem();
            String collectionName = (String) collectionsComboBox.getSelectedItem();
            String targetBucket = (String) targetBucketCombo.getSelectedItem();
            String targetScope = (String) targetScopeCombo.getSelectedItem();

            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();

            CBMigrateCommandBuilder builder = new CBMigrateCommandBuilder()
                    .setMongoDBURI(mongoDBConnectionString)
                    .setMongoDBDatabase(databaseName)
                    .setMongoDBCollection(collectionName)
                    .setCBBucket(targetBucket)
                    .setCBScope(targetScope)
                    .setCBCollection(collectionName) // TODO: change this to target collection
                    .setCBCluster(clusterURL)
                    .setCBUsername(username)
                    .setCBPassword(password)
                    .setCBGenerateKey("%_id%");
            // .setVerbose();

            CBMigrate.migrate(project, builder);
            super.doOKAction();

        } catch (Exception e) {
            Log.error("Error migrating data: " + e.getMessage());
            ApplicationManager.getApplication().invokeLater(
                    () -> Messages.showErrorDialog(project, "Error migrating data: " + e.getMessage(), "Error"));
        }

    }

}
