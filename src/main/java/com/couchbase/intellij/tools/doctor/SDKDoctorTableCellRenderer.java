package com.couchbase.intellij.tools.doctor;

import javax.swing.table.DefaultTableCellRenderer;


import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class SDKDoctorTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setBackground(JBColor.background());

        String message = (String) value;
        if (message.startsWith("[WARN]") || message.contains(" WARN ")) {
            component.setForeground(JBColor.ORANGE);
        } else if (message.startsWith("[ERRO]") || message.contains(" ERRO ")) {
            component.setForeground(JBColor.RED);
        } else {
            component.setForeground(table.getForeground());
        }

        return component;
    }
}
