package org.intellij.sdk.language.completion.cblite;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.cblite.SqlppLiteFile;
import org.intellij.sdk.language.cblite.SqlppLiteTokenSets;
import org.intellij.sdk.language.completion.CouchbaseLookupItem;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Keywords extends CompletionProvider<CompletionParameters> {
    private static final List<LookupElement> KWD = Arrays.stream(SqlppLiteTokenSets.KEYWORDS.getTypes())
            .map(kw -> kw.toString().replace("SqlppLiteTokenType.", ""))
            .map(s -> new CouchbaseLookupItem(s, false))
            .collect(Collectors.toList());

    public Keywords(SqlppLiteCompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(SqlppLiteFile.class)),
                this
        );
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(KWD);
    }
}
