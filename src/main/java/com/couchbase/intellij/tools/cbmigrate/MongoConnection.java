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
    
    public static List<String> canConnect(String url) {

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


    public static List<String> getCollectionNames(String url, String databaseName) {
        MongoClient tempMongoClient = getMongoClient(url);
        return tempMongoClient.getDatabase(databaseName).listCollectionNames().into(new ArrayList<>());

    }
}
