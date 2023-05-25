package com.couchbase.intellij.tools.doctor;

import com.couchbase.intellij.database.DataLoader;

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

        String temporaryFilePath = System.getProperty("java.io.tmpdir") + File.separator + sdkDoctorExecutable;
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

            // Step 4: Execute the temporary file
            ProcessBuilder processBuilder = new ProcessBuilder(temporaryFilePath, "diagnose", DataLoader.adjustClusterProtocol(host, ssl)+"/"+bucket, "-u", username, "-p", password);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line of output
                lambda.processLine(line);
            }

            // Wait for the process to finish and get the exit value
            int exitCode = process.waitFor();

            // Print the exit value
            System.out.println("Exit value: " + exitCode);

            // Step 5: Clean up the temporary file
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface Output {
        void processLine(String str);
    }
}