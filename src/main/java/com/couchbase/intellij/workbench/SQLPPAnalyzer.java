package com.couchbase.intellij.workbench;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLPPAnalyzer {

    public static boolean isMutation(String query) {

        List<String> items = createQueryTokens(query);
        return items.contains("insert")
                || items.contains("delete")
                || items.contains("upsert")
                || items.contains("replace")
                || items.contains("update")
                || items.contains("create")
                || items.contains("alter")
                || items.contains("drop")
                || items.contains("grant");
    }

    private static List<String> createQueryTokens(String query) {
        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'|`([^`]*)`");
        Matcher matcher = pattern.matcher(query);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add("\"" + matcher.group(1).toLowerCase() + "\"");
            } else if (matcher.group(2) != null) {
                tokens.add("'" + matcher.group(2).toLowerCase() + "'");
            } else if (matcher.group(3) != null) {
                tokens.add("`" + matcher.group(3).toLowerCase() + "`");
            } else {
                tokens.add(matcher.group().toLowerCase());
            }
        }
        return tokens;
    }
}
