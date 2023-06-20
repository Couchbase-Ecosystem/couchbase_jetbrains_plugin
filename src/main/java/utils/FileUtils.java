package utils;

import com.couchbase.intellij.workbench.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    //TODO: Not TESTED ON WINDOWS YET
    public static void unzipFile(String zipFilePath, String destDir) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        String unzipCommand;

        if (osName.contains("win")) {
            unzipCommand = "powershell.exe -nologo -noprofile -command \"Expand-Archive -Path '" + zipFilePath + "' -DestinationPath '" + destDir + "' -Force\"";
        } else if (osName.contains("nix") || osName.contains("mac") || osName.contains("nux")) {
            unzipCommand = "unzip -o -q " + zipFilePath + " -d " + destDir;
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

        Process process = Runtime.getRuntime().exec(unzipCommand);

        try {
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
