package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.CapellaApiMethods;
import com.couchbase.intellij.tree.iq.CapellaOrganization;
import com.couchbase.intellij.tree.iq.CapellaOrganizationList;
import com.couchbase.intellij.tree.iq.core.CapellaAuth;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class OrgSelectionPanel extends JPanel {
    private Project project;
    private CapellaAuth auth;
    private Listener listener;
    public OrgSelectionPanel(Project project, CapellaAuth auth, Listener listener) throws IOException {
        super(new GridBagLayout());
        this.project = project;
        this.auth = auth;
        this.listener = listener;

        CapellaOrganizationList organizationList = CapellaApiMethods.loadOrganizations(auth);
        if (organizationList.getData().size() == 1) {
            listener.onOrgSelected(organizationList.getData().get(0).getData());
            return;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(10);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel orgLabel = new JLabel("Select Organization: ");
        add(orgLabel, gbc);

        gbc.gridx++;
        JComboBox<String> orgComboBox = new ComboBox<>(organizationList.getData().stream()
                .map(CapellaOrganizationList.Entry::getData)
                .map(CapellaOrganization::getName)
                .toArray(String[]::new)
        );
        add(orgComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            listener.onOrgSelected(organizationList.getData().get(orgComboBox.getSelectedIndex()).getData());
        });

        add(selectButton, gbc);
    }

    public interface Listener {
        void onOrgSelected(CapellaOrganization organization);
    }
}
