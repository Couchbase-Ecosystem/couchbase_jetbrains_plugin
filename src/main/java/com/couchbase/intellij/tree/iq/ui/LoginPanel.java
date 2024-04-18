package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.core.IQCredentials;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoginPanel extends JPanel {

    private final Listener listener;
    private JTextField username;
    private JPasswordField password;

    public LoginPanel(IQCredentials storedCredentials, Listener loginListener) {
        super(new GridBagLayout());
        this.listener = loginListener;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(20, 20, 10, 20);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JLabel title = new JLabel("Welcome to Capella iQ");
        title.setBackground(null);
        title.setFont(new Font(
                title.getFont().getName(),
                Font.BOLD,
                (int) Math.round(title.getFont().getSize() * 1.75)
        ));
        title.setHorizontalAlignment(JLabel.CENTER);

        this.add(title, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(20, 20, 10, 20);
        gbc.gridy++;
        JTextArea brief = new JTextArea("Need a productivity boost? " +
                "Try chatting with Capella iQ, our AI cloud service. " +
                "Capella iQ is a generative AI-powered coding assistant that helps developers become more productive");
        brief.setLineWrap(true);
        brief.setWrapStyleWord(true);
        brief.setEditable(false);
        brief.setBackground(null);
        this.add(brief, gbc);

        gbc.gridy++;
        this.add(createLoginForm(storedCredentials), gbc);

        gbc.gridy++;
        this.add(createNotesPanel(), gbc);

        gbc.gridy++;
        gbc.weighty = 10;
        this.add(new JPanel(), gbc);
    }

    private JTextArea createNotesPanel() {
        JTextArea brief = new JTextArea("By default, Couchbase will collect your prompts and responses to help make Capella iQ better. You can disable this through the Settings.");
        brief.setLineWrap(true);
        brief.setWrapStyleWord(true);
        brief.setEditable(false);
        brief.setBackground(null);
        Font font = brief.getFont();
        brief.setFont(new Font(font.getName(), font.getStyle(), (int) Math.round(font.getSize() * 0.8)));
        return brief;
    }

    private JPanel createLoginForm(IQCredentials storedCredentials) {
        JPanel loginForm = new JPanel(new GridBagLayout());
        loginForm.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = JBUI.insets(20, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel signinToCapella = new JLabel("Sign in to Capella");
        signinToCapella.setFont(new Font(
                signinToCapella.getFont().getName(),
                Font.BOLD,
                (int) Math.round(signinToCapella.getFont().getSize() * 1.5)
        ));
        signinToCapella.setHorizontalAlignment(JLabel.CENTER);
        loginForm.add(signinToCapella, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        gbc.insets = JBUI.insets(10, 20, 5, 10);
        JLabel usernameLabel = new JLabel("Username");
        loginForm.add(usernameLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 2;
        gbc.insets = JBUI.insets(10, 10, 5, 20);
        username = new JTextField();
        loginForm.add(username, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = JBUI.insets(10, 20, 5, 10);
        JLabel passwordLabel = new JLabel("Password");
        loginForm.add(passwordLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 2;
        gbc.insets = JBUI.insets(10, 10, 5, 20);
        password = new JPasswordField();
        loginForm.add(password, gbc);

        if (storedCredentials != null) {
            username.setText(storedCredentials.getLogin());
            password.setText(storedCredentials.getPassword());
        }

        gbc.gridy++;
        JCheckBox showPassword = new JCheckBox("Show Password");
        loginForm.add(showPassword, gbc);

        showPassword.addActionListener(actionEvent -> {
            if (showPassword.isSelected()) {
                this.password.setEchoChar((char) 0);
            } else {
                this.password.setEchoChar('*');
            }
        });
        gbc.gridy++;
        JCheckBox saveLogin = new JCheckBox("Remember me");
        loginForm.add(saveLogin, gbc);

        JLabel invalidLogin = new JLabel("Login failed.");

        gbc.insets = JBUI.insets(10, 20, 10, 20);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Sign in");
        loginForm.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                invalidLogin.setVisible(false);
                loginButton.setEnabled(false);
                loginButton.setText("Signing in...");
                updateUI();
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!doLogin(username.getText(), new String(password.getPassword()), saveLogin.isSelected())) {
                        loginButton.setText("Sign in");
                        loginButton.setEnabled(true);
                        invalidLogin.setVisible(true);
                        updateUI();
                    }
                });
            });
        });

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = JBUI.insets(5, 25, 5, 20);
        gbc.gridwidth = 2;
        invalidLogin.setForeground(Color.RED);
        invalidLogin.setVisible(false);
        invalidLogin.setHorizontalAlignment(JLabel.CENTER);
        loginForm.add(invalidLogin, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = JBUI.insets(5, 10, 20, 20);
        JEditorPane signupLink = new JEditorPane("text/html", "<a href='https://cloud.couchbase.com/sign-up'>Don't have an account yet?</a>");
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
        if (credentials.doLogin()) {
            if (store) {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    credentials.store();
                }, "Storing Capella credentials...", false, null);
            } else {
                this.username.setText("");
                this.password.setText("");
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    credentials.unstore();
                }, "Removing Capella credentials...", false, null);
            }
            listener.onLogin(credentials);
            return true;
        } else {
            credentials.unstore();
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
