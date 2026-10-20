package com.couchbase.intellij.tree;


import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.ClusterAlreadyExistsException;
import com.couchbase.intellij.persistence.Clusters;
import com.couchbase.intellij.persistence.DuplicatedClusterNameAndUserException;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tools.dialog.CollapsiblePanel;
import com.couchbase.intellij.tools.doctor.SDKDoctorTableCellRenderer;
import com.couchbase.intellij.tools.doctor.SdkDoctorRunner;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.couchbase.intellij.tree.overview.NewConnectionBanner;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import utils.TemplateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewConnectionDialog extends DialogWrapper {

    private final Tree tree;
    private final SavedCluster savedCluster;
    JBTextField defaultBucketTextField;
    private JLabel errorLabel;
    private JLabel messageLabel;
    private JPanel thirdPanel;
    private JBTextField connectionNameTextField;
    private JBTextField hostTextField;
    private JCheckBox enableSSLCheckBox;
    private JCheckBox ldapAuthCheckbox;
    private JBTextField usernameTextField;
    private JPasswordField passwordField;
    private ComboBox<String> authenticationType;
    private JPanel credentialCards;
    private TextFieldWithBrowseButton keyStoreField;
    private TextFieldWithBrowseButton serverCaField;
    private JPasswordField keyStorePasswordField;
    private JPanel ldapPanel;
    private JButton testConnectionButton;
    private JButton saveButton;
    private JBScrollPane consoleScrollPane;
    private JBTable eventLogTable;
    private Project project;
    private JBTextField apiKeyField;
    private JPasswordField apiSecretField;
    private JPanel wrapperPanel;
    private JCheckBox liveReload;

    private DefaultMutableTreeNode clickedNode;
    private Set<String> options;

    public NewConnectionDialog(Project project, Tree tree, SavedCluster savedCluster, DefaultMutableTreeNode clickedNode) {
        super(false);
        this.tree = tree;
        this.project = project;
        this.savedCluster = savedCluster;
        if (savedCluster != null) {
            options = savedCluster.options().collect(Collectors.toSet());
        } else {
            options = Clusters.Options.defaults();
        }
        this.clickedNode = clickedNode;
        createUIComponents();

        init();
        setTitle("New Couchbase Connection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(800, 600));
    }

    public static String getQueryParams(String url) {
        int index = url.indexOf('?');

        if (index == -1) {
            return "";
        }
        return url.substring(index);
    }

    public static String getBaseUrl(String url) {
        int index = url.indexOf('?');

        if (index == -1) {
            return url;
        }

        return url.substring(0, index);
    }

    @Override
    protected JComponent createCenterPanel() {
        return wrapperPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    @Override
    protected JComponent createSouthPanel() {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> close(DialogWrapper.CANCEL_EXIT_CODE));
        buttonPanel.add(cancelButton);

        // Test Connection Button
        testConnectionButton = new JButton("Test Connection");
        testConnectionButton.addActionListener((ActionEvent e) -> {
            handleTestConnection();
        });
        buttonPanel.add(testConnectionButton);

        saveButton = new JButton("Save");
        saveButton.addActionListener((ActionEvent e) -> {
            handleSaveConnection();
        });

        buttonPanel.add(saveButton);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        errorLabel = new JLabel();
        errorLabel.setName("connectionError");
        errorLabel.setForeground(com.intellij.util.ui.UIUtil.getErrorForeground());
        messageLabel = new JLabel();
        messageLabel.setName("connectionStatus");
        messageLabel.setBorder(JBUI.Borders.emptyTop(5));
        notificationPanel.add(messageLabel, BorderLayout.NORTH);
        notificationPanel.add(errorLabel, BorderLayout.SOUTH);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(notificationPanel, BorderLayout.NORTH);
        wrapperPanel.add(buttonPanel, BorderLayout.CENTER);

        return wrapperPanel;
    }

    private void handleSaveConnection() { runConnectionCheck(true); }

    private void handleTestConnection() { runConnectionCheck(false); }

    private boolean certificateAuthentication() {
        return authenticationType.getSelectedIndex() == 1;
    }

    private SavedCluster connectionSettings() {
        SavedCluster settings = new SavedCluster();
        settings.setName(connectionNameTextField.getText().trim());
        settings.setUrl(DataLoader.adjustClusterProtocol(getBaseUrl(hostTextField.getText().trim()), enableSSLCheckBox.isSelected()));
        settings.setQueryParams(getQueryParams(hostTextField.getText().trim()));
        settings.setSslEnable(enableSSLCheckBox.isSelected());
        settings.setAuthenticationType(certificateAuthentication() ? "certificate" : "password");
        settings.setUsername(certificateAuthentication() ? "" : usernameTextField.getText().trim());
        settings.setLDAP(!certificateAuthentication() && ldapAuthCheckbox.isSelected());
        settings.setClientKeyStorePath(keyStoreField.getText().trim());
        settings.setServerCaCertificatePath(serverCaField.getText().trim());
        settings.setDefaultBucket(defaultBucketTextField.getText().trim().isEmpty() ? null : defaultBucketTextField.getText().trim());
        settings.options(options);
        if (savedCluster != null) {
            settings.setReadOnly(savedCluster.getReadOnly());
            settings.setColor(savedCluster.getColor());
            settings.setInferCachePeriod(savedCluster.getInferCachePeriod());
            settings.setQueryPreferences(savedCluster.getQueryPreferences());
        }
        return settings;
    }

    private void runConnectionCheck(boolean save) {
        checkSSL();
        List<String> errors = validateForm();
        if (!errors.isEmpty()) {
            showErrorLabel("<html>" + String.join("<br>", errors) + "</html>");
            return;
        }
        hideErrorLabel();
        SavedCluster settings = connectionSettings();
        String secret = new String(certificateAuthentication() ? keyStorePasswordField.getPassword() : passwordField.getPassword());
        saveButton.setEnabled(false);
        testConnectionButton.setEnabled(false);
        messageLabel.setText("Trying to Connect...");
        var modality = com.intellij.openapi.application.ModalityState.current();
        boolean runDoctor = !save && !settings.usesCertificateAuthentication() && settings.getDefaultBucket() != null;
        if (runDoctor) {
            consoleScrollPane.setVisible(true);
            thirdPanel.revalidate();
            ((DefaultTableModel) eventLogTable.getModel()).setRowCount(0);
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                if (runDoctor) {
                    SdkDoctorRunner.run(settings.getUrl(), settings.isSslEnable(), settings.getDefaultBucket(), settings.getUsername(), secret,
                            line -> ApplicationManager.getApplication().invokeLater(() -> {
                                if (!isDisposed()) ((DefaultTableModel) eventLogTable.getModel()).addRow(new Object[]{line});
                            }, modality));
                }
                Set<String> buckets = DataLoader.listBucketNames(settings, secret);
                if (settings.getDefaultBucket() != null && !buckets.contains(settings.getDefaultBucket())) {
                    throw new IllegalArgumentException("The bucket '" + settings.getDefaultBucket() + "' is not present in this cluster.");
                }
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (isDisposed()) return;
                    try {
                        if (save) {
                            SavedCluster sc = DataLoader.saveDatabaseCredentials(settings, secret, savedCluster);
                            if (clickedNode != null) clickedNode.setUserObject(new ConnectionNodeDescriptor(sc.getId(), sc, false));
                            TreeActionHandler.connectToCluster(project, sc, tree, null);
                            close(DialogWrapper.OK_EXIT_CODE);
                        } else {
                            messageLabel.setText("Connection was successful.");
                        }
                    } catch (DuplicatedClusterNameAndUserException ex) {
                        showErrorLabel("A connection with this name and authentication identity already exists.");
                        messageLabel.setText("");
                    } catch (ClusterAlreadyExistsException ex) {
                        showErrorLabel("This cluster and authentication identity already exist.");
                        messageLabel.setText("");
                    } finally {
                        saveButton.setEnabled(true);
                        testConnectionButton.setEnabled(true);
                    }
                }, modality);
            } catch (Exception ex) {
                Log.error("Connection test failed", ex);
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (isDisposed()) return;
                    messageLabel.setText("");
                    String detail = ex.getMessage();
                    showErrorLabel("<html>Connection failed. " + com.intellij.openapi.util.text.StringUtil.escapeXmlEntities(
                            detail == null ? "Check your connection and authentication settings." : detail) + "</html>");
                    saveButton.setEnabled(true);
                    testConnectionButton.setEnabled(true);
                }, modality);
            }
        });
    }

    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();
        cleanURL();

        if (connectionNameTextField.getText().trim().isEmpty()) {
            errors.add("Name can't be empty");
        }

        if (hostTextField.getText().trim().isEmpty()) {
            errors.add("Connection String can't be empty");
        }

        if (certificateAuthentication()) {
            validateFile(keyStoreField.getText(), "Client keystore", true, errors);
            validateFile(serverCaField.getText(), "Server CA certificate", false, errors);
        } else {
            if (usernameTextField.getText().trim().isEmpty()) errors.add("Username can't be empty");
            if (passwordField.getPassword().length == 0) errors.add("Password can't be empty");
        }
        return errors;
    }

    private static void validateFile(String value, String label, boolean required, List<String> errors) {
        if (value.isBlank()) {
            if (required) errors.add(label + " can't be empty");
            return;
        }
        try {
            if (!Files.isRegularFile(Path.of(value)) || !Files.isReadable(Path.of(value))) errors.add(label + " must be a readable file");
        } catch (java.nio.file.InvalidPathException ex) {
            errors.add(label + " must be a valid file path");
        }
    }

    private void checkSSL() {
        if (certificateAuthentication() || hostTextField.getText().contains("cloud.couchbase.com")
                || hostTextField.getText().startsWith("https://")
                || hostTextField.getText().startsWith("couchbases://")) {
            enableSSLCheckBox.setSelected(true);
        }
    }

    private void createUIComponents() {
        JPanel databasePanel = createDatabasePanel();
        JPanel credentialsPanel = createCredentialsPanel();
        CollapsiblePanel advancedPanel = createAdvancedPanel();

        // Main Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(databasePanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels
        topPanel.add(credentialsPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(advancedPanel, BorderLayout.CENTER);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(10));

        // Setting preferred size for leftPanel
        JPanel leftPanel = createCouchbaseBanner();
        leftPanel.setPreferredSize(new Dimension(280, leftPanel.getPreferredSize().height));
        leftPanel.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));  // this line ensures the panel doesn't grow more than 180px in width

        JPanel leftWrapper = new JPanel();
        leftWrapper.setLayout(new BoxLayout(leftWrapper, BoxLayout.PAGE_AXIS));
        leftWrapper.add(leftPanel);

        wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(leftWrapper, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(mainPanel, BorderLayout.CENTER);

        wrapperPanel.add(rightPanel, BorderLayout.CENTER);
    }

    @NotNull
    private CollapsiblePanel createAdvancedPanel() {
        JPanel advancedPanel = new JPanel(new BorderLayout());
        advancedPanel.add(getAdvancedTabs(), BorderLayout.CENTER);
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Advanced Settings", advancedPanel);
        collapsiblePanel.setBorder(JBUI.Borders.emptyTop(10));
        return collapsiblePanel;
    }

    @NotNull
    private JPanel createCredentialsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(new TitledSeparator("Credentials"), BorderLayout.NORTH);
        JPanel contents = new JPanel(new BorderLayout(0, 6));
        authenticationType = new ComboBox<>(new String[]{"Username and password", "Client certificate"});
        authenticationType.setName("connectionAuthentication");
        JPanel selector = new JPanel(new GridBagLayout());
        addCredentialRow(selector, 0, "Authentication", authenticationType);
        contents.add(selector, BorderLayout.NORTH);
        credentialCards = new JPanel(new CardLayout());

        JPanel passwordCard = new JPanel(new GridBagLayout());
        usernameTextField = new JBTextField(25);
        usernameTextField.setName("connectionUsername");
        passwordField = new JPasswordField(25);
        passwordField.setName("connectionPassword");
        addCredentialRow(passwordCard, 0, "Username", usernameTextField);
        addCredentialRow(passwordCard, 1, "Password", passwordField);
        JPanel passwordWrapper = new JPanel(new BorderLayout());
        passwordWrapper.add(passwordCard, BorderLayout.NORTH);
        credentialCards.add(passwordWrapper, "password");

        JPanel certificateCard = new JPanel(new GridBagLayout());
        keyStoreField = certificateFileField("connectionKeyStore", "Choose client keystore", "PKCS#12 (.p12, .pfx) or Java keystore (.jks) containing your client certificate and private key.");
        keyStorePasswordField = new JPasswordField(25);
        keyStorePasswordField.setName("connectionKeyStorePassword");
        keyStorePasswordField.setToolTipText("Keystore password. Leave empty for a keystore without a password. Stored in the IDE password safe.");
        serverCaField = certificateFileField("connectionServerCa", "Choose server CA certificate", "Optional PEM file containing trusted server CA certificates. Leave empty to use default trusted authorities.");
        addCredentialRow(certificateCard, 0, "Client keystore", keyStoreField);
        addCredentialRow(certificateCard, 1, "Keystore password", keyStorePasswordField);
        addCredentialRow(certificateCard, 2, "Server CA (optional)", serverCaField);
        JBLabel help = new JBLabel("<html>The server identifies your user from the certificate.<br>A secure TLS connection is enabled automatically.</html>");
        help.setForeground(com.intellij.util.ui.UIUtil.getContextHelpForeground());
        GridBagConstraints hint = new GridBagConstraints();
        hint.gridx = 0; hint.gridy = 3; hint.gridwidth = 2; hint.weightx = 1;
        hint.fill = GridBagConstraints.HORIZONTAL; hint.insets = JBUI.insets(4, 5);
        certificateCard.add(help, hint);
        credentialCards.add(certificateCard, "certificate");
        contents.add(credentialCards, BorderLayout.CENTER);
        panel.add(contents, BorderLayout.CENTER);
        authenticationType.addActionListener(e -> updateAuthenticationFields());
        if (savedCluster != null) {
            usernameTextField.setText(savedCluster.getUsername());
            if (savedCluster.usesCertificateAuthentication()) {
                keyStoreField.setText(java.util.Objects.toString(savedCluster.getClientKeyStorePath(), ""));
                serverCaField.setText(java.util.Objects.toString(savedCluster.getServerCaCertificatePath(), ""));
                keyStorePasswordField.setText(DataLoader.getClusterPassword(savedCluster));
                authenticationType.setSelectedIndex(1);
            } else {
                passwordField.setText(DataLoader.getClusterPassword(savedCluster));
            }
        }
        updateAuthenticationFields();
        return panel;
    }

    private TextFieldWithBrowseButton certificateFileField(String name, String title, String description) {
        TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();
        field.getTextField().setName(name);
        field.getButton().setText("…");
        field.getButton().getAccessibleContext().setAccessibleName(title);
        field.getTextField().setToolTipText(description);
        field.addBrowseFolderListener(project, FileChooserDescriptorFactory.createSingleFileDescriptor().withTitle(title).withDescription(description));
        return field;
    }

    private static void addCredentialRow(JPanel panel, int row, String text, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5); gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(JBUI.scale(125), label.getPreferredSize().height));
        label.setLabelFor(field instanceof TextFieldWithBrowseButton browse ? browse.getTextField() : field);
        panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void updateAuthenticationFields() {
        boolean certificate = certificateAuthentication();
        ((CardLayout) credentialCards.getLayout()).show(credentialCards, certificate ? "certificate" : "password");
        enableSSLCheckBox.setEnabled(!certificate);
        if (certificate) enableSSLCheckBox.setSelected(true);
        ldapPanel.setVisible(!certificate);
        credentialCards.revalidate();
        credentialCards.repaint();
    }

    @NotNull
    private JPanel createDatabasePanel() {
        JPanel firstPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);

        // Add TitledBorder to the firstPanel
        TitledSeparator separator = new TitledSeparator("Database");

        JLabel connectionNameLabel = new JLabel("Name:");
        connectionNameTextField = new JBTextField(30);
        connectionNameTextField.setName("connectionName");
        connectionNameLabel.setLabelFor(connectionNameTextField);
        connectionNameTextField.getEmptyText().setText("Give a name to your connection");
        JLabel hostLabel = new JLabel("Connection String:");
        hostTextField = new JBTextField(30);
        hostTextField.setName("connectionHost");
        hostLabel.setLabelFor(hostTextField);
        hostTextField.getEmptyText().setText("Your cluster URL");
        enableSSLCheckBox = new JCheckBox("Enable SSL");
        enableSSLCheckBox.setName("connectionSsl");
        ldapAuthCheckbox = new JCheckBox("LDAP Auth");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        firstPanel.add(separator, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        firstPanel.add(connectionNameLabel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        firstPanel.add(TemplateUtil.createComponentWithBalloon(connectionNameTextField, "A simple name to your connection so you can quickly identify it."), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        firstPanel.add(hostLabel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        firstPanel.add(TemplateUtil.createComponentWithBalloon(hostTextField, "The Connection String is the address of your server. If your cluster is running locally, it could be something like 'couchbase://localhost'. If you are using Couchbase Capella, it should look like 'couchbases://cb.fwu-odewcpjq7f.cloud.couchbase.com'. Do not include any port numbers"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.weightx = 0.7;

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.add(TemplateUtil.createComponentWithBalloon(enableSSLCheckBox, "Check this if TLS is enabled in your cluster. If you specify 'couchbases://' protocol in your Connection String, this option will be checked automatically"));
        ldapPanel = TemplateUtil.createComponentWithBalloon(ldapAuthCheckbox, "Check this if your authentication uses LDAP");
        ldapPanel.setBorder(JBUI.Borders.emptyLeft(10));
        checkboxPanel.add(ldapPanel);
        firstPanel.add(checkboxPanel, gbc);
        if (this.savedCluster != null) {
            connectionNameTextField.setText(this.savedCluster.getName());
            hostTextField.setText(this.savedCluster.getUrl());
            enableSSLCheckBox.setSelected(this.savedCluster.isSslEnable());
            ldapAuthCheckbox.setSelected(this.savedCluster.getLDAP());
            cleanURL();
            hostTextField.setText(hostTextField.getText() + (savedCluster.getQueryParams() != null ? savedCluster.getQueryParams() : ""));
        }

        return firstPanel;
    }

    private Component getAdvancedTabs() {
        JBTabbedPane tabbedPane = new JBTabbedPane();

        // Add "Management API Keys" tab
        JPanel managementAPIKeysPanel = new JPanel(new BorderLayout());
        managementAPIKeysPanel.setBorder(JBUI.Borders.empty(10));

        JBLabel disclaimer = new JBLabel("<html><strong>Couchbase Capella Only:</strong> Inform these values if you also want to perform administrative tasks via the plugin. You can generate these credentials under <small>Your Org -> Settings -> API Keys</small></html>");
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setPreferredSize(new Dimension(300, 50));
        textPanel.add(disclaimer, BorderLayout.CENTER);

        JPanel mgmFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = JBUI.insets(5);

        JLabel apiKeyLabel = new JLabel("API Key:");
        apiKeyField = new JBTextField(30);
        JLabel apiSecretLabel = new JLabel("API Secret:");
        apiSecretField = new JPasswordField(30);

        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = 2;
        mgmFormPanel.add(textPanel, c);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0.4;
        mgmFormPanel.add(apiKeyLabel, c);

        c.gridy = 1;
        c.gridx = 1;
        c.weightx = 0.6;
        mgmFormPanel.add(TemplateUtil.createComponentWithBalloon(apiKeyField, "Leave it blank if you don't want to inform your management keys"), c);

        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 0.4;
        mgmFormPanel.add(apiSecretLabel, c);

        c.gridy = 2;
        c.gridx = 1;
        c.weightx = 0.6;
        mgmFormPanel.add(TemplateUtil.createComponentWithBalloon(apiSecretField, "Leave it blank if you don't want to inform your management keys"), c);
        managementAPIKeysPanel.add(mgmFormPanel, BorderLayout.NORTH);

        /* *************** Troubleshooting Tab **************************/
        JPanel topThirdPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JBLabel tbLabel = new JBLabel("<html>If you can't successfully connect to your cluster, try informing an existing bucket in the field bellow and click on <strong>Test Connection</strong>. It will run the <strong>SDK Doctor</strong>, an utility that tries to identify potential issues</html>");
        tbLabel.setBorder(JBUI.Borders.empty(10, 0));
        JPanel tbWrapPanel = new JPanel(new BorderLayout());
        tbWrapPanel.setPreferredSize(new Dimension(400, 80));
        tbWrapPanel.add(tbLabel, BorderLayout.CENTER);


        JPanel troubleForm = new JPanel(new GridBagLayout());
        JLabel dfBucket = new JLabel("Bucket:");
        defaultBucketTextField = new JBTextField(30);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        troubleForm.add(dfBucket, gbc);

        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        troubleForm.add(TemplateUtil.createComponentWithBalloon(defaultBucketTextField, "Inform any existing bucket in you cluster that your credentials have access to."), gbc);

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Set the column class to String
            }
        };
        tableModel.addColumn("Output");
        eventLogTable = new JBTable(tableModel);
        eventLogTable.setDefaultRenderer(Object.class, new SDKDoctorTableCellRenderer());

        consoleScrollPane = new JBScrollPane(eventLogTable);
        consoleScrollPane.setMinimumSize(new Dimension(800, 300));
        consoleScrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScrollPane.setVisible(false);
        consoleScrollPane.setBorder(JBUI.Borders.emptyTop(10));


        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        topThirdPanel.add(tbWrapPanel, gbc);
        gbc.gridy = 1;
        gbc.weightx = 1;
        topThirdPanel.add(troubleForm, gbc);
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        topThirdPanel.add(consoleScrollPane, gbc);


        //TODO: Enable this when the new Capella Rest API is live
        //tabbedPane.addTab("Management API Keys", managementAPIKeysPanel);
        // Add "Connection Troubleshooting" tab
        thirdPanel = new JBPanel(new BorderLayout());
        thirdPanel.setBorder(JBUI.Borders.empty(10));
        thirdPanel.add(topThirdPanel, BorderLayout.NORTH);
        thirdPanel.add(consoleScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Connection", createAdvancedConnectionPanel());
        tabbedPane.addTab("Troubleshooting", thirdPanel);
        tabbedPane.setBorder(JBUI.Borders.emptyTop(10));

        return tabbedPane;
    }

    private Component createAdvancedConnectionPanel() {
        JBScrollPane sp = new JBScrollPane();
        JBPanel panel = new JBPanel(new GridBagLayout());

        JBCheckBox liveEditing = new JBCheckBox("Keep opened documents synchronized");
        liveEditing.setToolTipText("Requires document polling to be enabled. If selected, changes to Couchbase documents opened in IDE will be " +
                                           "auto-saved onto the cluster and vice-versa.\n" +
                                           "If not selected, then editing a document that got changed " +
                                           "on the cluster will trigger an alert.");
        if (options.contains("live_polling")) {
            liveEditing.setSelected(options.contains("live_reload"));
        } else {
            liveEditing.setEnabled(false);
        }
        liveEditing.addChangeListener(e -> {
            if (liveEditing.isSelected()) {
                options.add("live_reload");
            } else {
                options.remove("live_reload");
            }
        });

        JBCheckBox documentPolling = new JBCheckBox("Poll cluster for document changes");
        documentPolling.setToolTipText("If selected, the plugin will periodically poll the cluster for changes in opened documents.");
        documentPolling.setSelected(options.contains("live_polling"));
        documentPolling.addChangeListener(e -> {
            if (documentPolling.isSelected()) {
                options.add("live_polling");
            } else {
                options.remove("live_polling");
                options.remove("live_reload");
                liveEditing.setSelected(false);
            }
            liveEditing.setEnabled(documentPolling.isSelected());
        });


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(documentPolling, gbc);
        gbc.gridx++;
        panel.add(liveEditing, gbc);
        JPanel tbWrapPanel = new JPanel(new BorderLayout());
        tbWrapPanel.setPreferredSize(new Dimension(400, 80));
        tbWrapPanel.add(panel, BorderLayout.NORTH);
        sp.setViewportView(tbWrapPanel);
        return sp;
    }

    private JPanel createCouchbaseBanner() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Image Icon
        Icon icon = IconLoader.getIcon("/assets/icons/couchbase-logo.svg", NewConnectionDialog.class);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(JBUI.Borders.empty(35, 0));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JEditorPane label2 = new JEditorPane("text/html",
                NewConnectionBanner.getBannerContent());
        label2.setEditable(false);
        label2.setOpaque(false);
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
        label2.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        // Open the URL based on the link that was clicked
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException ex) {
                        Log.error(ex);
                    }
                }
            }
        });

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        leftPanel.add(label2);
        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    private void showErrorLabel(String errorMessage) {
        errorLabel.setText(errorMessage);
    }

    private void hideErrorLabel() {
        errorLabel.setText("");
    }

    private void cleanURL() {
        String text = hostTextField.getText();
        if (text.endsWith("/")) {
            hostTextField.setText(text.substring(0, text.length() - 1));
        }

        if (text.startsWith("https://")) {
            hostTextField.setText(text.replaceFirst("https://", ""));
            enableSSLCheckBox.setSelected(true);
        } else if (text.startsWith("http://")) {
            hostTextField.setText(text.replaceFirst("http://", ""));
        } else if (text.startsWith("couchbase://")) {
            hostTextField.setText(text.replaceFirst("couchbase://", ""));
        } else if (text.startsWith("couchbases://")) {
            hostTextField.setText(text.replaceFirst("couchbases://", ""));
            enableSSLCheckBox.setSelected(true);
        }

    }
}
