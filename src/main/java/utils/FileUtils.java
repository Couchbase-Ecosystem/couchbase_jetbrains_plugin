package utils;

import java.io.IOException;
import java.io.RandomAccessFile;

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
}
