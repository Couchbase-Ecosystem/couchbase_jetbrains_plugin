package utils;

import com.couchbase.intellij.workbench.Log;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static String readLastLine(String filePath) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        long lastByteIndex = file.length() - 1;
        StringBuilder sb = new StringBuilder();

        for (long filePointer = lastByteIndex; filePointer != -1; filePointer--) {
            file.seek(filePointer);
            int readByte = file.readByte();

            if (readByte == 0xA) {
                if (filePointer == lastByteIndex) {
                    continue;
                }
                break;
            } else if (readByte == 0xD) {
                if (filePointer == lastByteIndex - 1) {
                    continue;
                }
                break;
            }

            sb.append((char) readByte);
        }

        String lastLine = sb.reverse().toString();
        file.close();
        return lastLine;
    }

    public static String sampleElementFromJsonArrayFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonParser parser = mapper.createParser(Paths.get(filePath).toFile());
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
            };
            String result = null;

            while (parser.nextToken() != null) {
                if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                    Map<String, Object> jsonObject = parser.readValueAs(typeRef);
                    result = mapper.writeValueAsString(jsonObject);
                    break;
                }
            }

            parser.close();

            return result;
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static boolean checkFieldsInJson(String fieldText, String dataset) {
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(fieldText);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (!FileUtils.checkFieldInJson(dataset, match)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkFieldInJson(String filePath, String fieldText) {
        boolean isValidField = true;
        try {
            String sampleElement = FileUtils.sampleElementFromJsonArrayFile(filePath);
            if (sampleElement != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> jsonObject = mapper.readValue(sampleElement, new TypeReference<>() {
                });
                if (!jsonObject.containsKey(fieldText)) {
                    isValidField = false;
                }
            } else {
                isValidField = false;
            }
        } catch (Exception e) {
            Log.error("An error occurred while validating the field: " + e.getMessage());
            isValidField = false;
        }
        return isValidField;
    }

    public static String detectDatasetFormat(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine != null && firstLine.trim().startsWith("[")) {
                // File starts with a '[' character, so it's probably in list format
                return "list";
            } else if (firstLine != null && firstLine.trim().startsWith("{")) {
                // First line is a valid JSON object, so the file is probably in lines format
                return "lines";
            }
        }
        return null;
    }

    public static void createFolder(String folderPath) throws Exception {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                Log.error(e);
                System.out.println("Failed to create folder: " + e.getMessage());
                throw e;
            }
        }
    }

    // TODO: Not TESTED ON WINDOWS YET
    public static void unzipFile(String zipFilePath, String destDir) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();

        String zipFilePathCanonical, destDirCanonical;
        try {
            zipFilePathCanonical = new File(zipFilePath).getCanonicalPath();
            destDirCanonical = new File(destDir).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("Error canonicalizing path", e);
        }

        String[] unzipCommand;
        if (osName.contains("win")) {
            unzipCommand = new String[]{"powershell.exe", "-nologo", "-noprofile", "-command", "Expand-Archive -Path '" + zipFilePathCanonical + "' -DestinationPath '" + destDirCanonical + "' -Force"};
        } else if (osName.contains("nix") || osName.contains("mac") || osName.contains("nux")) {
            unzipCommand = new String[]{"unzip", "-o", "-q", zipFilePathCanonical, "-d", destDirCanonical};
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

        Process process = Runtime.getRuntime().exec(unzipCommand);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.info(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                Log.error(line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Command exited with code: " + exitCode);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Command execution was interrupted", e);
        }

        File zipFile = new File(zipFilePath);
        zipFile.delete();
    }

    public static void makeFilesExecutable(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    makeFilesExecutable(file);
                } else {
                    if (!file.setExecutable(true)) {
                        System.out.println("Could not set executable flag on file: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

}
