package com.couchbase.intellij.helpers;

import javax.swing.*;
import java.net.URL;

public class ImageLoader {
    public static ImageIcon loadImageIcon(String path) {
        URL imgURL = ImageLoader.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
