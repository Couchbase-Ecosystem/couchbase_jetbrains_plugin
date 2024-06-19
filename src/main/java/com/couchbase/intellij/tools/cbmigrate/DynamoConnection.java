package com.couchbase.intellij.tools.cbmigrate;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamoConnection {

    public static List<String> listTables(String region, String awsAccessKeyId, String awsSecretAccessKey) {
        DynamoDbClient ddb = null;
        try {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);
            ddb = DynamoDbClient.builder().region(Region.of(region))
                                .credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
            return listTables(ddb);
        } finally {
            if (ddb != null) {
                ddb.close();
            }
        }
    }

    public static List<String> listTables(String region, String profile) {
        DynamoDbClient ddb = null;
        try {
            ddb = DynamoDbClient.builder().region(Region.of(region))
                                .credentialsProvider(ProfileCredentialsProvider.create(profile)).build();
            return listTables(ddb);
        } finally {
            if (ddb != null) {
                ddb.close();
            }
        }
    }


    public static Set<String> listProfiles() {

        try {
            ProfileFile profileFile = ProfileFile.builder().content(
                                                         Paths.get(System.getProperty("user.home"), ".aws", "credentials"))
                                                 .type(ProfileFile.Type.CREDENTIALS).build();
            Map<String, Profile> profiles = profileFile.profiles();
            return profiles.keySet();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    private static List<String> listTables(DynamoDbClient ddb) {
        ListTablesRequest request = ListTablesRequest.builder().build();
        ListTablesResponse response = ddb.listTables(request);
        return response.tableNames();
    }

}
