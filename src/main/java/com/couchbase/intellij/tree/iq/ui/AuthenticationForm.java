package com.couchbase.intellij.tree.iq.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;

public class AuthenticationForm {
    private JPanel mainPanel;
    private JBTextField usernameField;
    private JBPasswordField passwordField;
    private JButton signInButton;
    private JBCheckBox rememberMeCheckBox;

    public AuthenticationForm() {
        // Initialize components
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(300, 200));

        JBLabel titleLabel = new JBLabel("Sign in to Capella");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));

        usernameField = new JBTextField();
        usernameField.getEmptyText().setText("Username");

        passwordField = new JBPasswordField();
        passwordField.getEmptyText().setText("Password");

        signInButton = new JButton("Sign in");
        rememberMeCheckBox = new JBCheckBox("Remember me");

        // Using GridBagConstraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        mainPanel.add(titleLabel, gbc);
        mainPanel.add(usernameField, gbc);
        mainPanel.add(passwordField, gbc);
        mainPanel.add(rememberMeCheckBox, gbc);
        mainPanel.add(signInButton, gbc);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
