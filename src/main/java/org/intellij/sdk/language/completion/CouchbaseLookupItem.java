package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

public class CouchbaseLookupItem extends LookupElement {
    private final String lookupString;
    private final boolean caseSensitive;

    public CouchbaseLookupItem(@NotNull String lookupString) {
        this(lookupString, true);
    }

    public CouchbaseLookupItem(@NotNull String lookupString, boolean caseSensitive) {
        this.lookupString = lookupString;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public @NotNull String getLookupString() {
        return lookupString;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}
