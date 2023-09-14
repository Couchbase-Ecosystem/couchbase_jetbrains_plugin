package com.couchbase.intellij.tools;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.workbench.Log;
import utils.ProcessUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CBStats {
    private String bucketName;
    private String scopeName;
    private String collectionName;

    public CBStats(String bucketName, String scopeName, String collectionName) {
        this.bucketName = bucketName;
        this.scopeName = scopeName;
        this.collectionName = collectionName;
    }

    public String executeCommand() throws IOException, InterruptedException {
        StringBuilder output;

        // Your command
        List<String> command = new ArrayList<>();
        command.add(CBTools.getTool(CBTools.Type.CBSTATS).getPath());
        command.add("-h");
        command.add(ActiveCluster.getInstance().getClusterURL().replaceFirst("^couchbase://", ""));
        command.add("-p");
        command.add(ActiveCluster.getInstance().isSSLEnabled() ? "11207" : "11210");
        command.add("-u");
        command.add(ActiveCluster.getInstance().getUsername());
        command.add("-P");
        command.add(ActiveCluster.getInstance().getPassword());

        command.add("-b");
        command.add(bucketName);

        command.add("collections");
        command.add(scopeName + "." + collectionName);

        // Run your command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        output = new StringBuilder(ProcessUtils.returnOutput(process));

        if (process.waitFor() == 0) {
            Log.info("Command executed successfully");
        } else {
            Log.error("Command execution failed");
        }

        return output.toString();
    }
}
