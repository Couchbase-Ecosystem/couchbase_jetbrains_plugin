package com.couchbase.intellij.tree.iq.util;

import java.util.Arrays;
import java.util.List;

public class SqlppLanguage implements Language {
    public static final SqlppLanguage INSTANCE = new SqlppLanguage();

    private SqlppLanguage() {

    }

    @Override
    public List<String> ids() {
        return Arrays.asList("sqlpp");
    }

    @Override
    public String mimeType() {
        return "text/sqlpp";
    }

    @Override
    public List<String> fileExtensions() {
        return Arrays.asList("sqlpp");
    }
}
