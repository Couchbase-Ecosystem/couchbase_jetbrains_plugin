package com.couchbase.intellij.workbench.result;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JsonTableModelTest {

    private static Map<String, Object> row(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    @Test
    public void emptyModelHasNoRowsOrColumns() {
        JsonTableModel model = new JsonTableModel();
        assertEquals(0, model.getRowCount());
        assertEquals(0, model.getColumnCount());
    }

    @Test
    public void flatRowsBecomeColumnsInFirstSeenOrder() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("name", "a", "age", 1L));
        data.add(row("name", "b", "age", 2L));

        model.updateData(data);

        assertEquals(2, model.getRowCount());
        assertEquals(2, model.getColumnCount());
        assertEquals("name", model.getColumnName(0));
        assertEquals("age", model.getColumnName(1));
        assertEquals("b", model.getValueAt(1, 0));
        assertEquals(2L, model.getValueAt(1, 1));
    }

    @Test
    public void columnsAreUnionOfAllRowKeys() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("a", 1L));
        data.add(row("b", 2L));

        model.updateData(data);

        assertEquals(2, model.getColumnCount());
        assertEquals("a", model.getColumnName(0));
        assertEquals("b", model.getColumnName(1));
        assertNull(model.getValueAt(1, 0));
    }

    @Test
    public void singleNestedObjectKeyIsUnwrapped() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("travel", JsonObject.create().put("city", "Paris")));
        data.add(row("travel", JsonObject.create().put("city", "Rome")));

        model.updateData(data);

        assertEquals(1, model.getColumnCount());
        assertEquals("city", model.getColumnName(0));
        assertEquals("Rome", model.getValueAt(1, 0));
    }

    @Test
    public void nestedArrayIsWrappedInContentColumn() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("list", JsonArray.from(1, 2)));

        model.updateData(data);

        assertEquals("content", model.getColumnName(0));
        // the array goes through JsonObject.toMap(), so the cell holds a plain java.util.List
        Object cell = model.getValueAt(0, 0);
        assertTrue(cell instanceof java.util.List);
        assertEquals(2, ((java.util.List<?>) cell).size());
    }

    @Test
    public void singleKeyScalarRowsAreKeptAsIs() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("count", 5L));
        data.add(row("count", 7L));

        model.updateData(data);

        assertEquals("count", model.getColumnName(0));
        assertEquals(7L, model.getValueAt(1, 0));
    }

    @Test
    public void mismatchedSingleKeysAreNotUnnested() {
        JsonTableModel model = new JsonTableModel();
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row("x", JsonObject.create().put("inner", 1)));
        data.add(row("y", JsonObject.create().put("inner", 2)));

        model.updateData(data);

        assertEquals(2, model.getColumnCount());
        assertEquals("x", model.getColumnName(0));
        assertEquals("y", model.getColumnName(1));
    }

    @Test
    public void updateDataReplacesPreviousContent() {
        JsonTableModel model = new JsonTableModel();
        model.updateData(new ArrayList<>(List.of(row("a", 1L), row("a", 2L))));
        model.updateData(new ArrayList<>(List.of(row("z", "only"))));

        assertEquals(1, model.getRowCount());
        assertEquals(1, model.getColumnCount());
        assertEquals("z", model.getColumnName(0));
    }

    @Test
    public void removeRowDropsOnlyThatRow() {
        JsonTableModel model = new JsonTableModel();
        model.updateData(new ArrayList<>(List.of(row("a", 1L), row("a", 2L), row("a", 3L))));

        model.removeRow(1);

        assertEquals(2, model.getRowCount());
        assertEquals(1L, model.getValueAt(0, 0));
        assertEquals(3L, model.getValueAt(1, 0));
    }

    @Test
    public void csvHasHeaderAndOneLinePerRow() {
        JsonTableModel model = new JsonTableModel();
        model.updateData(new ArrayList<>(List.of(row("name", "a", "age", 1L), row("name", "b", "age", 2L))));

        assertEquals("name,age\na,1\nb,2\n", model.tableModelToCSV());
    }

    @Test
    public void csvOfEmptyModelIsJustNewline() {
        assertEquals("\n", new JsonTableModel().tableModelToCSV());
    }

    @Test
    public void csvWritesMissingValueAsNullLiteral() {
        // NOTE: possible bug: CSV export does not quote or escape values and renders missing cells as "null"
        JsonTableModel model = new JsonTableModel();
        model.updateData(new ArrayList<>(List.of(row("a", 1L), row("b", 2L))));

        assertEquals("a,b\n1,null\nnull,2\n", model.tableModelToCSV());
    }
}
