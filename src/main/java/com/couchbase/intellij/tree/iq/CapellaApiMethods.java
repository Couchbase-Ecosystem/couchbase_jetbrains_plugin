package com.couchbase.intellij.tree.iq;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.intellij.tree.iq.core.CapellaAuth;
import com.couchbase.intellij.tree.iq.core.IQCredentials;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class CapellaApiMethods {
    private static final String API_URL = "https://api.dev.nonprod-project-avengers.com/sessions";


    public static CapellaAuth login(String username, String password) throws IOException, IllegalStateException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Capella username and password must not be null");
        }
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");

        connection.setRequestProperty("Authorization", getBasicAuthHeader(username, password));

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Authentication failed");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(connection.getInputStream(), CapellaAuth.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read authentication response", e);
        }
    }

    private static String getBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public static CapellaOrganizationList loadOrganizations(CapellaAuth auth) throws IOException {
        URL url = new URL("https://api.dev.nonprod-project-avengers.com/v2/organizations");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + auth.getJwt());
        connection.setRequestProperty("Content-Type", "application/json");


        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
        }


        String result = IOUtils.toString(connection.getInputStream());
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(result, CapellaOrganizationList.class);
    }
}
