package com.couchbase.intellij.tools.dialog;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/** Sizing of the shared "Advanced Options" panel; it drives how the Pillow Fight and Export dialogs pack. */
public class CollapsiblePanelTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private static JPanel wideContent() {
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        content.add(new JLabel("Number of Threads, Percentage, Population and many more advanced settings:"), c);
        c.gridx = 1;
        content.add(new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)), c);
        return content;
    }

    private static void layoutTree(Component component) {
        if (component instanceof Container container) {
            container.doLayout();
            for (Component child : container.getComponents()) {
                layoutTree(child);
            }
        }
    }

    @Test
    public void expandedWidthFollowsTheContentAndCollapsingRestoresTheCompactWidth() {
        JPanel content = wideContent();
        CollapsiblePanel panel = new CollapsiblePanel("Advanced Options", content);
        JPanel parent = new JPanel(new BorderLayout());
        parent.add(panel, BorderLayout.CENTER);
        parent.setSize(100, 50);

        int collapsed = panel.getPreferredSize().width;
        assertTrue("collapsed: only the header counts", collapsed < content.getPreferredSize().width);

        panel.toggleContent();
        assertTrue("expanded: the content decides, not the parent's current width",
                panel.getPreferredSize().width >= content.getPreferredSize().width);

        // Lay the expanded panel out wide, as the dialog does after packing, then collapse again.
        parent.setSize(900, 400);
        layoutTree(parent);
        panel.toggleContent();
        assertFalse(panel.contentIsEnabled());
        assertEquals("the wide layout must not stick to the header separator", collapsed, panel.getPreferredSize().width);
    }
}
