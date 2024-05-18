package com.couchbase.intellij.config;

import javax.swing.*;
import java.awt.*;

public class CoucbaseSettingsComponent {

    private JPanel panel;
    private JPasswordField openApiKeyField;
    private JPasswordField googleGeminiKeyField;

    public CoucbaseSettingsComponent() {
        panel = new JPanel(new BorderLayout());

        JPanel apiPanel = new JPanel(new GridLayout(2, 2));
        apiPanel.add(new JLabel("Open API Key:"));
        openApiKeyField = new JPasswordField();
        Dimension preferredSize = new Dimension(200, openApiKeyField.getPreferredSize().height);
        openApiKeyField.setPreferredSize(preferredSize);
        apiPanel.add(openApiKeyField);

        apiPanel.add(new JLabel("Google Gemini Key:"));
        googleGeminiKeyField = new JPasswordField();
        googleGeminiKeyField.setPreferredSize(preferredSize);
        apiPanel.add(googleGeminiKeyField);

        panel.add(apiPanel, BorderLayout.NORTH);
        reset();
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean isModified() {
        CouchbaseSettingsStorage settings = CouchbaseSettingsStorage.getInstance();
        return !String.valueOf(openApiKeyField.getPassword()).equals(settings.getState().getOpenApiKey()) ||
                !String.valueOf(googleGeminiKeyField.getPassword()).equals(settings.getState().getGoogleGeminiKey());
    }

    public void apply() {
        CouchbaseSettingsStorage settings = CouchbaseSettingsStorage.getInstance();
        settings.getState().setOpenApiKey(String.valueOf(openApiKeyField.getPassword()));
        settings.getState().setGoogleGeminiKey(String.valueOf(googleGeminiKeyField.getPassword()));
    }

    public void reset() {
        CouchbaseSettingsStorage settings = CouchbaseSettingsStorage.getInstance();
        openApiKeyField.setText(settings.getState().getOpenApiKey());
        googleGeminiKeyField.setText(settings.getState().getGoogleGeminiKey());
    }
}