package com.couchbase.intellij.tree.iq.api;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class IQRestService {

    private static final String API_URL = "https://api.dev.nonprod-project-avengers.com/sessions";
    private static final Gson gson = new Gson();


    public static AuthResponse login(String username, String password) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set up the connection for POST request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");

        // Set up Basic Authentication header
        String authValue = getBasicAuthHeaderValue(username, password);
        connection.setRequestProperty("Authorization", authValue);

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            return gson.fromJson(responseBuilder.toString(), AuthResponse.class);
        } finally {
            connection.disconnect();
        }
    }

    private static String getBasicAuthHeaderValue(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public static ChatCompletionResponse sendIQMessage(String orgId, String token, String systemMessage, String userMessage) throws Exception {
        URL url = new URL("https://api.dev.nonprod-project-avengers.com/v2/organizations/" + orgId + "/integrations/iq/openai/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Data payload
        String jsonPayload = "{\"messages\":[{\"role\":\"system\",\"content\":\"" + systemMessage + "\"},{\"role\":\"user\",\"content\":\"" + userMessage + "\"}],\"completionSettings\":{\"model\":\"gpt-4\", \"stream\":false}}";

        // Write payload
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            Gson gson = new Gson();
            return gson.fromJson(reader, ChatCompletionResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            connection.disconnect();
        }
    }

    public static OrganizationResponse loadOrganizations(String token) throws Exception {
        URL url = new URL("https://api.dev.nonprod-project-avengers.com/v2/organizations");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            System.out.println(responseContent);
            Gson gson = new Gson();
            return gson.fromJson(responseContent.toString(), OrganizationResponse.class);
        } finally {
            connection.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        try {


        OrganizationResponse response = loadOrganizations("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2OTY4Nzg0MzEsImlkIjoiZmNhNjkxM2ItY2JlOS00MDBkLTgyZTYtNTk3MTYwMDEzNzcwIn0.OK6OsYDFppUVCALqg7KmvB-xJKNroXxu3Y7M4mxg7lQ");
        System.out.println(response.getData().get(0).getData().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
