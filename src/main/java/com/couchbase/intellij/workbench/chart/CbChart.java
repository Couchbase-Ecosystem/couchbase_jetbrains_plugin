package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonObject;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface CbChart {

    void render(Map<String, String> fields, List<JsonObject> results);

    JPanel getFieldsPanel();

    JPanel getMainPanel();
}
