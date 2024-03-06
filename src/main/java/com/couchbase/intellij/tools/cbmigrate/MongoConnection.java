package com.couchbase.intellij.tools.cbmigrate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MongoConnection {

    private static MongoClient tempMongoClient;
    private String connectionString;

    public MongoConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    public List<String> listDatabases() {
        if (tempMongoClient == null) {
            tempMongoClient = getMongoClient(connectionString);
        }

        MongoIterable<String> databases = tempMongoClient.listDatabaseNames();
        List<String> databasesList = new ArrayList<>();
        for (String db : databases) {
            databasesList.add(db);
        }
        return databasesList;
    }

    public static List<String> testConnection(String url) {

        MongoClient tempMongoClient = getMongoClient(url);
        MongoIterable<String> databases = tempMongoClient.listDatabaseNames();
        List<String> databasesList = new ArrayList<>();
        for (String db : databases) {
            databasesList.add(db);
        }

        tempMongoClient.close();
        return databasesList;
    }

    @NotNull
    private static MongoClient getMongoClient(String url) {
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(url)).serverApi(serverApi).build();

        // Create a new client and connect to the server
        MongoClient tempMongoClient = MongoClients.create(settings);
        return tempMongoClient;
    }


    public List<String> getCollectionNames(String databaseName) {
        if (tempMongoClient == null) {
            tempMongoClient = getMongoClient(connectionString);
        }
        return tempMongoClient.getDatabase(databaseName).listCollectionNames().into(new ArrayList<>());
    }

    public void closeConnection() {
        if (tempMongoClient != null) {
            tempMongoClient.close();
        }
    }
}
