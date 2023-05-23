package com.couchbase.intellij.tools.doctor;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;

import javax.swing.*;
import java.awt.*;

public class SDKDoctorTableCellRenderer extends DefaultTableRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setBackground(Color.decode("#313131"));
        String message = (String) value;
        if (message.startsWith("[WARN]") || message.contains(" WARN ")) {
            component.setForeground(Color.YELLOW);
        } else if (message.startsWith("[ERRO]") || message.contains(" ERRO ") ) {
             component.setForeground(Color.RED);
        } else {
            component.setForeground(table.getForeground());
        }

        return component;
    }
}
