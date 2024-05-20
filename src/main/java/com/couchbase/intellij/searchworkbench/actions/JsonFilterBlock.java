package com.couchbase.intellij.searchworkbench.actions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonFilterBlock {

    private String type;
    private int start;
    private int end;
    private int offset;
}
