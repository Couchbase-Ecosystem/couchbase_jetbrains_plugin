package com.couchbase.intellij.tree.examples;

import javax.swing.*;

public class Card {
    private ImageIcon image;
    private String title;
    private String description;
    private String url;

    public Card(ImageIcon image, String title, String description, String url) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
