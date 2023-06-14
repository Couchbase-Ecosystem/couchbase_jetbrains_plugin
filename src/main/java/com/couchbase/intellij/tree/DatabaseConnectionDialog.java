package com.couchbase.intellij.tree;


import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.persistence.ClusterAlreadyExistsException;
import com.couchbase.intellij.persistence.DuplicatedClusterNameAndUserException;
import com.couchbase.intellij.persistence.SavedCluster;
import com.couchbase.intellij.tools.doctor.SDKDoctorTableCellRenderer;
import com.couchbase.intellij.tools.doctor.SdkDoctorRunner;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseConnectionDialog extends DialogWrapper {

    private JLabel errorLabel;
    private JLabel messageLabel;

    private JPanel mainPanel;
    private JPanel thirdPanel;
    private JBTextField connectionNameTextField;
    private JBTextField hostTextField;
    private JCheckBox enableSSLCheckBox;
    private JBTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton testConnectionButton;
    private JButton saveButton;
    private JBScrollPane consoleScrollPane;

    JLabel defaultBucketLabel;
    JBTextField defaultBucketTextField;

    private JBTable eventLogTable;
    private Tree tree;

    public DatabaseConnectionDialog(Tree tree) {
        super(false);
        this.tree = tree;
        createUIComponents();

        init();
        setTitle("New Couchbase Connection");
        setResizable(true);
        getPeer().getWindow().setMinimumSize(new Dimension(600, 400));
    }

    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }

    @Override
    protected JComponent createSouthPanel() {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            close(DialogWrapper.CANCEL_EXIT_CODE);
        });
        buttonPanel.add(cancelButton);

        // Test Connection Button
        testConnectionButton = new JButton("Test Connection");
        testConnectionButton.addActionListener((ActionEvent e) -> {
            checkSSLIfCapella();
            List<String> errors = validateForm();
            if (errors.isEmpty()) {
                hideErrorLabel();
                messageLabel.setText("");
            } else {
                showErrorLabel("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
                return;
            }
            testConnectionButton.setEnabled(false);
            messageLabel.setText("Trying to Connect...");

            SwingUtilities.invokeLater(() -> {
                if (!defaultBucketTextField.getText().trim().isEmpty()) {
                    consoleScrollPane.setVisible(true);
                    thirdPanel.revalidate();
                    ((DefaultTableModel) eventLogTable.getModel()).setRowCount(0);
                    SdkDoctorRunner.run(hostTextField.getText(), enableSSLCheckBox.isSelected(),
                            defaultBucketTextField.getText(),
                            usernameTextField.getText(), String.valueOf(passwordField.getPassword()), (str) -> {
                                DefaultTableModel tableModel = (DefaultTableModel) eventLogTable.getModel();
                                tableModel.addRow(new Object[]{str});
                                eventLogTable.scrollRectToVisible(eventLogTable.getCellRect(tableModel.getRowCount() - 1, 0, true));
                            });
                }

                try {
                    if (!hasCorrectBucketConnection()) {
                        messageLabel.setText("");
                        testConnectionButton.setEnabled(true);
                        return;
                    }
                } catch (Exception ex) {
                    Log.error(ex);
                    ex.printStackTrace();
                    showErrorLabel("<html>Connection failed.<br>Please double-check your credentials"
                            + (defaultBucketTextField.getText().trim().isEmpty() ? " or inform a 'Default Bucket' and click again on 'Test Connection' to run the SDK Doctor" : "")
                            + "</html>");
                    testConnectionButton.setEnabled(true);
                    messageLabel.setText("");
                    return;
                }
                hideErrorLabel();
                messageLabel.setText("Connection was successful.");
                testConnectionButton.setEnabled(true);
            });
        });
        buttonPanel.add(testConnectionButton);

        saveButton = new JButton("Save");
        saveButton.addActionListener((ActionEvent e) -> {
            saveButton.setEnabled(false);
            checkSSLIfCapella();
            List<String> errors = validateForm();
            if (errors.isEmpty()) {
                hideErrorLabel();
                messageLabel.setText("");
            } else {
                showErrorLabel("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
                saveButton.setEnabled(true);
                return;
            }
            messageLabel.setText("Trying to Connect...");
            SwingUtilities.invokeLater(() -> {
                try {
                    if (!hasCorrectBucketConnection()) {
                        messageLabel.setText("");
                        saveButton.setEnabled(true);
                        return;
                    }
                } catch (Exception ex) {
                    Log.error(ex);
                    ex.printStackTrace();
                    showErrorLabel("<html>Could not connect to the cluster.<br>Please double-check your credentials or"
                            + (defaultBucketTextField.getText().isEmpty() ? " inform a <strong>Default Bucket</strong> and" : "")
                            + " click on <strong>Test Connection</strong> to troubleshoot the error.</html>");
                    saveButton.setEnabled(true);
                    messageLabel.setText("");
                    return;
                }

                try {
                    SavedCluster sc = DataLoader.saveDatabaseCredentials(connectionNameTextField.getText(), hostTextField.getText(), enableSSLCheckBox.isSelected(),
                            usernameTextField.getText(), String.valueOf(passwordField.getPassword()),
                            defaultBucketTextField.getText().trim().isEmpty() ? null : defaultBucketTextField.getText());
                    messageLabel.setText("Connection was successful");
                    TreeActionHandler.connectToCluster(sc, tree);
                    close(DialogWrapper.CANCEL_EXIT_CODE);
                } catch (DuplicatedClusterNameAndUserException cae) {
                    messageLabel.setText("");
                    showErrorLabel("The combination of the name of the cluster and user already exists.");
                } catch (ClusterAlreadyExistsException caee) {
                    messageLabel.setText("");
                    showErrorLabel("The Couchbase cluster URL and username already exists.");
                } catch (Exception ex) {
                    Log.error(ex);
                    ex.printStackTrace();
                    messageLabel.setText("");
                    showErrorLabel("Could not save the database credentials");
                }
                saveButton.setEnabled(true);

            });
        });

        buttonPanel.add(saveButton);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.decode("#FF4444"));
        messageLabel = new JLabel();
        messageLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        messageLabel.setForeground(JBColor.GREEN);
        notificationPanel.add(messageLabel, BorderLayout.NORTH);
        notificationPanel.add(errorLabel, BorderLayout.SOUTH);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(notificationPanel, BorderLayout.NORTH);
        wrapperPanel.add(buttonPanel, BorderLayout.CENTER);

        return wrapperPanel;
    }

    private boolean hasCorrectBucketConnection() {
        Set<String> buckets = DataLoader.listBucketNames(hostTextField.getText(), enableSSLCheckBox.isSelected(),
                usernameTextField.getText(), String.valueOf(passwordField.getPassword()));

        if (!defaultBucketTextField.getText().trim().isEmpty()) {
            if (!buckets.contains(defaultBucketTextField.getText())) {
                showErrorLabel("The bucket '" + defaultBucketTextField.getText() + "' is not present in this cluster.");
                return false;
            }
        }
        return true;
    }

    private List<String> validateForm() {
        List<String> errors = new ArrayList<>();
        cleanURL();

        if (connectionNameTextField.getText().isEmpty()) {
            errors.add("Connection Name can't be empty");
        }

        if (hostTextField.getText().isEmpty()) {
            errors.add("Couchbase Connection URL can't be empty");
        }

        if (usernameTextField.getText().isEmpty()) {
            errors.add("Username can't be empty");
        }

        if (String.valueOf(passwordField.getPassword()).isEmpty()) {
            errors.add("Password can't be empty");
        }
        return errors;
    }

    private void checkSSLIfCapella() {
        if (hostTextField.getText().contains("cloud.couchbase.com")) {
            enableSSLCheckBox.setSelected(true);
        }
    }

    private void createUIComponents() {
        // First Panel
        JPanel firstPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);

        // Add TitledBorder to the firstPanel
        TitledBorder databaseBorder = BorderFactory.createTitledBorder("Database");
        firstPanel.setBorder(databaseBorder);

        JLabel connectionNameLabel = new JLabel("Connection Name");
        connectionNameTextField = new JBTextField(20);
        connectionNameTextField.getEmptyText().setText("Give a name to your connection");
        connectionNameTextField.addFocusListener(new TextFieldFocusListener(connectionNameTextField));
        JLabel hostLabel = new JLabel("Couchbase Server URL");
        hostTextField = new JBTextField(20);
        hostTextField.getEmptyText().setText("Your cluster URL");
        hostTextField.addFocusListener(new TextFieldFocusListener(hostTextField));
        defaultBucketLabel = new JLabel("Default Bucket");
        defaultBucketTextField = new JBTextField(20);
        defaultBucketTextField.getEmptyText().setText("Optional");
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel signupLabel = new JLabel("<html><a href=\"https://cloud.couchbase.com/sign-up\">Sign up to Capella</a></html>");
        signupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://cloud.couchbase.com/sign-up"));
                } catch (Exception ex) {
                    Log.error(ex);
                    ex.printStackTrace();
                }
            }
        });
        signupPanel.add(signupLabel);

        enableSSLCheckBox = new JCheckBox("Enable SSL");

        gbc.gridx = 0;
        gbc.gridy = 0;
        firstPanel.add(connectionNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        firstPanel.add(connectionNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        firstPanel.add(hostLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        firstPanel.add(hostTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        firstPanel.add(enableSSLCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        firstPanel.add(defaultBucketLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        firstPanel.add(defaultBucketTextField, gbc);


        gbc.gridx = 2;
        gbc.gridy = 1;
        firstPanel.add(signupPanel, gbc);
        firstPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, firstPanel.getPreferredSize().height));


        // Second Panel
        JPanel secondPanel = new JPanel(new BorderLayout());
        TitledBorder credentialsBorder = BorderFactory.createTitledBorder("Credentials");
        secondPanel.setBorder(credentialsBorder);
        JPanel credentialsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcCredentials = new GridBagConstraints();
        gbcCredentials.anchor = GridBagConstraints.WEST;
        gbcCredentials.insets = new Insets(5, -20, 5, 30);

        JLabel usernameLabel = new JLabel("Username");
        usernameTextField = new JBTextField(20);
        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField(20);

        gbcCredentials.gridx = 0;
        gbcCredentials.gridy = 0;
        credentialsPanel.add(usernameLabel, gbcCredentials);

        gbcCredentials.gridx = 1;
        gbcCredentials.gridy = 0;
        credentialsPanel.add(usernameTextField, gbcCredentials);

        gbcCredentials.gridx = 0;
        gbcCredentials.gridy = 1;
        credentialsPanel.add(passwordLabel, gbcCredentials);

        gbcCredentials.gridx = 1;
        gbcCredentials.gridy = 1;
        credentialsPanel.add(passwordField, gbcCredentials);

        secondPanel.add(credentialsPanel, BorderLayout.CENTER);
        secondPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, secondPanel.getPreferredSize().height));


        // Third Panel
        thirdPanel = new JPanel(new BorderLayout());
        //consoleArea = new JBTextArea(10, 20);
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
        thirdPanel.add(consoleScrollPane, BorderLayout.CENTER);

        // Main Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(firstPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels
        topPanel.add(secondPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between panels

        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(thirdPanel, BorderLayout.CENTER);
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
        }

        if (text.startsWith("http://")) {
            hostTextField.setText(text.replaceFirst("http://", ""));
        }
    }
}
