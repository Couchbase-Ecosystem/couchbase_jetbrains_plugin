package com.couchbase.intellij.tree.node;

/** "Load More" row at the end of a Collections node: loads the next page of collections, not documents. */
public class LoadMoreCollectionsNodeDescriptor extends LoadMoreNodeDescriptor {

    public LoadMoreCollectionsNodeDescriptor(String bucket, String scope, int pagedToLoad) {
        super(bucket, scope, null, pagedToLoad);
    }
}
