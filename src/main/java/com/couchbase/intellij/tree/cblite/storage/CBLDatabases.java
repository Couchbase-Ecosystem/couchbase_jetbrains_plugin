package com.couchbase.intellij.tree.cblite.storage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CBLDatabases {

    private List<SavedCBLDatabase> savedDatabases = new ArrayList<>();
}
