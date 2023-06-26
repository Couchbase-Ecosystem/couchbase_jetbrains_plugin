package com.couchbase.intellij.tools.doctor;

import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.workbench.Log;

import java.io.*;

public class SdkDoctorRunner {
    public static void run(String host, boolean ssl, String bucket, String username, String password, Output lambda) {

        // Check the operating system
        String os = System.getProperty("os.name").toLowerCase();
        String sdkDoctorExecutable = "";

        if (os.contains("mac")) {
            sdkDoctorExecutable = "sdk-doctor-macos";
        } else if (os.contains("windows")) {
            sdkDoctorExecutable = "sdk-doctor-windows.exe";
        } else {
            sdkDoctorExecutable = "sdk-doctor-linux";
        }

        String temporaryFilePath = System.getProperty("java.io.tmpdir");
        if (temporaryFilePath.endsWith(File.separator)) {
            temporaryFilePath += sdkDoctorExecutable;
        } else {
            temporaryFilePath += File.separator + sdkDoctorExecutable;
        }
        String localPath = "/tools/sdkdoctor/" + sdkDoctorExecutable;
        try (InputStream inputStream = SdkDoctorRunner.class.getResourceAsStream(localPath);
             FileOutputStream outputStream = new FileOutputStream(temporaryFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            File tempFile = new File(temporaryFilePath);
            tempFile.setExecutable(true);

        } catch (Exception e) {
            Log.error("Error while copying SDK Doctor", e);
            e.printStackTrace();
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(temporaryFilePath, "diagnose", DataLoader.adjustClusterProtocol(host, ssl) + "/" + bucket, "-u", username, "-p", password);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.debug(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Log.debug("Exit value: " + exitCode);
            }


            File tempFile = new File(temporaryFilePath);
            tempFile.delete();

        } catch (Exception e) {
            Log.error("Error while running the SDK Doctor", e);
            e.printStackTrace();
        }

    }

    public interface Output {
        void processLine(String str);
    }
}