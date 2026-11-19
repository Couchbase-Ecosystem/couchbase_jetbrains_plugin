package com.couchbase.intellij.tree.overview.apis;

/**
 * The Couchbase Server REST services the plugin calls, with their plain and TLS ports.
 * <p>
 * The scheme chosen in {@link CouchbaseRestAPI} and the port chosen here both follow the cluster's TLS setting,
 * so they must agree; keeping the port pairs in one place stops call sites from mixing an https scheme with a
 * plain port.
 */
enum CouchbaseService {
    MANAGEMENT(8091, 18091),
    SEARCH(8094, 18094),
    INDEX(9102, 19102);

    private final int plainPort;
    private final int tlsPort;

    CouchbaseService(int plainPort, int tlsPort) {
        this.plainPort = plainPort;
        this.tlsPort = tlsPort;
    }

    int port(boolean tls) {
        return tls ? tlsPort : plainPort;
    }

    /**
     * The {@code port + path} endpoint string that {@link CouchbaseRestAPI} appends to {@code scheme://host:}.
     */
    String endpoint(boolean tls, String path) {
        return port(tls) + path;
    }
}
