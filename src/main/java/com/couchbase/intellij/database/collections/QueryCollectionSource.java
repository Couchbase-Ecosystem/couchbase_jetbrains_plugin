package com.couchbase.intellij.database.collections;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;

import java.util.*;

/**
 * Pages through {@code system:all_keyspaces} so that only the requested slice of collections travels over the wire,
 * instead of the full scope manifest that {@code CollectionManager.getAllScopes()/getScope()} always returns.
 */
public class QueryCollectionSource implements CollectionSource {

    // system:keyspaces represents the default collection as a bucket without bucket/scope
    // attributes. all_keyspaces also supplies its explicit collection row, so the same
    // scoped predicate includes _default without duplicating the legacy bucket row.
    private static final String FROM = " FROM system:all_keyspaces WHERE `bucket` = $bucket AND `scope` = $scope";
    private static final String ORDER = " ORDER BY LOWER(`name`), `name`";

    private final Cluster cluster;
    private final String bucket;
    private final String scope;

    public QueryCollectionSource(Cluster cluster, String bucket, String scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
    }

    private JsonObject params() {
        return JsonObject.create().put("bucket", bucket).put("scope", scope);
    }

    private List<String> strings(String statement, JsonObject params) {
        return cluster.query(statement, QueryOptions.queryOptions().parameters(params)).rowsAs(String.class);
    }

    @Override
    public int count() {
        List<Integer> rows = cluster.query("SELECT RAW COUNT(*)" + FROM, QueryOptions.queryOptions().parameters(params()))
                .rowsAs(Integer.class);
        return rows.isEmpty() ? 0 : rows.get(0);
    }

    @Override
    public List<String> page(int offset, int limit) {
        return strings("SELECT RAW `name`" + FROM + ORDER + " LIMIT $limit OFFSET $offset",
                params().put("limit", limit).put("offset", offset));
    }

    @Override
    public List<String> search(String text, int limit) {
        String needle = text == null ? "" : text.toLowerCase(Locale.ROOT);
        return strings("SELECT RAW `name`" + FROM + " AND POSITION(LOWER(`name`), $text) >= 0" + ORDER + " LIMIT $limit",
                params().put("text", needle).put("limit", limit));
    }

    @Override
    public Set<String> existing(Collection<String> names) {
        if (names.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(strings("SELECT RAW `name`" + FROM + " AND `name` IN $names",
                params().put("names", JsonArray.from(new ArrayList<>(names)))));
    }
}
