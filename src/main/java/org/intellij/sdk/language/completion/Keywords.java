package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import org.intellij.sdk.language.psi.SqlppFile;
import org.intellij.sdk.language.psi.SqlppTokenSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Keywords extends CompletionProvider<CompletionParameters> {
    private static final List<LookupElementBuilder> KWD = Arrays.stream(SqlppTokenSets.KEYWORDS.getTypes())
            .map(kw -> LookupElementBuilder.create(kw.toString().replace("SqlppTokenType.", "")))
            .collect(Collectors.toList());
    public Keywords(CompletionContributor with) {
        with.extend(
                    CompletionType.BASIC,
                    PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(SqlppFile.class)),
                this
        );
    }


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(KWD);
    }
}
