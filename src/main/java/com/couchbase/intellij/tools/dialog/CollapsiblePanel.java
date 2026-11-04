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

    public CollapsiblePanel(String title, JPanel content, boolean isExpanded) {
        this(title, content);
        if (isExpanded) {
            toggleContent();
        }
    }

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

        // The separator stretches to whatever width the header gets, without a stored preferred width that would
        // keep the header (and the dialog) as wide as the last expanded layout after collapsing.
        JPanel separatorPanel = new JPanel(new GridBagLayout());
        GridBagConstraints line = new GridBagConstraints();
        line.fill = GridBagConstraints.HORIZONTAL;
        line.weightx = 1.0;
        line.insets = new Insets(0, 10, 0, 0); // Some space between text and line
        separatorPanel.add(separator, line);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(separatorPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        // The preferred size is the real one of header plus (visible) content: reporting the parent's current
        // width instead made expanded content narrower than it needs, so its grid slid off the left edge.
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
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

    public boolean contentIsEnabled() {
        return contentPanel.isVisible();
    }

}
