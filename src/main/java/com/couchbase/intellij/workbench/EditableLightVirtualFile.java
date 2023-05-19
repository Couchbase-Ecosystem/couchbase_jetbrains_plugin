package com.couchbase.intellij.workbench;

import com.intellij.lang.Language;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

public class EditableLightVirtualFile extends LightVirtualFile {
    public EditableLightVirtualFile(@NotNull String name) {
        super(name);
    }

    public EditableLightVirtualFile(@NotNull String name, @NotNull Language language, @NotNull CharSequence text) {
        super(name, language, text);
    }

    // Override the method to return true
    @Override
    public boolean isWritable() {
        return true;
    }
}
