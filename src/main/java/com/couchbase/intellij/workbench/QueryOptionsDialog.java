package com.couchbase.intellij.workbench;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.QueryPreferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class QueryOptionsDialog extends DialogWrapper {

    private JSpinner queryTimeout;
    private JLabel errorLabel;

    private JCheckBox queryHistoryCheckbox;

    protected QueryOptionsDialog(Project project) {
        super(project);
        setTitle("Query Options for " + ActiveCluster.getInstance().getId());

        init();
        getPeer().getWindow().setMinimumSize(new Dimension(300, 150));
    }


    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.insets = JBUI.insets(5);


        queryHistoryCheckbox = new JCheckBox("Save query history");
        queryHistoryCheckbox.setSelected(
                ActiveCluster.getInstance().getSavedCluster()
                        .getQueryPreferences().isSaveHistory());
        panel.add(queryHistoryCheckbox, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Query Timeout (sec):");
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        queryTimeout = new JSpinner(new SpinnerNumberModel(ActiveCluster.getInstance().getSavedCluster()
                .getQueryPreferences().getQueryTimeout(), 1, 2147483648L, 1));
        panel.add(queryTimeout, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        return panel;
    }

    protected Action @NotNull [] createActions() {
        Action cancelAction = getCancelAction();
        DialogWrapper dialog = this;

        Action pasteAction = new DialogWrapperAction("Save") {
            @Override
            protected void doAction(ActionEvent e) {

                Double timeout = Double.parseDouble(queryTimeout.getValue().toString());
                if (timeout < 1) {
                    errorLabel.setText("Query Timeout (sec) must be greater than or equal to 1.\n");
                    return;
                }

                QueryPreferences pref = ActiveCluster.getInstance().getSavedCluster()
                        .getQueryPreferences();

                pref.setSaveHistory(queryHistoryCheckbox.isSelected());
                pref.setQueryTimeout(timeout.intValue());
                dialog.close(DialogWrapper.OK_EXIT_CODE);
            }
        };
        return new Action[]{cancelAction, pasteAction};
    }
}