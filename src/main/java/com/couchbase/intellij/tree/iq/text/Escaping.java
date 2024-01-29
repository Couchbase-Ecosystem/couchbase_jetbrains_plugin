package com.couchbase.intellij.tree.iq.text;

public class Escaping {

    /**
     * Escapes all special characters in the given string.
     *
     * @param text the string to escape special characters in
     * @return a new string with all special characters escaped
     */
    public static String escapeMarkdown(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isSpecialChar(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isSpecialChar(char c) {
        return switch (c) {
            case '\\', '*', '_', '`', '#', '+', '-', '=', '|', '<', '>', '[', ']', '(', ')', '!' -> true;
            default -> false;
        };
    }
}