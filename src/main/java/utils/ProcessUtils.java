package utils;

import com.couchbase.intellij.workbench.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessUtils {

    public static void printOutput(Process process, String message) throws IOException {
        Log.info(message);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.info(line);
            }
        }
        // Consume and print the standard error
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.error(line);
            }
        }
    }
}
