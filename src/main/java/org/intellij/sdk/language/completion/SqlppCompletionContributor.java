package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class SqlppCompletionContributor extends CompletionContributor {

    private static final Logger log = Logger.getInstance(SqlppCompletionContributor.class);

    public SqlppCompletionContributor() {
        new Keywords(this);
        new Functions(this);
        new Identifiers(this);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
