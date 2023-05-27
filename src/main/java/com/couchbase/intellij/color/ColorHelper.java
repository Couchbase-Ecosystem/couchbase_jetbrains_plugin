package com.couchbase.intellij.color;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class ColorHelper {

    private static String keyword;
    private static String string;


    public static String getKeywordColor() {

        if(keyword != null) {
            return keyword;
        }
        EditorColorsManager colorsManager = EditorColorsManager.getInstance();

        EditorColorsScheme scheme = colorsManager.getGlobalScheme();
        TextAttributesKey keywordKey = TextAttributesKey.createTextAttributesKey("SQLPP_KEY", DefaultLanguageHighlighterColors.KEYWORD);
        TextAttributes keywordAttributes = scheme.getAttributes(keywordKey);
        Color keywordColor = keywordAttributes.getForegroundColor();
        keyword = String.format("#%02x%02x%02x", keywordColor.getRed(), keywordColor.getGreen(), keywordColor.getBlue());
        return keyword;
    }

    public static String getStringColor() {
        if(string != null) {
            return string;
        }

        EditorColorsManager colorsManager = EditorColorsManager.getInstance();
        EditorColorsScheme scheme = colorsManager.getGlobalScheme();
        TextAttributesKey keywordKey = TextAttributesKey.createTextAttributesKey("SQLPP_STRING", DefaultLanguageHighlighterColors.STRING);
        TextAttributes keywordAttributes = scheme.getAttributes(keywordKey);
        Color keywordColor = keywordAttributes.getForegroundColor();
        string =  String.format("#%02x%02x%02x", keywordColor.getRed(), keywordColor.getGreen(), keywordColor.getBlue());
        return string;
    }
}
