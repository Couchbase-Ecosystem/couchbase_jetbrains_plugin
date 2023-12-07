package org.intellij.sdk.language.completion.cblite;

import com.intellij.codeInsight.completion.CompletionContributor;

public class SqlppLiteCompletionContributor extends CompletionContributor {
    public SqlppLiteCompletionContributor() {
        new Keywords(this);
    }
}
