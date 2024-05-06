package com.couchbase.intellij.searchworkbench.validator;

public class CBSMessageUtil {


    public static String getUnexpectedAttUnder(String key, String target) {
        return "Unexpected attribute '" + key + "' under '" + target + "' object";
    }

    public static String singleOptionalKeyOccurrenceMessage(String key, String target) {
        return "The attribute '" + key + "' must not appear more than once under '" + target + "'";
    }

    public static String singleRequiredKeyOccurrenceMessage(String key, String target) {
        return "The attribute '" + key + "' must appear once under '" + target + "'";
    }
}
