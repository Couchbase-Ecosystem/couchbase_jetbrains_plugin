package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.psi.SqlppFile;
import org.intellij.sdk.language.psi.SqlppTokenSets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Keywords extends CompletionProvider<CompletionParameters> {
    private static final List<LookupElement> KWD = Arrays.stream(SqlppTokenSets.KEYWORDS.getTypes())
            .map(kw -> kw.toString().replace("SqlppTokenType.", ""))
            .map(s -> new CouchbaseLookupItem(s, false))
            .collect(Collectors.toList());

    public Keywords(CompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(SqlppFile.class))
                        .with(Utils.EXCLUDE_KEYWORDS),
                this
        );
    }


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(KWD);
    }
}
