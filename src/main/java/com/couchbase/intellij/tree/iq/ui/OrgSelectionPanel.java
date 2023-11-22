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
    public OrgSelectionPanel(Project project, CapellaOrganizationList organizationList, Listener listener) throws IOException {
        super(new GridBagLayout());
        this.project = project;
        this.auth = auth;
        this.listener = listener;

        if (organizationList.getData().size() == 1) {
            SwingUtilities.invokeLater(() -> {
                listener.onOrgSelected(organizationList.getData().get(0).getData());
            });
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel orgLabel = new JLabel("Select Organization: ");
        add(orgLabel, gbc);

        gbc.gridx++;
        gbc.gridwidth = 2;
        JComboBox<String> orgComboBox = new ComboBox<>(organizationList.getNames());
        add(orgComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridwidth = 3;
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            listener.onOrgSelected(organizationList.getData().get(orgComboBox.getSelectedIndex()).getData());
        });

        add(selectButton, gbc);

        gbc.gridy++;
        gbc.weighty = 10;
        add(new JPanel(), gbc);
    }

    public interface Listener {
        void onOrgSelected(CapellaOrganization organization);
    }

    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }
}
