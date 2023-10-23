package com.couchbase.intellij.workbench.result;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.intellij.util.ui.ItemRemovable;
import com.intellij.util.ui.JBUI;

import utils.TemplateUtil;

public class JsonTableModel extends AbstractTableModel implements ItemRemovable {

    private final List<Map<String, Object>> data;
    private final List<String> columns;

    private static final String QUERY_KEY_ID_NOT_PRESENT_MESSAGE = "The query result should have an attribute called 'key' or 'id' to be exported in this way.";


    public JsonTableModel() {
        data = new ArrayList<>();
        columns = new ArrayList<>();
    }

    public void updateData(List<Map<String, Object>> result) {
        data.clear();
        columns.clear();

        result = unnestResult(result);
        for (Map<String, Object> map : result) {
            data.add(map);
            for (String key : map.keySet()) {
                if (!columns.contains(key)) {
                    columns.add(key);
                }
            }
        }
        fireTableStructureChanged();
    }

    /**
     * SQL++ sometimes returns the result nested inside an attribute
     * this mess up with the table rendering
     */
    private List<Map<String, Object>> unnestResult(List<Map<String, Object>> result) {
        List<Map<String, Object>> values = new ArrayList<>();
        String key = "";
        for (Map<String, Object> map : result) {
            //if contains more the one key at the top-level,
            // that is not the case we are looking for
            if (map.keySet().size() != 1) {
                return result;
            }

            if ("".equals(key)) {
                key = map.keySet().stream().findFirst().get();
            } else if (!key.equals(map.keySet().stream().findFirst().get())) {
                //one of they keys is not similar to the the others
                //this is not the scenario that we are trying to solve on this method
                return result;
            }

            Object obj = map.get(key);

            if (obj instanceof JsonObject) {
                JsonObject jo = (JsonObject) obj;
                values.add(jo.toMap());
            } else if (obj instanceof JsonArray) {
                JsonObject jo = JsonObject.create();
                jo.put("content", obj);
                values.add(jo.toMap());
            } else if (obj instanceof Map) {
                values.add((Map<String, Object>) obj);
            } else {
                values.add(map);
            }

        }

        return values;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map<String, Object> row = data.get(rowIndex);
        String column = columns.get(columnIndex);
        return row.get(column);
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public void removeRow(int idx) {
        data.remove(idx);
        fireTableRowsDeleted(idx, idx);
    }

    public String tableModelToCSV() {
        StringBuilder csvData = new StringBuilder();

        int colCount = getColumnCount();
        int rowCount = getRowCount();

        // Append column names
        for (int i = 0; i < colCount; i++) {
            csvData.append(getColumnName(i));
            if (i < colCount - 1) {
                csvData.append(",");
            }
        }
        csvData.append("\n");

        // Append rows data
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                csvData.append(getValueAt(row, col));
                if (col < colCount - 1) {
                    csvData.append(",");
                }
            }
            csvData.append("\n");
        }
        return csvData.toString();
    }

    public String convertToSQLPPUpsert() {
        StringBuilder sqlpp = new StringBuilder();
        CustomInputDialog dialog = new CustomInputDialog();
        String targetCollection = dialog.showDialog();
        if (targetCollection == null) {
            return null; // User closed the dialog or entered invalid collection name
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                return QUERY_KEY_ID_NOT_PRESENT_MESSAGE;
            }
            String key = keyObject.toString();
            // row.remove("key");
            // row.remove("id");
            sqlpp.append("UPSERT INTO ");
            sqlpp.append(targetCollection);
            sqlpp.append(" (KEY, VALUE) VALUES (\"");
            sqlpp.append(key);
            sqlpp.append("\", ");

            JsonObject value = JsonObject.from(row);
            sqlpp.append(value);

            sqlpp.append(");\n");
        }

        return sqlpp.toString();
    }

    public String convertToSQLPPInsert() {
        StringBuilder sqlpp = new StringBuilder();
        CustomInputDialog dialog = new CustomInputDialog();
        String targetCollection = dialog.showDialog();
        if (targetCollection == null) {
            return null; // User closed the dialog or entered invalid collection name
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                return QUERY_KEY_ID_NOT_PRESENT_MESSAGE;
            }
            String key = keyObject.toString();
            // row.remove("key");
            // row.remove("id");
            sqlpp.append("INSERT INTO ");
            sqlpp.append(targetCollection);
            sqlpp.append(" (KEY, VALUE) VALUES (\"");
            sqlpp.append(key);
            sqlpp.append("\", ");

            JsonObject value = JsonObject.from(row);
            sqlpp.append(value);

            sqlpp.append(");\n");
        }

        return sqlpp.toString();
    }

    public String convertToSQLPPUpdate() {
        StringBuilder sqlpp = new StringBuilder();
        CustomInputDialog dialog = new CustomInputDialog();
        String targetCollection = dialog.showDialog();
        if (targetCollection == null) {
            return null; // User closed the dialog or entered invalid collection name
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                return QUERY_KEY_ID_NOT_PRESENT_MESSAGE;
            }
            String key = keyObject.toString();
            // row.remove("key");
            // row.remove("id");
            sqlpp.append("UPDATE ");
            sqlpp.append(targetCollection);
            sqlpp.append(" USE KEYS \"");
            sqlpp.append(key);
            sqlpp.append("\" SET ");

            boolean first = true;
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (!first) {
                    sqlpp.append(", ");
                }
                sqlpp.append(entry.getKey()).append(" = ");
                if (entry.getValue() instanceof String) {
                    sqlpp.append("\"").append(entry.getValue()).append("\"");
                } else {
                    sqlpp.append(entry.getValue());
                }
                first = false;
            }

            sqlpp.append(";\n");
        }

        return sqlpp.toString();
    }

    private class CustomInputDialog extends JDialog {
        private final JTextField textField;
        private final JLabel errorLabel;
        private String value;

        public CustomInputDialog() {
            setModal(true); // Necessary to block input to other windows while this dialog is open
            setTitle("Target Collection");
            setMinimumSize(new Dimension(400, 10));

            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel labelWithHelp = TemplateUtil.getLabelWithHelp("Enter Target Collection Name",
                    "Your elaborate hint here.");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.insets = JBUI.insets(5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            centerPanel.add(labelWithHelp, gbc);

            textField = new JTextField(40);
            gbc.gridx = 1;
            centerPanel.add(textField, gbc);

            errorLabel = new JLabel();
            errorLabel.setForeground(Color.RED);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = 1;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            centerPanel.add(errorLabel, gbc);

            add(centerPanel, BorderLayout.CENTER);

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> onOk());
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> onCancel());

            southPanel.add(okButton);
            southPanel.add(cancelButton);

            add(southPanel, BorderLayout.SOUTH);

            pack();
        }

        private void onOk() {
            String text = textField.getText();
            String validationError = validateCollectionName(text);
            if (validationError != null) {
                setError(validationError);
                textField.setBorder(BorderFactory.createLineBorder(Color.decode("#FF4444")));
                pack();
            } else {
                value = text;
                setVisible(false);
            }
        }

        private void onCancel() {
            value = null;
            setVisible(false);
        }

        public String showDialog() {
            setVisible(true);
            return value;
        }

        public void setError(String error) {
            errorLabel.setText(error);
        }

        private String validateCollectionName(String targetCollection) {
            if (targetCollection == null || targetCollection.trim().isEmpty()) {
                return "Collection name cannot be empty.";
            }
            if ((targetCollection.startsWith("'") && !targetCollection.endsWith("'")) ||
                    (targetCollection.startsWith("\"") && !targetCollection.endsWith("\"")) ||
                    (targetCollection.startsWith("`") && !targetCollection.endsWith("`"))) {
                return "If collection name starts with a quote ('), double quote (\") or backtick (`), it must also end with the same.";
            }
            if (!targetCollection.matches("[A-Za-z0-9=+/.,_@'\"`-]+")) {
                return "Only text letters [A-Z, a-z], digits [0-9], and special characters [= + / . , _ @ ' \" ` -] are allowed.";
            }
            return null;
        }
    }

}
