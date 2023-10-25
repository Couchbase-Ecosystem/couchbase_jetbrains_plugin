package com.couchbase.intellij.tree.cblite.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedCBLiteDatabase {

    private String id;
    private String name;
    private String path;
}
