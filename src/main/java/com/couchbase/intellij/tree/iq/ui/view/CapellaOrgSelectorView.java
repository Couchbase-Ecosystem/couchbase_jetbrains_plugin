package com.couchbase.intellij.tree.iq.ui.view;

import com.couchbase.intellij.tree.iq.CapellaOrganization;
import com.couchbase.intellij.tree.iq.CapellaOrganizationList;
import com.couchbase.intellij.tree.iq.ui.ChatPanel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class CapellaOrgSelectorView extends JPanel {
    private static final String SELECT_ORG_LABEL = "Select Capella organization:";

    public CapellaOrgSelectorView(CapellaOrganizationList organizationList, ChatPanel.OrganizationListener listener) {
        super();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(20, 20, 10, 20);
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 2;

        add(new JLabel(SELECT_ORG_LABEL), gbc);
        ComboBox orgSelector = new ComboBox<>(organizationList.getData().stream()
                .map(org -> org.getData())
                .filter(data -> data.getIq() != null && data.getIq().isEnabled())
                .map(data -> String.format("%s%s", data.getName(), !data.getIq().getOther().getIsTermsAcceptedForOrg() ? " (*)" : ""))
                .toArray(String[]::new));

        gbc.gridy++;
        add(orgSelector, gbc);

        JButton select = new JButton("Select");
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(select, gbc);

        JButton cancel = new JButton("Cancel");
        gbc.gridx++;
        add(cancel, gbc);

        select.addActionListener(e -> {
            CapellaOrganization org = organizationList.getData().get(orgSelector.getSelectedIndex()).getData();
            listener.onOrgSelected(org);
        });

        cancel.addActionListener(e -> {
            listener.onOrgSelected(null);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }
}
