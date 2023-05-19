package com.couchbase.intellij.workbench.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class SQLPPFileType extends LanguageFileType {
    public static final SQLPPFileType INSTANCE = new SQLPPFileType();

    private SQLPPFileType() {
        super(SQLPPLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "SQL++ file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "SQL++ language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "sqlpp";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }
}
