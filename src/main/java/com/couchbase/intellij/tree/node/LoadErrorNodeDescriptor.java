package com.couchbase.intellij.tree.node;

import com.intellij.openapi.util.IconLoader;

/** A failed tree request can be retried without collapsing the node or losing loaded pages. */
public final class LoadErrorNodeDescriptor extends TooltipNodeDescriptor {
    private final Runnable retry;

    public LoadErrorNodeDescriptor(String subject, Throwable error, Runnable retry) {
        super("Couldn't load " + subject + " — Retry", error.getMessage() == null ? "Select this row to retry." : error.getMessage(),
                IconLoader.getIcon("/assets/icons/refresh.svg", LoadErrorNodeDescriptor.class));
        this.retry = retry;
    }

    public void retry() { retry.run(); }
}
