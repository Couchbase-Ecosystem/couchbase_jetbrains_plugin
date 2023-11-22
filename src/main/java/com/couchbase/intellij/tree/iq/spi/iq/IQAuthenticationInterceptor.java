package com.couchbase.intellij.tree.iq.spi.iq;

import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class IQAuthenticationInterceptor implements Interceptor {

    public IQAuthenticationInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = OpenAISettingsState.getInstance().getGpt4Config().getApiKey();
        Request request = chain.request()
                .newBuilder()
                .header("Authorization", String.format("Bearer %s", token))
                .header("Content-Type", "application/json")
                .build();
        return chain.proceed(request);
    }
}
