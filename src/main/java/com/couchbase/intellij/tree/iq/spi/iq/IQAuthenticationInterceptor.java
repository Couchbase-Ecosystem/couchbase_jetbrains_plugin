package com.couchbase.intellij.tree.iq.spi.iq;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class IQAuthenticationInterceptor implements Interceptor {

    public IQAuthenticationInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = OpenAISettingsState.getInstance().getGpt4Config().getApiKey();
        Request request = chain.withReadTimeout(10, TimeUnit.SECONDS)
                .request()
               .newBuilder()
                .header("Authorization", String.format("Bearer %s", token))
                .header("Content-Type", "application/json")
                .build();
        Response response = chain.proceed(request);
        if (response.code() > 399) {
            JsonObject body = JsonObject.fromJson(response.body().string());
            if (!body.containsKey("error") && body.containsKey("message")) {
                OpenAiError error = new OpenAiError(new OpenAiError.OpenAiErrorDetails(body.getString("message"), "", "", String.valueOf(body.getInt("code"))));
                Integer code = body.containsKey("code") ? body.getInt("code") : -1;
                throw new OpenAiHttpException(error, null, code);
            }
        }
        return response;
    }
}
