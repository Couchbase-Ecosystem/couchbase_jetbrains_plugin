package com.couchbase.intellij.searchworkbench.contributor;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.searchworkbench.idxparser.SearchIndexCache;
import com.couchbase.intellij.searchworkbench.idxparser.SearchIndexParser;
import com.couchbase.intellij.workbench.Log;
import utils.JsonObjectUtil;

import java.util.*;

public class FieldsContributor {

    public static Map<String, String> getFieldNames(String bucket, String indexName) {

        String index = SearchIndexCache.getIndex(bucket, indexName);
        if (index != null) {
            try {
                Map<String, String> fields = SearchIndexParser.extractPropertiesMap(index);
                fields.put(SearchIndexParser.getDefaultField(index), null);

                boolean isDynamic = SearchIndexParser.isIndexDynamic(index);
                if (isDynamic) {
                    List<String> cols = SearchIndexParser.listCollections(index);
                    for (String prop : cols) {

                        if(!SearchIndexParser.isCollectionDynamicallyIndexed(index, prop)) {
                            continue;
                        }

                        if (prop.contains(".")) {
                            String[] parts = prop.split("\\.");
                            Map<String, String> additionalFields = getCollectionFields(bucket, parts[0], parts[1]);

                            for (Map.Entry<String, String> entry : additionalFields.entrySet()) {
                                //remove arrays
                                if (!entry.getKey().contains("[")) {
                                    if ((entry.getKey().endsWith(".lat") || entry.getKey().endsWith(".lon"))
                                            && entry.getKey().length() > 4) {
                                        fields.putIfAbsent(entry.getKey().substring(0, entry.getKey().length() - 4), null);
                                    }
                                    fields.putIfAbsent(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                }
                return fields;
            } catch (Exception e) {
                Log.debug("An error occurred while trying to extract fields from index: " + indexName, e);
            }
        }
        return new HashMap<>();
    }

    private static Map<String, String> getCollectionFields(String bucketName, String scopeName, String colName) throws Exception {

        Map<String, String> fields = new HashMap<>();
        Optional<JsonObject> obj = ActiveCluster.getInstance().getChild(bucketName)
                .flatMap(bucket -> bucket.getChild(scopeName))
                .flatMap(scope -> scope.getChild(colName))
                .map(col -> ((CouchbaseCollection) col).generateDocument())
                .filter(Objects::nonNull)
                .findFirst();

        obj.ifPresent(jsonObject -> fields.putAll(JsonObjectUtil.generatePaths(jsonObject, "")));

        return fields;
    }
}
