package com.couchbase.intellij.tree;



import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class DatabaseConnectionDialog extends DialogWrapper {
    private JBTextField nameField;
    private JBTextField hostField;
    private JCheckBox enableSSLCheckBox;
    private JBTextField usernameField;
    private JBPasswordField passwordField;
    private JBTextArea consoleArea;
    private JBScrollPane consoleScrollPane;

    protected DatabaseConnectionDialog() {
        super(true); // use current window as parent
        init();
        setTitle("New Couchbase Connection");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(JBUI.Borders.empty(10)); // margin
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 5, 10); // bottom and right padding
        c.anchor = GridBagConstraints.WEST; // align to the left

        // Fields and Labels
        addField(panel, "Name:", 0, c);
        nameField = addTextField(panel, 1, 0, c);

        addField(panel, "Host:", 1, c);
        hostField = addTextField(panel, 1, 1, c);
        c.gridy = 1;
        c.gridx = 2;
        c.gridwidth = 1; // back to one column
        c.anchor = GridBagConstraints.CENTER;
        JBLabel linkLabel = new JBLabel("<html><a href=''>Sign up to Couchbase Capella</a></html>");
        linkLabel.setFont(linkLabel.getFont().deriveFont(10f)); // Decrease font size
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://cloud.couchbase.com/sign-up"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(linkLabel, c);

        addField(panel, "Username:", 2, c);
        usernameField = addTextField(panel, 1, 2, c);

        addField(panel, "Password:", 3, c);
        passwordField = new JBPasswordField();
        c.gridx = 1;
        c.gridy = 3;
        panel.add(passwordField, c);

        // CheckBox
        c.gridx = 2;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST; // align to the right
        enableSSLCheckBox = new JCheckBox("Enable SSL");
        panel.add(enableSSLCheckBox, c);

        // Console
        consoleArea = new JBTextArea(5, 20);
        consoleArea.setVisible(false);
        consoleScrollPane = new JBScrollPane(consoleArea);
        consoleScrollPane.setVisible(false);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3; // span 3 columns
        c.weighty = 1.0; // request any extra vertical space
        panel.add(consoleScrollPane, c);

        return panel;
    }

    private void addField(JPanel panel, String label, int y, GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = y;
        panel.add(new JBLabel(label), c);
    }

    private JBTextField addTextField(JPanel panel, int x, int y, GridBagConstraints c) {
        JBTextField textField = new JBTextField(20);
        c.gridx = x;
        c.gridy = y;
        panel.add(textField, c);
        return textField;
    }

    @Nullable
    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testConnectionButton = new JButton("Test Connection");
        testConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleArea.setText("Testing the connection...");
                consoleArea.setVisible(true);
                consoleScrollPane.setVisible(true); // Show the scroll pane as well
            }
        });
        panel.add(testConnectionButton);
        panel.add(super.createSouthPanel());

        return panel;
    }

    @Override
    protected void doOKAction() {
        // Add your save logic here
        super.doOKAction();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        // Add your validation logic here
        return super.doValidate();
    }

    @Override
    public void doCancelAction() {
        // You may want to add some custom logic here
        super.doCancelAction();
    }

    public static void main(String[] args) {
        DatabaseConnectionDialog dialog = new DatabaseConnectionDialog();
        dialog.show();
    }
}
