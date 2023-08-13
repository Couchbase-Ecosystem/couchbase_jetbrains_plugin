package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import org.jetbrains.annotations.NotNull;

public class SqlppCompletionContributor extends CompletionContributor {


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
