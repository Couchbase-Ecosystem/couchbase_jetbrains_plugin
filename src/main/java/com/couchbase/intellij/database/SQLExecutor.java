package com.couchbase.intellij.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLExecutor {

    public static List<Map<String, String>> query(String query) {

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> m1 = new HashMap<>();
        m1.put("header1", "val1");
        result.add(m1);

        Map<String, String> m2 = new HashMap<>();
        m2.put("header1", "val2");
        result.add(m2);

        return result;
    }
}
