package com.couchbase.intellij.tree.iq.spi.iq;

import com.couchbase.intellij.tree.iq.CapellaApiMethods;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.spi.OpenAiServiceProvider;
import com.couchbase.intellij.workbench.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;

public class CouchbaseIQServiceProvider implements OpenAiServiceProvider {

    @Override
    public boolean supportsEndpoint(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost().endsWith("couchbase.com")
                    || uri.getHost().endsWith("project-avengers.com")
                    || url.toLowerCase().contains(CapellaApiMethods.getCapellaDomain().toLowerCase());
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public OpenAiService createService(String group, OpenAISettingsState settings) {
        var modelSettings = settings.getConfigurationPage(group);
        var completionUrl = modelSettings.getApiEndpointUrl();
        var timeout = Duration.of(Long.parseLong(settings.getReadTimeout()), ChronoUnit.MILLIS);
        var token = modelSettings.getApiKey();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new CouchbaseLoggerAdapter());
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        OkHttpClient client = OpenAiService.defaultClient(token, timeout)
                .newBuilder()
                .addInterceptor(new IQAuthenticationInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        client.newBuilder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(completionUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        CouchbaseIqApi api = retrofit.create(CouchbaseIqApi.class);
        ExecutorService executorService = client.dispatcher().executorService();
        return new OpenAiService(api, executorService);
    }

    private class CouchbaseLoggerAdapter implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(@NotNull String s) {
            Log.debug(s);
        }
    }
}
