package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.intellij.util.ui.JBUI;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoginPanel extends JPanel {

    private final Listener listener;

    public LoginPanel(Listener loginListener) {
        super(new GridBagLayout());
        this.listener = loginListener;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(10);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JTextArea title = new JTextArea("Welcome to Couchbase IQ");
        title.setLineWrap(true);
        title.setWrapStyleWord(true);
        title.setEditable(false);
        title.setBackground(null);
        title.setFont(new Font(
                title.getFont().getName(),
                Font.BOLD,
                title.getFont().getSize() * 2
        ));

        this.add(title, gbc);

        gbc.gridy++;
        JTextArea brief = new JTextArea("Need a productivity boost? " +
                "Try chatting with Capella IQ, our AI cloud service. " +
                "Capella IQ is a generative AI-powered coding assistant that helps developers become more productive");
        brief.setLineWrap(true);
        brief.setWrapStyleWord(true);
        brief.setEditable(false);
        brief.setBackground(null);
        this.add(brief, gbc);

        gbc.gridy++;
        this.add(createLoginForm(), gbc);

        gbc.gridy++;
        gbc.weighty = 10;
        this.add(new JPanel(), gbc);
    }

    private JPanel createLoginForm() {
        JPanel loginForm = new JPanel(new GridBagLayout());
        loginForm.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username");
        loginForm.add(usernameLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 2;
        JTextField username = new JTextField();
        loginForm.add(username, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password");
        loginForm.add(passwordLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 2;
        JPasswordField password = new JPasswordField();
        loginForm.add(password, gbc);

        gbc.gridy++;
        JCheckBox saveLogin = new JCheckBox("Remember me");
        loginForm.add(saveLogin, gbc);

        JLabel invalidLogin = new JLabel("Login failed.");

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Sign in");
        loginForm.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            invalidLogin.setVisible(false);
            loginButton.setEnabled(false);
            loginButton.setText("Signing in...");
            updateUI();
            SwingUtilities.invokeLater(() -> {
                if (!doLogin(username.getText(), new String(password.getPassword()), saveLogin.isSelected())) {
                    loginButton.setText("Sign in");
                    loginButton.setEnabled(true);
                    invalidLogin.setVisible(true);
                    updateUI();
                }
            });
        });

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        invalidLogin.setForeground(Color.RED);
        invalidLogin.setVisible(false);
        loginForm.add(invalidLogin, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        JEditorPane signupLink = new JEditorPane("text/html","<a href='https://cloud.couchbase.com/sign-up'>Don't have an account yet?</a>");
        signupLink.setEditable(false);
        signupLink.setBackground(null);
        signupLink.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        loginForm.add(signupLink, gbc);

        return loginForm;
    }

    private boolean doLogin(String login, String password, boolean store) {
        IQCredentials credentials = new IQCredentials(login, password);
        if (credentials.checkAuthStatus()) {
            if (store) {
                credentials.store();
            }
            listener.onLogin(credentials);
            return true;
        }
        return false;
    }

    public interface Listener {
        void onLogin(IQCredentials credentials);
    }

    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }
}
