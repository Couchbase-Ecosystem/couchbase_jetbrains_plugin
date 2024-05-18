package com.couchbase.intellij.embeddings;

import com.couchbase.intellij.config.CouchbaseSettingsStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoogleGeminiEmbeddingProvider implements EmbeddingProvider {

    private static final String LIST_MODELS_URL = "https://generativelanguage.googleapis.com/v1beta/models?key=";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/";

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    public GoogleGeminiEmbeddingProvider() {
        objectMapper = new ObjectMapper();
        client = new OkHttpClient();
    }

    @Override
    public String getName() {
        return "Google Gemini";
    }

    @Override
    public boolean isAvailable() {
        String key = CouchbaseSettingsStorage.getInstance().getState().getGoogleGeminiKey();
        return key != null && !key.isEmpty();
    }

    @Override
    public List<String> listModels() throws IOException {
        List<String> models;
        Request request = new Request.Builder()
                .url(LIST_MODELS_URL + CouchbaseSettingsStorage.getInstance().getState().getGoogleGeminiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            models = parseModelsFromResponse(responseBody);
        }
        return models;
    }

    @Override
    public String generateEmbedding(String model, String text) throws IOException {
        String url = BASE_URL + model + ":embedContent?key=" + CouchbaseSettingsStorage.getInstance().getState().getGoogleGeminiKey();

        String jsonRequestBody = String.format("{\n" +
                "        \"model\": \"%s\",\n" +
                "        \"content\": {\n" +
                "        \"parts\":[{\n" +
                "          \"text\": \"%s\"}]} }", model, text);

        RequestBody body = RequestBody.create(
                jsonRequestBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readTree(response.body().string()).get("embedding").get("values").toString();
        }
    }

    private List<String> parseModelsFromResponse(String responseBody) throws JsonProcessingException {
        JsonNode responseJson = objectMapper.readTree(responseBody);
        Iterator<JsonNode> it = responseJson.get("models").iterator();
        List<String> models = new ArrayList<>();
        while (it.hasNext()) {
            JsonNode modelNode = it.next();
            if (modelNode.get("supportedGenerationMethods").toString().contains("embedContent")) {
                models.add(modelNode.get("name").asText());
            }
        }
        return models;
    }
}