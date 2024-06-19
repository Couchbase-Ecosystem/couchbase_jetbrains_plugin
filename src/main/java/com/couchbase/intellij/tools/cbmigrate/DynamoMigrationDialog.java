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
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import utils.TemplateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DynamoMigrationDialog extends DialogWrapper {

    public static final String ALL_TABLES_IN_THE_DATABASE = "All tables in the database";
    public static final String NO_PROFILES_FOUND = "No profiles found";
    protected static final Color ERROR_COLOR = Color.decode("#FF4444");
    protected static final Color SUCCESS_COLOR = Color.decode("#00C851");
    protected static final Color ESTABILISHING_COLOR = Color.decode("#FFA726");
    private final Project project;
    protected ComboBox<AWSRegion> regionBox;
    protected JComboCheckBox tablesCombobox;
    protected ComboBox<String> targetBucketCombo;
    protected ComboBox<String> targetScopeCombo;
    protected List<ScopeSpec> targetScopeItems;
    private JRadioButton awsProfileButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JComboBox<String> profileBox;
    private JTextField accessKeyField;
    private JTextField secretKeyField;
    private JLabel connectionError = new JLabel("");
    private JCheckBox migrateIdxCheckBox;
    private JCheckBox verboseModeCheckbox;
    private JCheckBox sslNoVerifyCheckbox;

    public DynamoMigrationDialog(Project project) {
        super(true);
        this.project = project;
        init();
        setTitle("DynamoDB To Couchbase Migration - Beta");
        getWindow().setMinimumSize(new Dimension(600, 400));
        setResizable(true);
    }

    private static List<AWSRegion> getRegions() {
        List<AWSRegion> regions = new ArrayList<>();
        regions.add(new AWSRegion("US East (N. Virginia)", "us-east-1"));
        regions.add(new AWSRegion("US East (Ohio)", "us-east-2"));
        regions.add(new AWSRegion("US West (N. California)", "us-west-1"));
        regions.add(new AWSRegion("US West (Oregon)", "us-west-2"));
        regions.add(new AWSRegion("Africa (Cape Town)", "af-south-1"));
        regions.add(new AWSRegion("Asia Pacific (Hong Kong)", "ap-east-1"));
        regions.add(new AWSRegion("Asia Pacific (Mumbai)", "ap-south-1"));
        regions.add(new AWSRegion("Asia Pacific (Osaka)", "ap-northeast-3"));
        regions.add(new AWSRegion("Asia Pacific (Seoul)", "ap-northeast-2"));
        regions.add(new AWSRegion("Asia Pacific (Singapore)", "ap-southeast-1"));
        regions.add(new AWSRegion("Asia Pacific (Sydney)", "ap-southeast-2"));
        regions.add(new AWSRegion("Asia Pacific (Tokyo)", "ap-northeast-1"));
        regions.add(new AWSRegion("Canada (Central)", "ca-central-1"));
        regions.add(new AWSRegion("China (Beijing)", "cn-north-1"));
        regions.add(new AWSRegion("China (Ningxia)", "cn-northwest-1"));
        regions.add(new AWSRegion("Europe (Frankfurt)", "eu-central-1"));
        regions.add(new AWSRegion("Europe (Ireland)", "eu-west-1"));
        regions.add(new AWSRegion("Europe (London)", "eu-west-2"));
        regions.add(new AWSRegion("Europe (Milan)", "eu-south-1"));
        regions.add(new AWSRegion("Europe (Paris)", "eu-west-3"));
        regions.add(new AWSRegion("Europe (Stockholm)", "eu-north-1"));
        regions.add(new AWSRegion("Middle East (Bahrain)", "me-south-1"));
        regions.add(new AWSRegion("South America (São Paulo)", "sa-east-1"));

        return regions;
    }

    private void showConnectionError(String message) {
        connectionError.setText(message);
        connectionError.setVisible(true);
        connectionError.setForeground(ERROR_COLOR);
    }

    @Override
    protected JComponent createCenterPanel() {

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(creteConnectionPanel(), "0");
        cardPanel.add(createDataSourcePanel(), "1");
        cardPanel.add(createTargetPanel(), "2");

        return cardPanel;
    }

    private JPanel creteConnectionPanel() {


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
                        <h2>DynamoDB Connection</h2>
                           <p>Select the AWS Region, the AWS profile or the key/secret to connect to your Dynamo DB</p>
                           <br>
                        Check the CLI tool for additional options <a href='https://github.com/couchbaselabs/cbmigrate'>here</a>
                        <br>
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

        JLabel regionLabel = new JLabel("AWS Region");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        mongoDBPanel.add(regionLabel, gbc);

        DefaultComboBoxModel<AWSRegion> model = new DefaultComboBoxModel<>();
        for (AWSRegion region : getRegions()) {
            model.addElement(region);
        }

        regionBox = new ComboBox<>(model);
        regionBox.setSelectedItem(null);

        gbc.gridx = 1;
        gbc.weightx = 0.95;
        gbc.gridwidth = 3;
        mongoDBPanel.add(regionBox, gbc);


        awsProfileButton = new JRadioButton("AWS Profile");
        JRadioButton accessKeyButton = new JRadioButton("Access Key/Secret Key");

        ButtonGroup group = new ButtonGroup();
        group.add(awsProfileButton);
        group.add(accessKeyButton);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(awsProfileButton);
        radioPanel.add(accessKeyButton);
        radioPanel.setBorder(JBUI.Borders.emptyTop(10));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 4;
        mongoDBPanel.add(radioPanel, gbc);

        Set<String> profiles = DynamoConnection.listProfiles();
        awsProfileButton.setSelected(!profiles.isEmpty());
        accessKeyButton.setSelected(profiles.isEmpty());

        JPanel dynamicPanel = new JPanel(new GridBagLayout());
        GridBagConstraints dynamicGbc = new GridBagConstraints();
        dynamicGbc.fill = GridBagConstraints.BOTH;
        dynamicGbc.anchor = GridBagConstraints.NORTHWEST;
        dynamicGbc.weightx = 0.05;
        dynamicGbc.weighty = 0;
        dynamicGbc.insets = new Insets(5, 5, 5, 5);

        awsProfileButton.addActionListener(e -> showAWSProfilePanel(dynamicPanel, dynamicGbc));

        accessKeyButton.addActionListener(e -> showAccessKeyPanel(dynamicPanel, dynamicGbc));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 4;
        mongoDBPanel.add(dynamicPanel, gbc);

        if (!profiles.isEmpty()) {
            showAWSProfilePanel(dynamicPanel, dynamicGbc);
        } else {
            showAccessKeyPanel(dynamicPanel, dynamicGbc);
        }


        JPanel southErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southErrorPanel.add(connectionError);

        JButton button = new JButton("Next");
        button.addActionListener(e -> {

            if (regionBox.getSelectedItem() == null) {
                showConnectionError("Select an AWS Region");
            }
            connectionError.setText("Loading tables...");
            connectionError.setForeground(ESTABILISHING_COLOR);
            connectionError.setVisible(true);

            List<String> tables = null;

            if (awsProfileButton.isSelected()) {
                if (profileBox.getSelectedItem().toString().equals(NO_PROFILES_FOUND)) {
                    showConnectionError("You must have a valid AWS profile configured to use this option");
                    return;
                } else {
                    try {
                        tables = DynamoConnection.listTables(((AWSRegion) regionBox.getSelectedItem()).getCode(), profileBox
                                .getSelectedItem().toString());
                    } catch (Exception ex) {
                        Log.error("An error occurred while trying to connect to a DynamoDB instance", ex);
                        showConnectionError("An error occurred while trying to connect to a DynamoDB instance. Check " + "the logs for more details.");
                        return;
                    }
                }
            } else {
                if (accessKeyField.getText().trim().isEmpty() || secretKeyField.getText().trim().isEmpty()) {
                    showConnectionError("Please inform the AWS Key and Secret Key");
                    return;
                } else {
                    try {
                        tables = DynamoConnection.listTables(((AWSRegion) regionBox.getSelectedItem()).getCode(), accessKeyField
                                .getText().trim(), secretKeyField.getText().trim());
                    } catch (Exception ex) {
                        Log.error("An error occurred while trying to connect to a DynamoDB instance", ex);
                        showConnectionError("An error occurred while trying to connect to a DynamoDB instance. Check " + "the logs for more details.");
                        return;
                    }
                }
            }

            if (tables == null || tables.isEmpty()) {
                showConnectionError("No DynamoDB tables found for the selected region and credentials");
                return;
            } else {
                showDataSourcePanel(tables);
            }
            connectionError.setText("");

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

    private void showAccessKeyPanel(JPanel panel, GridBagConstraints gbc) {
        panel.removeAll();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("AWS Access Key"), gbc);

        accessKeyField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(accessKeyField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("AWS Secret Key"), gbc);

        secretKeyField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(secretKeyField, gbc);

        panel.revalidate();
        panel.repaint();
    }

    private void showAWSProfilePanel(JPanel panel, GridBagConstraints gbc) {
        panel.removeAll();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Profile"), gbc);

        Set<String> profiles = DynamoConnection.listProfiles();

        if (!profiles.isEmpty()) {
            profileBox = new ComboBox<>(profiles.toArray(new String[profiles.size()]));
        } else {
            profileBox = new ComboBox<>(new String[]{NO_PROFILES_FOUND});
        }

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(profileBox, gbc);

        panel.revalidate();
        panel.repaint();
    }

    private void showDataSourcePanel(List<String> tables) {

        tablesCombobox.removeAllItems();
        tablesCombobox.addItem(ALL_TABLES_IN_THE_DATABASE);
        for (String tableName : tables) {
            tablesCombobox.addItem(tableName);
        }
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
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DynamoMigrationDialog.class));
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String content = """
                        <html>
                        <h2>Data Source Information</h2>
                        <div>
                          Select the tables that you would like to migrate from Dynamo to Couchbase
                         </div>
                        <br>
                        Check the CLI tool for additional options <a href='https://github.com/couchbaselabs/cbmigrate'>here</a>
                        <br>
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

        JLabel databaseLabel = new JLabel("Select the tables to migrate");
        gbc.gridy++;
        gbc.gridwidth = 1;
        dataSourcePanel.add(databaseLabel, gbc);

        tablesCombobox = new JComboCheckBox();
        gbc.gridx = 1;
        gbc.weightx = 0.95;
        dataSourcePanel.add(tablesCombobox, gbc);

        migrateIdxCheckBox = new JCheckBox("Migrate indexes definitions");
        migrateIdxCheckBox.setSelected(true);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dataSourcePanel.add(migrateIdxCheckBox, gbc);

        sslNoVerifyCheckbox = new JCheckBox("SSL No Verify");
        sslNoVerifyCheckbox.setSelected(false);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dataSourcePanel.add(sslNoVerifyCheckbox, gbc);

        verboseModeCheckbox = new JCheckBox("Enable verbose mode");
        verboseModeCheckbox.setSelected(false);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        dataSourcePanel.add(verboseModeCheckbox, gbc);

        JLabel errorLabel = new JLabel();
        JPanel southErrorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southErrorPanel.add(errorLabel);

        JButton button = new JButton("Next");
        button.addActionListener(e -> {
            List<String> selectedCols = tablesCombobox.getSelectedItems();
            if (selectedCols.isEmpty()) {
                errorLabel.setForeground(ERROR_COLOR);
                errorLabel.setText("Please select at least one table");
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
        infoLabel.setIcon(IconLoader.getIcon("/assets/icons/information_big.svg", DynamoMigrationDialog.class));
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
                        <br>
                        Check the CLI tool for additional options <a href='https://github.com/couchbaselabs/cbmigrate'>here</a>
                        <br>
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
                targetScopeItems.addAll(ActiveCluster.getInstance().get().bucket(selectedBucket).collections()
                                                     .getAllScopes());

                targetScopeCombo.removeAllItems();

                String[] scopes = targetScopeItems.stream().map(ScopeSpec::name).distinct().toArray(String[]::new);
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


    @Override
    protected Action @NotNull [] createActions() {
        return new Action[0];
    }

    protected void importMongoDataset() {
        try {
            String targetBucket = (String) targetBucketCombo.getSelectedItem();
            String targetScope = (String) targetScopeCombo.getSelectedItem();

            String clusterURL = ActiveCluster.getInstance().getClusterURL();
            String username = ActiveCluster.getInstance().getUsername();
            String password = ActiveCluster.getInstance().getPassword();


            List<String> selectedCols;
            if (tablesCombobox.getSelectedItems().contains(ALL_TABLES_IN_THE_DATABASE)) {
                if (awsProfileButton.isSelected()) {
                    selectedCols = DynamoConnection.listTables(((AWSRegion) regionBox.getSelectedItem()).getCode(), profileBox
                            .getSelectedItem().toString());
                } else {
                    selectedCols = DynamoConnection.listTables(((AWSRegion) regionBox.getSelectedItem()).getCode(), accessKeyField
                            .getText().trim(), secretKeyField.getText().trim());
                }
            } else {
                selectedCols = tablesCombobox.getSelectedItems();
            }

            List<CBMigrateDynamo.CBMigrateDynamoCommandBuilder> builders = new ArrayList<>();

            for (String collection : selectedCols) {
                CBMigrateDynamo.CBMigrateDynamoCommandBuilder builder = new CBMigrateDynamo.CBMigrateDynamoCommandBuilder();
                if (awsProfileButton.isSelected()) {
                    builder.setAwsProfile(profileBox.getSelectedItem().toString());
                } else {
                    builder.setAwsAccessKey(accessKeyField.getText().trim())
                           .setAwsAccessKeySecret(secretKeyField.getText().trim());
                }

                builder.setDynamoTable(collection).setCBBucket(targetBucket).setCBScope(targetScope)
                       .setAwsRegion(((AWSRegion) regionBox.getSelectedItem()).getCode()).setCBCollection(collection)
                       .setCBCluster(clusterURL).setCBUsername(username)
                       .setCopyIndexes(String.valueOf(migrateIdxCheckBox.isSelected())).setCBPassword(password)
                       .setCBGenerateKey("#UUID#")
                ;

                if (verboseModeCheckbox.isSelected()) {
                    builder.setVerbose();
                }

                if (sslNoVerifyCheckbox.isSelected()) {
                    builder.setSslNoVerify();
                }
                builders.add(builder);
            }

            CBMigrateDynamo.migrateMultipleCollections(project, builders);
            super.doOKAction();
        } catch (Exception e) {
            Log.error("Error migrating data: " + e.getMessage(), e);
            ApplicationManager.getApplication()
                              .invokeLater(() -> Messages.showErrorDialog(project, "Error migrating " + "data: " + e.getMessage(), "Error"));
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
    }

    static class AWSRegion {
        private String name;
        private String code;

        public AWSRegion(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return name + " (" + code + ")";
        }
    }
}
