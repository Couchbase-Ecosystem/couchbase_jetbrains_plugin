package com.couchbase.intellij.tree.overview.apis;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortManager {
    private Map<String, Integer> portMap;
    private final boolean isCapella;

    public PortManager(String serverURL) {
        this.isCapella = ActiveCluster.getInstance().isCapella();
        if (isCapella) {
            // If the active cluster is Capella, we don't allow port changing
            // Initialize the portMap with default values
            portMap = new HashMap<>();
            portMap.put("capi", 8092);
            portMap.put("capiSSL", 18092);
            portMap.put("kv", 11210);
            portMap.put("kvSSL", 11207);
            portMap.put("mgmt", 8091);
            portMap.put("mgmtSSL", 18091);
            // Add other services and their default ports...
        } else {
            // Make a GET request to the provided URL to get the JSON response
            try {
                String json = CouchbaseRestAPI.callGetEndpoint("/pools/default/nodeServices", serverURL);
                // Parse the JSON response to get the ports
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> data = gson.fromJson(json, type);
                this.portMap = (Map<String, Integer>) ((Map<String, Object>)((List<?>)data.get("nodesExt")).get(0)).get("services");
            } catch (Exception e) {
                // Handle exceptions appropriately
                Log.error("Error getting ports from server: " + serverURL, e);
            }
        }
    }

    public void updatePorts(String json) {
        if (!isCapella) {
            // Update the ports with the values from the API
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(json, type);
            this.portMap = (Map<String, Integer>) ((Map<String, Object>)((List<?>)data.get("nodesExt")).get(0)).get("services");
        }
    }

    public int getPort(String service) {
        // Return the current port for the given service
        return portMap.get(service);
    }

    public void setPort(String service, int port) {
        // Set the port for the given service
        portMap.put(service, port);
    }
}
