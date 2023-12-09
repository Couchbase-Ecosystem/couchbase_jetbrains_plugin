package com.couchbase.intellij.workbench.chart;

import com.couchbase.client.java.json.JsonObject;
import utils.JsonObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static Map<String, String> getFieldTypes(List<JsonObject> results) {

        int counter = 0;
        Map<String, String> fields = new HashMap<>();
        for (JsonObject obj : results) {

            //we only analyze the up to the first 10 elements
            if (counter > 10) {
                break;
            }

            Map<String, String> map = JsonObjectUtil.generatePaths(obj, "", true);
            if (fields.isEmpty()) {
                fields = map;
            } else {

                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    //contains key but the value type is different and the new value is not unknown
                    if (map.containsKey(entry.getKey())
                            && !map.get(entry.getKey()).equals(entry.getValue())
                            && !"Unknown".equals(map.get(entry.getKey()))) {

                        //if the current value is unknown, accept the new one
                        if ("Unknown".equals(entry.getValue())) {
                            entry.setValue(map.get(entry.getKey()));
                            //if the current value is string, just keep it as string
                        } else if (!"String".equals(entry.getValue())) {
                            entry.setValue(map.get(entry.getKey()));
                        }
                    }
                }

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fields.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }

            counter++;
        }

        return fields;
    }

    public static void main(String[] args) {

        try {
            JsonObject jsonObject = JsonObject.fromJson(
                    "{ \"name\": \"john\", \"age\": 30, \"faca\": null, \"someArr\": [1,2,3,4]}"

            );

            JsonObject obj1 = JsonObject.fromJson(
                    "{ \"name\": \"john\", \"age\": 30, \"faca\": 1, \"other\": 1.5, \"geo\": {\"lon\":123, \"lat\": 124}}"

            );

            List<JsonObject> objs = new ArrayList<>();
            objs.add(jsonObject);
            objs.add(obj1);

            Map<String, String> map = getFieldTypes(objs);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
