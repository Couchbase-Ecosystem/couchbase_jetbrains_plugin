package com.couchbase.intellij.embeddings;

import com.couchbase.intellij.config.CouchbaseSettingsStorage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenApiEmbeddingProvider implements EmbeddingProvider {

    private static final String BASE_URL = "https://api.openai.com/v1";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;


    public OpenApiEmbeddingProvider() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return "OpenAPI";
    }

    @Override
    public boolean isAvailable() {
        String key = CouchbaseSettingsStorage.getInstance().getState().getOpenApiKey();
        return key != null && !key.isEmpty();
    }

    @Override
    public List<String> listModels() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/models")
                .header("Authorization", "Bearer " + CouchbaseSettingsStorage.getInstance().getState().getOpenApiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonNode responseJson = objectMapper.readTree(response.body().string());
            List<String> embeddingModelList = new ArrayList<>();
            for (JsonNode model : responseJson.get("data")) {
                String modelId = model.get("id").asText();
                if (modelId.contains("embedding")) {
                    embeddingModelList.add(modelId);
                }
            }
            return embeddingModelList;
        }
    }

    @Override
    public String generateEmbedding(String model, String text) throws IOException {

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Creating the JSON request body
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put("model", model);

        ArrayNode inputArray = objectMapper.createArrayNode();
        inputArray.add(text);
        requestJson.set("input", inputArray);

        RequestBody body = RequestBody.create(JSON, objectMapper.writeValueAsString(requestJson));
        Request request = new Request.Builder()
                .url(BASE_URL + "/embeddings")
                .header("Authorization", "Bearer " + CouchbaseSettingsStorage.getInstance().getState().getOpenApiKey())
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return objectMapper.readTree(response.body().string()).get("data").get(0).get("embedding").toString();
        }
    }
}
