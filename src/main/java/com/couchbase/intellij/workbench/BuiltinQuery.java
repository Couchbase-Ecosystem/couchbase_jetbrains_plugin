package com.couchbase.intellij.workbench;

import lombok.Data;

@Data
public class BuiltinQuery {
    private String name;
    private String query;
    private String description;
}
