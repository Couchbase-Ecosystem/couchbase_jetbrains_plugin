package com.couchbase.intellij.searchworkbench;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CBSJsonFileType extends LanguageFileType {
    public static final CBSJsonFileType INSTANCE = new CBSJsonFileType();

    public static final Icon FILE = IconLoader.getIcon("/assets/cbs.png", CBSJsonFileType.class);

    private CBSJsonFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Couchbase Search Query";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "JSON query for couchbase search";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "cbs.json";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return FILE;
    }
}
