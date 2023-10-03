package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import org.jetbrains.annotations.NotNull;

public class SqlppCompletionContributor extends CompletionContributor {


    public SqlppCompletionContributor() {
        new Keywords(this);
        new Functions(this);
        new Identifiers(this);
        new SQLPPTemplates(this);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }
}
