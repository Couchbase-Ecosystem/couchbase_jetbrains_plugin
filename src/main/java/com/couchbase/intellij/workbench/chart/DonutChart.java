package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonObject;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class DonutChart implements CbChart {
    @Override
    public void render(Map<String, String> fields, List<JsonObject> results) {
        
    }

    @Override
    public JPanel getFieldsPanel() {
        return null;
    }

    @Override
    public JPanel getMainPanel() {
        return null;
    }
}
