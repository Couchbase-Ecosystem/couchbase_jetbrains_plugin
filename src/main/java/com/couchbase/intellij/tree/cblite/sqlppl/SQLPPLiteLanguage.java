package com.couchbase.intellij.tree.cblite.sqlppl;

import com.intellij.lang.Language;

public class SQLPPLiteLanguage extends Language {

    public static final SQLPPLiteLanguage INSTANCE = new SQLPPLiteLanguage();

    private SQLPPLiteLanguage() {
        super("sqlppl");
    }
}
