package com.couchbase.intellij.tree.cblite.sync;

import utils.CBIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RefreshIconRenderer extends DefaultTableCellRenderer {
    private final JLabel label = new JLabel(CBIcons.OPEN_DOCUMENT);

    public RefreshIconRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setToolTipText("Open Document");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return label;
    }
}
