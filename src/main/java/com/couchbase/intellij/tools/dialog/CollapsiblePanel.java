package com.couchbase.intellij.tools.dialog;

import com.couchbase.intellij.tree.CouchbaseWindowContent;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollapsiblePanel extends JPanel {

    private PanelToggleListener panelToggleListener;
    private JPanel contentPanel;
    private JLabel headerLabel;
    private Icon expandedIcon;
    private Icon collapsedIcon;
    private JSeparator separator;

    private Integer originalWidth;

    public CollapsiblePanel(String title, JPanel content) {
        super(new BorderLayout());


        // Initialize icons
        expandedIcon = IconLoader.getIcon("/assets/icons/triangle-down.svg", CouchbaseWindowContent.class);
        // Replace with your own image path
        collapsedIcon = IconLoader.getIcon("/assets/icons/triangle-right.svg", CouchbaseWindowContent.class);
        // Replace with your own image path

        // Create content panel
        contentPanel = content;
        contentPanel.setVisible(false);

        // Create header
        headerLabel = new JLabel(title, collapsedIcon, JLabel.LEADING);
        headerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleContent();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                headerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                headerLabel.setCursor(Cursor.getDefaultCursor());
            }
        });

        separator = new JSeparator();

        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        titlePanel.add(headerLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Some space between text and line
        titlePanel.add(separator);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension headerPreferredSize = headerLabel.getPreferredSize();
        Dimension contentPreferredSize = contentPanel.isVisible() ? contentPanel.getPreferredSize() : new Dimension(0, 0);

        int preferredHeight = headerPreferredSize.height + contentPreferredSize.height + 30;

        return new Dimension(originalWidth = getParent().getWidth(), preferredHeight);
    }


    public void setPanelToggleListener(PanelToggleListener panelToggleListener) {
        this.panelToggleListener = panelToggleListener;
    }

    private void toggleContent() {
        if (contentPanel.isVisible()) {
            contentPanel.setVisible(false);
            headerLabel.setIcon(collapsedIcon);
        } else {
            contentPanel.setVisible(true);
            headerLabel.setIcon(expandedIcon);
        }

        if (panelToggleListener != null) {
            panelToggleListener.panelToggled();
        }

        // Inform parent to layout again since this component changed its size
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        super.doLayout();

        // Adjust the size of the separator to fill the rest of the space
        Dimension preferredSize = separator.getPreferredSize();
        preferredSize.width = getWidth() - headerLabel.getWidth() - 10; // 10 is the rigid area width
        separator.setPreferredSize(preferredSize);
        separator.setMaximumSize(preferredSize);
    }

    public boolean contentIsEnabled() {
        return contentPanel.isVisible();
    }

}
