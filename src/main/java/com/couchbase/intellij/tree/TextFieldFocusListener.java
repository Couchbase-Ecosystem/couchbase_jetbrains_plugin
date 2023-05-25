package com.couchbase.intellij.tree;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextFieldFocusListener implements FocusListener {
    private final JTextField textField;
    private final Border defaultBorder;
    private final Border errorBorder;

    public TextFieldFocusListener(JTextField textField) {
        this.textField = textField;
        this.defaultBorder = textField.getBorder();
        this.errorBorder = BorderFactory.createLineBorder(Color.decode("#FF4444"));
    }

    @Override
    public void focusGained(FocusEvent e) {
        textField.setBorder(defaultBorder);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
            textField.setBorder(errorBorder);
        }
    }
}
