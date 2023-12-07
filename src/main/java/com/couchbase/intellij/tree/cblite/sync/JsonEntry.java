package com.couchbase.intellij.tree.cblite.sync;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonEntry {

    String id;
    boolean isPush;
    boolean isDeletion;
    String scopeName;
    String collectionName;
    boolean isAccessRemoved;
}
