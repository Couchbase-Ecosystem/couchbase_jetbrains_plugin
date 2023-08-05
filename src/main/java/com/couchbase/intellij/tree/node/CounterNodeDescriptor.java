package com.couchbase.intellij.tree.node;

import javax.swing.*;
import java.awt.*;

public abstract class CounterNodeDescriptor extends NodeDescriptor {
    private JLabel counter;
    private JPanel panel;

    public CounterNodeDescriptor(String text, Icon icon) {
        super(text, icon);
        counter = new JLabel("");
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(counter);
    }

    public JPanel getCounterPanel() {
        return panel;
    }

    public void setCounter(String text) {
        counter.setText("(" + text + ")");
        counter.revalidate();
        panel.revalidate();
    }

    @Override
    public String toString() {
        return getText();
    }
}
