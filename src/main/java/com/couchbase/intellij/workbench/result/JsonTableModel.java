package com.couchbase.intellij.workbench.result;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.intellij.util.ui.ItemRemovable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonTableModel extends AbstractTableModel implements ItemRemovable {

    private final List<Map<String, Object>> data;
    private final List<String> columns;

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
}
