package utils;

import com.couchbase.intellij.workbench.Log;

import javax.swing.*;
import java.net.URL;

public class ImageLoader {
    public static ImageIcon loadImageIcon(String path) {
        URL imgURL = ImageLoader.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Log.debug("Couldn't find file: " + path);
            return null;
        }
    }
}
