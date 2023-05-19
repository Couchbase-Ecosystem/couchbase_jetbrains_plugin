package com.couchbase.intellij.result;

import com.couchbase.client.java.json.JsonObject;
import com.intellij.ui.table.JBTable;
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

    public void updateData(List<JsonObject> jsonObjects) {
        data.clear();
        columns.clear();

        jsonObjects = unnestResult( jsonObjects);
        for (JsonObject jsonObject : jsonObjects) {
            Map<String, Object> map = jsonObject.toMap();
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
    private List<JsonObject> unnestResult(List<JsonObject> jsonObjects) {
        List<JsonObject> values = new ArrayList<>();
        String key = "";
        for(JsonObject obj: jsonObjects) {
            //if contains more the one key at the top-level,
            // that is not the case we are looking for
            if(obj.size() > 1) {
                return jsonObjects;
            }

            if("".equals(key)) {
                key = obj.getNames().toArray()[0].toString();
            } else if ( !key.equals(obj.getNames().toArray()[0].toString())) {
                //one of they keys is not similar to the the others
                //this is not the scenario that we are trying to solve on this method
                return jsonObjects;
            }
            values.add(obj.getObject(key));
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
}
