package com.couchbase.intellij.tree.overview.apis;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class KVDocsListTest {

    private static final Gson GSON = new Gson();

    @Test
    public void rowIdsAreExtractedInOrder() {
        KVDocsList list = GSON.fromJson(
                "{\"rows\":[{\"id\":\"airline_1\"},{\"id\":\"airline_2\"}],\"totalRows\":2}", KVDocsList.class);

        List<String> ids = list.getRows().stream().map(KVRow::getId).collect(Collectors.toList());

        Assert.assertEquals(List.of("airline_1", "airline_2"), ids);
    }

    @Test
    public void emptyRowsGiveEmptyIdList() {
        KVDocsList list = GSON.fromJson("{\"rows\":[]}", KVDocsList.class);

        Assert.assertTrue(list.getRows().isEmpty());
    }

    @Test
    public void missingRowsIsNull() {
        // NOTE: possible bug: a response without "rows" gives a null list, and CouchbaseRestAPI.listKVDocuments
        // calls getRows().stream() on it, which would throw NullPointerException.
        KVDocsList list = GSON.fromJson("{\"errors\":[]}", KVDocsList.class);

        Assert.assertNull(list.getRows());
    }

    @Test
    public void rowWithoutIdHasNullId() {
        KVRow row = GSON.fromJson("{\"key\":\"x\"}", KVRow.class);

        Assert.assertNull(row.getId());
    }
}
