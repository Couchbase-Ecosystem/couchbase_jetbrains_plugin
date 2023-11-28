package com.couchbase.intellij.tree.iq.core;

import lombok.Data;

@Data
public class CapellaAuth {
    private String jwt;
    private String tenant;
}
