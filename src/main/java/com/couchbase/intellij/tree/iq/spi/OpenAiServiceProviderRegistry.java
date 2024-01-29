package com.couchbase.intellij.tree.iq.spi;

import com.couchbase.intellij.tree.iq.spi.iq.CouchbaseIQServiceProvider;

import java.util.List;

public class OpenAiServiceProviderRegistry {

    private volatile List<OpenAiServiceProvider> providers;

    public List<OpenAiServiceProvider> getProviders() {
        if (providers == null)
            providers = List.of(new CouchbaseIQServiceProvider());
        return providers;
    }

    public OpenAiServiceProvider getProviderForCompletionUrl(String completionUrl) {
        for (OpenAiServiceProvider provider : getProviders())
            if (provider.supportsEndpoint(completionUrl))
                return provider;

        throw new AssertionError("OpenAiServiceProvider's not configured properly");
    }
}
