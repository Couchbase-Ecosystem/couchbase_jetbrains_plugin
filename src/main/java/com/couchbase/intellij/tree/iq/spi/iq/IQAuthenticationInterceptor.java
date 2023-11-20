package com.couchbase.intellij.tree.iq.spi.iq;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class IQAuthenticationInterceptor implements Interceptor {
    private final String token;

    public IQAuthenticationInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .header("Authorization", String.format("Bearer %s", token))
                .header("Content-Type", "application/json")
                .build();
        return chain.proceed(request);
    }
}
