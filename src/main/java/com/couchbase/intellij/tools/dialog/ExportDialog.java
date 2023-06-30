package com.couchbase.intellij.tools.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class ExportDialog extends DialogWrapper {
    private JPanel mainPanel;

    public ExportDialog() {
        super(true);
        init();
        setTitle("Export Dialog");
        getWindow().setMinimumSize(new Dimension(600, 460));
        setResizable(true);
    }

    @Override
    protected JComponent createCenterPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel targetPanelWrapper = new JPanel(new BorderLayout());
        targetPanelWrapper.add(createPanel("Target", new String[]{"Bucket", "Scope", "Collections", "Format", "File Destination"}), BorderLayout.NORTH);
        mainPanel.add(targetPanelWrapper);

        JPanel panel = createAdvancedOptionsPanel(new String[]{"Exclude Collections", "Document Key", "Scope Field", "Collection Field", "Threads", "Verbose Log"});
        CollapsiblePanel col = new CollapsiblePanel("Advanced Settings", panel);
        col.setBorder(JBUI.Borders.emptyTop(15));
        col.setPanelToggleListener(this::pack);
        mainPanel.add(col);

        mainPanel.add(createContentPreviewPanel());

        return mainPanel;
    }

    private JPanel createPanel(String title, String[] labels) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new TitledSeparator(title), BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.weightx = 0.3;
            c.gridx = 0;
            formPanel.add(new JLabel(labels[i]), c);

            c.weightx = 0.7;
            c.gridx = 1;
            if (labels[i].equals("File Destination")) {
                TextFieldWithBrowseButton fileDestination = new TextFieldWithBrowseButton();
                fileDestination.addBrowseFolderListener("Select File Destination", "", null, FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());
                formPanel.add(fileDestination, c);
            } else {
                formPanel.add(new JComboBox(), c);
            }
        }

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createContentPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new TitledSeparator("Content Preview"), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setRows(10);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdvancedOptionsPanel(String[] labels) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.weightx = 0.3;
            c.gridx = 0;
            formPanel.add(new JLabel(labels[i]), c);

            c.weightx = 0.7;
            c.gridx = 1;
            if (labels[i].equals("Exclude Collections")) {
                formPanel.add(new JComboBox(), c);
            } else if (labels[i].equals("Verbose Log")) {
                formPanel.add(new JCheckBox(), c);
            } else {
                formPanel.add(new JTextField(), c);
            }
        }

        return formPanel;
    }
}
