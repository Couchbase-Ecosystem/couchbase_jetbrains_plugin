package com.couchbase.intellij.tools.cbmigrate;

import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.dialog.JComboCheckBox;
import com.couchbase.intellij.tree.docfilter.DocumentFilterDialog;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.Consumer;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import utils.TemplateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MigrationDialog extends DialogWrapper {

    public static final String ALL_COLLECTIONS_IN_THE_DATABASE = "All collections in the database";
    private final Project project;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    protected JBTextField mongoDBTextField;

    protected ComboBox<String> databaseComboBox;
    protected JComboCheckBox collectionsComboBox;

    protected ComboBox<String> targetBucketCombo;
    protected ComboBox<String> targetScopeCombo;

    protected List<ScopeSpec> targetScopeItems;

    protected static final Color ERROR_COLOR = Color.decode("#FF4444");
    protected static final Color SUCCESS_COLOR = Color.decode("#00C851");
    protected static final Color ESTABILISHING_COLOR = Color.decode("#FFA726");

    private MongoConnection mongoConnection;

    public MigrationDialog(Project project) {
        super(true);
        this.project = project;
        init();
        setTitle("Mongo To Couchbase Migration - Beta");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
    }

    @Override
    protected JComponent createCenterPanel() {

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createMongoDBPanel(), "0");
        cardPanel.add(createDataSourcePanel(), "1");
        cardPanel.add(createTargetPanel(), "2");

        return cardPanel;
    }

    private JPanel createMongoDBPanel() {

        JLabel mongoErrorLabel = new JLabel("");

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
                String content = """
                        <html>
                        <h2>MongoDB Connection URI</h2>
                            <div>
                            The MongoDB Connection URI is a string that specifies how to connect to a MongoDB database. It contains all the information needed to establish a connection, including:
                            <ul>
                                <li>Hostnames or IP addresses of MongoDB servers</li>
                                <li>Port number for the MongoDB server</li>
                                <li>Authentication credentials (username and password)</li>
                                <li>Database name</li>
                                <li>Additional connection options</li>
                            </ul>
                            Here is an example of a MongoDB Connection URI:
                            <pre>
                            mongodb://username:password@hostname1:27017,hostname2:27017/databaseName?authSource=admin&ssl=true
                            </pre>
                            <h3>Components of the MongoDB Connection URI</h3>
                            <ul>
                                <li><strong>mongodb://</strong> - Indicates the protocol for MongoDB</li>
                                <li><strong>username:password</strong> - Authentication credentials (optional)</li>
                                <li><strong>@hostname1:27017,hostname2:27017</strong> - Hostnames or IP addresses of MongoDB servers, along with port numbers</li>
                                <li><strong>/databaseName</strong> - Name of the database to connect to</li>
                                <li><strong>?authSource=admin&ssl=true</strong> - Additional connection options (optional)</li>
                            </ul>
                        </div>
                        </html>""";

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

        mongoDBTextField = new JBTextField();
        mongoDBTextField.getEmptyText().setText("Inform your connection string");

        gbc.gridx = 1;
        gbc.weightx = 0.95;
        gbc.gridwidth = 3;
        mongoDBPanel.add(mongoDBTextField, gbc);

        JButton connectButton = new JButton("Test Connection");
        connectButton.addActionListener(e -> {
            mongoErrorLabel.setText("Establishing Connection...");
            mongoErrorLabel.setForeground(ESTABILISHING_COLOR);
            mongoErrorLabel.setVisible(true);
            tryToConnectToMongo(mongoDBTextField.getText(), mongoErrorLabel, list -> {
                //do nothing
            });
        });

        gbc.gridy++;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.05;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        mongoDBPanel.add(connectButton, gbc);


        JPanel southErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southErrorPanel.add(mongoErrorLabel);

        JButton button = new JButton("Next");
        button.addActionListener(e -> {
            mongoErrorLabel.setText("Loading databases...");
            mongoErrorLabel.setForeground(ESTABILISHING_COLOR);
            mongoErrorLabel.setVisible(true);
            connectToMongo(mongoDBTextField.getText(), mongoErrorLabel, list -> {
                if (!list.isEmpty()) {
                    showDataSourcePanel(list);
                    mongoErrorLabel.setText("");
                }
            });

        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> {
            super.doCancelAction();
        });

        JPanel southButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southButtonPanel.add(cancel);
        southButtonPanel.add(button);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(southErrorPanel);
        southPanel.add(southButtonPanel);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mongoDBPanel, BorderLayout.NORTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showDataSourcePanel(List<String> databases) {

        collectionsComboBox.removeAllItems();
        collectionsComboBox.setEnabled(false);
        ActionListener[] listeners = databaseComboBox.getActionListeners();
        for (ActionListener listener : listeners) {
            databaseComboBox.removeActionListener(listener);
        }
        databaseComboBox.removeAllItems();
        for (String dbName : databases) {
            databaseComboBox.addItem(dbName);
        }

        databaseComboBox.setItem(null);

        databaseComboBox.addActionListener(e -> {
            if (databaseComboBox.getSelectedItem() == null) {
                return;
            }
            List<String> collectionNames = mongoConnection.getCollectionNames(databaseComboBox.getSelectedItem().toString());
            collectionsComboBox.removeAllItems();
            collectionsComboBox.addItem(ALL_COLLECTIONS_IN_THE_DATABASE);
            for (String collectionName : collectionNames) {
                collectionsComboBox.addItem(collectionName);
            }
            collectionsComboBox.setEnabled(true);
        });

        cardLayout.show(cardPanel, "1");

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
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", MigrationDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = """
                        <html>
                        <h2>Data Source Information</h2>
                        <div>
                          The data source panel allows you to specify the source of the data you want to migrate. You can select the database and collections from which you want to migrate data.
                          <h3>Database</h3>
                         Select the database from which you want to migrate data
                         <h3>Collections</h3>
                         Choose the specific collections within the selected database that you want to migrate.
                         <h3>Migrate Collections Indexes and Definitions</h3>
                         Check this option if you want to migrate the indexes and definitions of the selected collections.
                         </div>
                         </html>""";

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

        JCheckBox migrateCollectionsCheckBox = new JCheckBox("Migrate indexes definitions");
        migrateCollectionsCheckBox.setSelected(true);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dataSourcePanel.add(migrateCollectionsCheckBox, gbc);

        JLabel errorLabel = new JLabel();
        JPanel southErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southErrorPanel.add(errorLabel);

        JButton button = new JButton("Next");
        button.addActionListener(e -> {
            if (databaseComboBox.getSelectedItem() == null) {
                errorLabel.setForeground(ERROR_COLOR);
                errorLabel.setText("Please select the database");
                return;
            }

            List<String> selectedCols = collectionsComboBox.getSelectedItems();
            if (selectedCols.isEmpty()) {
                errorLabel.setForeground(ERROR_COLOR);
                errorLabel.setText("Please select at least one collection");
                return;
            }
            cardLayout.show(cardPanel, "2");
        });
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            cardLayout.show(cardPanel, "0");
        });

        JPanel southButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southButtonPanel.add(back);
        southButtonPanel.add(button);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(southErrorPanel);
        southPanel.add(southButtonPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(dataSourcePanel, BorderLayout.NORTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
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
                String content = """
                        <html><h2>Target Information</h2>
                        <div>
                        The target panel allows you to specify the destination for the migrated data. You can select the target bucket and scope where the data will be migrated to.
                        <h3>Bucket</h3>
                        Choose the destination bucket for the migration.
                        <h3>Scope</h3>
                        Select the scope within the chosen bucket where the data will be migrated.
                        </div>
                        </html>""";

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
        targetBucketCombo.setSelectedItem(null);
        targetPanel.add(targetBucketCombo, gbc);

        targetBucketCombo.addActionListener(e -> {
            String selectedBucket = (String) targetBucketCombo.getSelectedItem();
            if (selectedBucket != null) {
                targetScopeItems = new ArrayList<>();
                targetScopeItems.addAll(ActiveCluster.getInstance().get().bucket(selectedBucket)
                        .collections().getAllScopes());

                targetScopeCombo.removeAllItems();

                String[] scopes = targetScopeItems.stream()
                        .map(ScopeSpec::name).distinct().toArray(String[]::new);
                targetScopeCombo.setModel(new DefaultComboBoxModel<>(scopes));
            }
        });

        JLabel scopeLabel = new JLabel("Scope");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.05;
        targetPanel.add(scopeLabel, gbc);

        targetScopeCombo = new ComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        targetPanel.add(targetScopeCombo, gbc);

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            cardLayout.show(cardPanel, "1");
        });

        JLabel errorLabel = new JLabel();
        JPanel southErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southErrorPanel.add(errorLabel);

        JButton importButton = new JButton("Import");
        importButton.addActionListener(e -> {
            importMongoDataset();
        });

        JPanel southButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southButtonPanel.add(back);
        southButtonPanel.add(importButton);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(southErrorPanel);
        southPanel.add(southButtonPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(targetPanel, BorderLayout.NORTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void tryToConnectToMongo(String mongoDBUri, JLabel errorLabel, Consumer<List<String>> callback) {
        SwingUtilities.invokeLater(() -> {
            try {
                List<String> databases = MongoConnection.testConnection(mongoDBUri);

                if (!databases.isEmpty()) {
                    errorLabel.setForeground(SUCCESS_COLOR);
                    errorLabel.setText("Connection was successful");
                    callback.consume(databases);
                } else {
                    throw new IllegalStateException("Cluster has zero databases.");
                }
            } catch (Exception ex) {
                errorLabel.setForeground(ERROR_COLOR);
                errorLabel.setText("Connection failed. Please make sure your URI is valid");
                Log.error("Could not connect to MongoDB", ex);
                callback.consume(new ArrayList<>());
            }
        });
    }

    public void connectToMongo(String mongoDBUri, JLabel errorLabel, Consumer<List<String>> callback) {
        SwingUtilities.invokeLater(() -> {
            try {
                mongoConnection = new MongoConnection(mongoDBUri);
                List<String> databases = mongoConnection.listDatabases();

                if (!databases.isEmpty()) {
                    errorLabel.setForeground(SUCCESS_COLOR);
                    errorLabel.setText("Connection was successful");
                    callback.consume(databases);
                } else {
                    throw new IllegalStateException("Cluster has zero databases.");
                }
            } catch (Exception ex) {
                errorLabel.setForeground(ERROR_COLOR);
                errorLabel.setText("Connection failed. Please make sure your URI is valid");
                Log.error("Could not connect to MongoDB", ex);
                callback.consume(new ArrayList<>());
            }
        });
    }

    @Override
    protected Action @NotNull [] createActions() {
        // Return an empty array to not show the OK/Cancel buttons.
        return new Action[0];
    }


    protected void importMongoDataset() {
        try {
            String mongoDBConnectionString = mongoDBTextField.getText();
            String databaseName = (String) databaseComboBox.getSelectedItem();
            String targetBucket = (String) targetBucketCombo.getSelectedItem();
            String targetScope = (String) targetScopeCombo.getSelectedItem();

            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();


            List<String> selectedCols;
            if (collectionsComboBox.getSelectedItems().contains(ALL_COLLECTIONS_IN_THE_DATABASE)) {
                selectedCols = mongoConnection.getCollectionNames(databaseName);
            } else {
                selectedCols = collectionsComboBox.getSelectedItems();
            }

            List<CBMigrate.CBMigrateCommandBuilder> builders = new ArrayList<>();

            for (String collection : selectedCols) {
                CBMigrate.CBMigrateCommandBuilder builder = new CBMigrate.CBMigrateCommandBuilder()
                        .setMongoDBURI(mongoDBConnectionString)
                        .setMongoDBDatabase(databaseName)
                        .setMongoDBCollection(collection)
                        .setCBBucket(targetBucket)
                        .setCBScope(targetScope)
                        .setCBCollection(collection)
                        .setCBCluster(clusterURL)
                        .setCBUsername(username)
                        .setCBPassword(password)
                        .setCBGenerateKey("%_id%");
                builders.add(builder);
            }

            CBMigrate.migrateMultipleCollections(project, builders);
            super.doOKAction();
        } catch (Exception e) {
            Log.error("Error migrating data: " + e.getMessage(), e);
            ApplicationManager.getApplication().invokeLater(
                    () -> Messages.showErrorDialog(project, "Error migrating data: " + e.getMessage(), "Error"));
        }
    }

    @Override
    protected void dispose() {
        if (mongoConnection != null) {
            mongoConnection.closeConnection();
        }
        super.dispose();
    }

}
