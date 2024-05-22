package com.couchbase.intellij.tree.docfilter;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.persistence.QueryFilter;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;

public class QueryFilterUtil {

    public static QueryFilter getQueryFilter( String bucket,
                                       String scope,
                                       String collectionName) {
        String content = QueryFiltersStorage.getInstance().getValue().getQueryFilter(
                ActiveCluster.getInstance().getId(),
                bucket,
                scope,
                collectionName);

        if(content == null) {
            return null;
        } else {

            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(content, QueryFilter.class);

            } catch (Exception e) {
                QueryFilter queryFilter = new QueryFilter();
                queryFilter.setQuery(content);
                return queryFilter;
            }
        }
    }

    public static void saveQueryFilter( String bucket,
                                 String scope,
                                 String collectionName, QueryFilter queryFilter ) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        QueryFiltersStorage.getInstance().getValue()
                .saveQueryFilter(
                        ActiveCluster.getInstance().getId(),
                        bucket,
                        scope,
                        collectionName,
                        mapper.writeValueAsString(queryFilter));

    }
}
