package com.couchbase.intellij.searchworkbench.contributor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CBSTemplateDef {

    private String key;
    private String desc;

    private List<String> attrs;
}
