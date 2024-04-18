package com.couchbase.intellij.tree.iq;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.core.CapellaAuth;
import com.couchbase.intellij.workbench.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CapellaApiMethods {
    private static String CAPELLA_DOMAIN;
    private static String AUTH_URL;
    private static String ORGANIZATIONS_URL;

    static {
        // DEV:
        // setCapellaDomain(System.getenv().getOrDefault("CAPELLA_DOMAIN", "https://api.dev.nonprod-project-avengers.com"));
        // PROD:
        setCapellaDomain(System.getenv().getOrDefault("CAPELLA_DOMAIN", "https://api.cloud.couchbase.com"));
    }


    public static CapellaAuth login(String username, String password) throws IOException, IllegalStateException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Capella username and password must not be null");
        }
        URL url = new URL(AUTH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");

        connection.setRequestProperty("Authorization", getBasicAuthHeader(username, password));

        if (connection.getResponseCode() != 200) {
            String errmsg = String.format("Authentication failed; Response code: %d",
                    connection.getResponseCode());
            throw new IllegalStateException(errmsg);
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
        URL url = new URL(ORGANIZATIONS_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + auth.getJwt());
        connection.setRequestProperty("Content-Type", "application/json");


        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to get organization list: HTTP error code: " + connection.getResponseCode());
        }


        String result = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CapellaOrganizationList resultList = objectMapper.readValue(result, CapellaOrganizationList.class);
        return resultList;
    }

    public static String getCapellaDomain() {
        return CAPELLA_DOMAIN;
    }

    public static void setCapellaDomain(String domain) {
        CAPELLA_DOMAIN = domain;
        AUTH_URL = domain + "/sessions";
        ORGANIZATIONS_URL = domain + "/v2/organizations";
    }

    public static JsonObject getOrganization(CapellaAuth auth, String orgId) throws Exception {
        URL url = new URL(String.format("%s/%s", ORGANIZATIONS_URL, orgId));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + auth.getJwt());
        connection.setRequestProperty("Content-Type", "application/json");


        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to get organization list: HTTP error code: " + connection.getResponseCode());
        }


        String result = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        return JsonObject.fromJson(result).getArray("data").getObject(0);
    }

    public static void acceptIqTerms(CapellaAuth auth, CapellaOrganization organization) throws Exception {
        JsonObject original = getOrganization(auth, organization.getId());
        URL url = new URL(String.format("%s/%s", ORGANIZATIONS_URL, organization.getId()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + auth.getJwt());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("PUT");
        OutputStream os = connection.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringWriter writer = new StringWriter();

        original.put("iq", JsonObject.create());

        JsonObject origIq = original.getObject("iq");
        origIq.put("enabled", true);
        origIq.put("other", JsonObject.create());
        JsonObject other = origIq.getObject("other");
        other.put("isTermsAcceptedForOrg", true);

        original.removeKey("modifiedAt");
        original.removeKey("upsertedAt");
        original.removeKey("modifiedBy");
        original.removeKey("modifiedByUserID");

        objectMapper.writeValue(writer, organization);
        objectMapper.writeValue(os, organization);

        Log.debug(String.format("Submitting the organization: %s", writer.toString()));

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to put organization: HTTP error code: " + connection.getResponseCode());
        }


        String result = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
        Log.debug(String.format("Accepted Capella IQ terms. Updated org: %s", result));
        return;
    }
}
