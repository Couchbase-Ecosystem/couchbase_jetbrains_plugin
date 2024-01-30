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
import javax.swing.SwingWorker;
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

    private final Project project;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JButton connectButton;
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
    protected JComboCheckBox collectionsComboBox;

    protected ComboBox<String> targetBucketCombo;
    protected ComboBox<String> targetScopeCombo;

    protected List<ScopeSpec> targetScopeItems;

    protected JBLabel universalErrorLabel;

    protected boolean isConnected = false;

    protected static final Color ERROR_COLOR = Color.decode("#FF4444");
    protected static final Color SUCCESS_COLOR = Color.decode("#00C851");
    protected static final Color ESTABILISHING_COLOR = Color.decode("#FFA726");

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

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        universalErrorPanel = new JPanel();
        universalErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        universalErrorLabel = new JBLabel();
        universalErrorLabel.setForeground(ERROR_COLOR);
        universalErrorLabel.setVisible(true);
        universalErrorPanel.add(universalErrorLabel);
        mainPanel.add(universalErrorPanel, BorderLayout.SOUTH);

        addListeners();
        return mainPanel;
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
                String content = new StringBuilder().append("<html><h2>MongoDB Connection URI</h2>\n").append("<div>\n")
                        .append("The MongoDB Connection URI is a string that specifies how to connect to a MongoDB database. It contains all the information needed to establish a connection, including:\n")
                        .append("<ul>\n").append("    <li>Hostnames or IP addresses of MongoDB servers</li>\n")
                        .append("    <li>Port number for the MongoDB server</li>\n")
                        .append("    <li>Authentication credentials (username and password)</li>\n")
                        .append("    <li>Database name</li>\n").append("    <li>Additional connection options</li>\n")
                        .append("</ul>\n").append("Here is an example of a MongoDB Connection URI:\n").append("<pre>\n")
                        .append("mongodb://username:password@hostname1:27017,hostname2:27017/databaseName?authSource=admin&ssl=true\n")
                        .append("</pre>\n").append("<h3>Components of the MongoDB Connection URI</h3>\n")
                        .append("<ul>\n")
                        .append("    <li><strong>mongodb://</strong> - Indicates the protocol for MongoDB</li>\n")
                        .append("    <li><strong>username:password</strong> - Authentication credentials (optional)</li>\n")
                        .append("    <li><strong>@hostname1:27017,hostname2:27017</strong> - Hostnames or IP addresses of MongoDB servers, along with port numbers</li>\n")
                        .append("    <li><strong>/databaseName</strong> - Name of the database to connect to</li>\n")
                        .append("    <li><strong>?authSource=admin&ssl=true</strong> - Additional connection options (optional)</li>\n")
                        .append("</ul>\n").append("</div>\n").append("</html>").toString();

                TemplateUtil.showGotItTooltip(e.getComponent(), content);
            }
        });

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpPanel.add(infoLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mongoDBPanel.add(helpPanel, gbc);

        JLabel mongoDBLabel = new JLabel("MongoDB Connection String");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        mongoDBPanel.add(mongoDBLabel, gbc);

        mongoDBTextField = new JBTextField("Please enter your MongoDB connection string");

        gbc.gridx = 1;
        gbc.weightx = 0.95;
        gbc.gridwidth = 3;
        mongoDBPanel.add(mongoDBTextField, gbc);

        connectButton = new JButton("Connect");
        gbc.gridy++;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.05;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        mongoDBPanel.add(connectButton, gbc);

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
                String content = new StringBuilder().append("<html><h2>Data Source Information</h2>\n")
                        .append("<div>\n")
                        .append("The data source panel allows you to specify the source of the data you want to migrate. You can select the database and collections from which you want to migrate data.\n")
                        .append("<h3>Database</h3>\n")
                        .append("Select the database from which you want to migrate data.\n")
                        .append("<h3>Collections</h3>\n")
                        .append("Choose the specific collections within the selected database that you want to migrate.\n")
                        .append("<h3>Migrate Collections Indexes and Definitions</h3>\n")
                        .append("Check this option if you want to migrate the indexes and definitions of the selected collections.\n")
                        .append("</div>\n").append("</html>").toString();

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

        collectionsComboBox = new JComboCheckBox();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        collectionsComboBox.setHint("Select collections to migrate");
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
                String content = new StringBuilder().append("<html><h2>Target Information</h2>\n").append("<div>\n")
                        .append("The target panel allows you to specify the destination for the migrated data. You can select the target bucket and scope where the data will be migrated to.\n")
                        .append("<h3>Bucket</h3>\n").append("Choose the destination bucket for the migration.\n")
                        .append("<h3>Scope</h3>\n")
                        .append("Select the scope within the chosen bucket where the data will be migrated.\n")
                        .append("<h3>Create Missing Collections</h3>\n")
                        .append("Check this option if you want to create any missing collections in the selected scope during migration.\n")
                        .append("</div>\n").append("</html>").toString();

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
        targetBucketCombo.setSelectedIndex(0);
        targetPanel.add(targetBucketCombo, gbc);

        JLabel scopeLabel = new JLabel("Scope");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        targetPanel.add(scopeLabel, gbc);

        targetScopeCombo = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        targetPanel.add(targetScopeCombo, gbc);

        JCheckBox createMissingCollectionsCheckBox = new JCheckBox("Auto create missing collections");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        createMissingCollectionsCheckBox.setSelected(true);
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
        nextButton.setEnabled(false);
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
            // Hide the connection status label if not on the first page
            universalErrorLabel.setVisible(currentPage == 1);
        }
    }

    protected void nextPage() {
        if (currentPage < 3) {
            currentPage++;
            cardLayout.show(cardPanel, Integer.toString(currentPage));
            // Hide the connection status label if not on the first page
            universalErrorLabel.setVisible(currentPage == 1);
        }
    }

    protected void validateAndEnableNextButton() {
        nextButton.setEnabled(isConnected);
    }

    protected void addListeners() {

        connectButton.addActionListener(e -> {
            universalErrorLabel.setText("Establishing Connection...");
            universalErrorLabel.setForeground(ESTABILISHING_COLOR);
            universalErrorLabel.setVisible(true);

            SwingWorker<MongoDBConnection, Void> worker = new SwingWorker<>() {
                @Override
                protected MongoDBConnection doInBackground() {
                    return new MongoDBConnection(mongoDBTextField.getText());
                }

                @Override
                protected void done() {
                    try {
                        MongoDBConnection connection = get();
                        databaseComboBox.setModel(
                                new DefaultComboBoxModel<>(connection.getDatabaseNames().toArray(new String[0])));
                        if (databaseComboBox.getItemCount() > 0) {
                            databaseComboBox.setSelectedIndex(0);
                        }

                        isConnected = connection.mongoClient != null;
                        if (isConnected) {
                            Log.info("Successfully connected to MongoDB");
                            universalErrorLabel.setForeground(SUCCESS_COLOR);
                            universalErrorLabel.setText("Connection was successful");
                        } else {
                            Log.error("Failed to connect to MongoDB");
                            universalErrorLabel.setForeground(ERROR_COLOR);
                            universalErrorLabel.setText("Connection failed. Please make sure your URI is valid");
                        }
                        validateAndEnableNextButton();
                    } catch (Exception ex) {
                        Log.error("Failed to connect to MongoDB");
                        universalErrorLabel.setForeground(ERROR_COLOR);
                        universalErrorLabel.setText("Connection failed. Please make sure your URI is valid");
                        validateAndEnableNextButton();
                    }
                }
            };
            worker.execute();
        });

        mongoDBTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                isConnected = false;
                universalErrorLabel.setForeground(ERROR_COLOR);
                universalErrorLabel.setText("Not connected");
                universalErrorLabel.setVisible(true);
                validateAndEnableNextButton();
            }
        });

        databaseComboBox.addActionListener(e -> {
            MongoDBConnection connection = new MongoDBConnection(mongoDBTextField.getText());
            List<String> collectionNames = connection
                    .getCollectionNames(Objects.requireNonNull(databaseComboBox.getSelectedItem()).toString());
            collectionsComboBox.removeAllItems(); // Clear the previous items
            collectionsComboBox.addItem("All collections in the database"); // Add the "All collections" option
            for (String collectionName : collectionNames) {
                collectionsComboBox.addItem(collectionName);
            }
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
            String targetBucket = (String) targetBucketCombo.getSelectedItem();
            String targetScope = (String) targetScopeCombo.getSelectedItem();

            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();

            List<CBMigrateCommandBuilder> builders = new ArrayList<>();

            for (String collections : collectionsComboBox.getSelectedItems()) {
                if (collections.equals("All collections in the database")) {
                    builders.clear();
                    // If "All collections in the database" is selected, add all collections to the
                    // builders list
                    MongoDBConnection connection = new MongoDBConnection(mongoDBTextField.getText());
                    List<String> allCollectionNames = connection.getCollectionNames(databaseName);
                    for (String allCollectionName : allCollectionNames) {
                        CBMigrateCommandBuilder builder = new CBMigrateCommandBuilder()
                                .setMongoDBURI(mongoDBConnectionString)
                                .setMongoDBDatabase(databaseName)
                                .setMongoDBCollection(allCollectionName)
                                .setCBBucket(targetBucket)
                                .setCBScope(targetScope)
                                .setCBCollection(allCollectionName)
                                .setCBCluster(clusterURL)
                                .setCBUsername(username)
                                .setCBPassword(password)
                                .setCBGenerateKey("%_id%");
                                // .setVerbose();
                        builders.add(builder);
                    }
                    break;
                } else {
                    // If a specific collection is selected, add it to the builders list
                    CBMigrateCommandBuilder builder = new CBMigrateCommandBuilder()
                            .setMongoDBURI(mongoDBConnectionString)
                            .setMongoDBDatabase(databaseName)
                            .setMongoDBCollection(collections)
                            .setCBBucket(targetBucket)
                            .setCBScope(targetScope)
                            .setCBCollection(collections)
                            .setCBCluster(clusterURL)
                            .setCBUsername(username)
                            .setCBPassword(password)
                            .setCBGenerateKey("%_id%");
                            // .setVerbose();
                    builders.add(builder);
                }
            }

            CBMigrate.migrateMultipleCollections(project, builders);
            super.doOKAction();

        } catch (Exception e) {
            Log.error("Error migrating data: " + e.getMessage());
            ApplicationManager.getApplication().invokeLater(
                    () -> Messages.showErrorDialog(project, "Error migrating data: " + e.getMessage(), "Error"));
        }
    }

}
