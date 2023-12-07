package com.couchbase.intellij.tree.cblite.sqlppl;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.intellij.sdk.language.SqlppIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SQLPPLiteFileType extends LanguageFileType {
    public static final SQLPPLiteFileType INSTANCE = new SQLPPLiteFileType();

    private SQLPPLiteFileType() {
        super(SQLPPLiteLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "SQL++ Lite file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "SQL++ Lite language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "sqlppl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SqlppIcons.FILE;
    }
}
