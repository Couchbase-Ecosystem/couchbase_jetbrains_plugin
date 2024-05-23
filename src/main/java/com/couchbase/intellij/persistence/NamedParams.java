package com.couchbase.intellij.persistence;

import lombok.Data;

import java.util.Map;

@Data
public class NamedParams {
    private Map<String, String> params;
}
