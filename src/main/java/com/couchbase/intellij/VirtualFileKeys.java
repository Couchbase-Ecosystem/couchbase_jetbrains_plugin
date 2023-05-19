package com.couchbase.intellij;

import com.intellij.openapi.util.Key;

public class VirtualFileKeys {

    public static final Key<String> CLUSTER = new Key<>("cluster");
    public static final Key<String> BUCKET = new Key<>("bucket");
    public static final Key<String> SCOPE = new Key<>("scope");
    public static final Key<String> COLLECTION = new Key<>("col");
    public static final Key<String> ID = new Key<>("ID");
    public static final Key<String> CAS = new Key<>("cas");

}
