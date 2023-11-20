package com.couchbase.intellij.tree.iq.spi.iq;

import com.couchbase.intellij.tree.iq.CapellaOrganization;
import com.couchbase.intellij.tree.iq.core.CapellaAuth;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.spi.OpenAiServiceProvider;
import com.couchbase.intellij.tree.iq.spi.azure.AzureOpenAiApi;
import com.couchbase.intellij.tree.iq.spi.azure.AzureOpenAiServiceConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CouchbaseIQServiceProvider implements OpenAiServiceProvider {

    private static final Pattern ORG_PATTERN = Pattern.compile("organizations\\/([\\d\\-a-fA-F-]{36})\\/");
    private static final Pattern BASE_URL_PATTERN = Pattern.compile("(https?://[^/]+/)");
    private CapellaOrganization organization;
    private CapellaAuth auth;

    @Override
    public boolean supportsEndpoint(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost().endsWith("couchbase.com")
                    || uri.getHost().endsWith("project-avengers.com");
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

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        OkHttpClient client = OpenAiService.defaultClient(token, timeout)
                .newBuilder()
                //.addInterceptor(loggingInterceptor)
                .addInterceptor(new IQAuthenticationInterceptor(token))
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

    private String extractOrgId(String url) {
        Matcher matcher = ORG_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String extractBaseUrl(String url) {
        Matcher matcher = BASE_URL_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }
}
