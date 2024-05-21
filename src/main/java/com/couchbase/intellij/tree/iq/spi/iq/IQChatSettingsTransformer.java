package com.couchbase.intellij.tree.iq.spi.iq;

import com.couchbase.client.java.json.JsonObject;
import com.google.common.net.MediaType;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class IQChatSettingsTransformer implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Buffer bodySink = new Buffer();
        chain.request().body().writeTo(bodySink);

        JsonObject body = JsonObject.fromJson(bodySink.readUtf8());
        if (!body.containsKey("completionSettings")) {
            if (body.containsKey("model")) {
                JsonObject completionSettings = JsonObject.create();
                completionSettings.put("model", body.getString("model"));
                if (body.containsKey("stream")) {
                    completionSettings.put("stream", body.getBoolean("stream"));
                } else {
                    completionSettings.put("stream", false);
                }
                body.put("completionSettings", completionSettings);
            }
        }

        RequestBody rqBody = RequestBody.create(body.toBytes());
        Request request = chain.request().newBuilder()
                .post(rqBody)
                .build();

        return chain.proceed(request);
    }
}
