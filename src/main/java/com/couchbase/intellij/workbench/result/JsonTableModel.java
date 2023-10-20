package com.couchbase.intellij.workbench.result;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.ItemRemovable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String targetCollection = Messages.showInputDialog("Give a target collection name", "Target Collection", null);
        String validationError = validateCollectionName(targetCollection);
        if (validationError != null) {
            Messages.showErrorDialog(validationError, "Invalid Collection Name");
            return null;
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                Messages.showErrorDialog(QUERY_KEY_ID_NOT_PRESENT_MESSAGE, "Invalid Key");
                return null;
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
        String targetCollection = Messages.showInputDialog("Give a target collection name", "Target Collection", null);
        String validationError = validateCollectionName(targetCollection);
        if (validationError != null) {
            Messages.showErrorDialog(validationError, "Invalid Collection Name");
            return null;
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                Messages.showErrorDialog(QUERY_KEY_ID_NOT_PRESENT_MESSAGE, "Invalid Key");
                return null;
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
        String targetCollection = Messages.showInputDialog("Give a target collection name", "Target Collection", null);
        String validationError = validateCollectionName(targetCollection);
        if (validationError != null) {
            Messages.showErrorDialog(validationError, "Invalid Collection Name");
            return null;
        }
        for (Map<String, Object> row : data) {
            Object keyObject = row.containsKey("key") ? row.get("key") : row.get("id");
            if (keyObject == null) {
                Messages.showErrorDialog(QUERY_KEY_ID_NOT_PRESENT_MESSAGE, "Invalid Key");
                return null;
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

    public String validateCollectionName(String targetCollection) {
        if (targetCollection == null || targetCollection.trim().isEmpty()) {
            return "Collection name cannot be empty.";
        }
        if (!targetCollection.matches("[A-Za-z0-9=+/.,_@]+")) {
            return "Collection name contains invalid characters. Only text letters [A-Z, a-z], digits [0-9], and special characters [= + / . , _ @] are allowed.";
        }
        return null;
    }

}
