package org.intellij.sdk.language;

import com.intellij.lang.Language;

public class SQLPPLanguage extends Language {
    public static final SQLPPLanguage INSTANCE = new SQLPPLanguage();

    private SQLPPLanguage() {
        super("sqlpp", "text/sqlpp");
    }
}