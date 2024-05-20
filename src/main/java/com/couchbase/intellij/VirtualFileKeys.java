package com.couchbase.intellij;

import com.intellij.openapi.util.Key;

public class VirtualFileKeys {

    public static final Key<String> CONN_ID = new Key<>("connid");
    public static final Key<String> CLUSTER = new Key<>("cluster");
    public static final Key<String> BUCKET = new Key<>("bucket");
    public static final Key<String> SCOPE = new Key<>("scope");
    public static final Key<String> COLLECTION = new Key<>("col");
    public static final Key<String> ID = new Key<>("ID");
    public static final Key<String> CAS = new Key<>("cas");

    public static final Key<String> SEARCH_INDEX = new Key<>("searchindex");

    public static final Key<String> CBL_CON_ID = new Key<>("cbl_conn_id");

    public static final Key<String> READ_ONLY = new Key<>("readonly");

}
