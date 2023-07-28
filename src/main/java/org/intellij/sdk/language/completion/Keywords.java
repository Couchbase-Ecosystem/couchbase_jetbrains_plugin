package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import org.intellij.sdk.language.psi.SqlppFile;
import org.intellij.sdk.language.psi.SqlppTokenSets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Keywords extends CompletionProvider<CompletionParameters> {
    private static final List<LookupElementBuilder> KWD = Arrays.stream(SqlppTokenSets.KEYWORDS.getTypes())
            .map(kw -> kw.toString().replace("SqlppTokenType.", ""))
            .flatMap(kw -> Stream.of(kw, kw.toLowerCase(Locale.ENGLISH)))
            .map(LookupElementBuilder::create)
            .collect(Collectors.toList());

    public Keywords(CompletionContributor with) {
        with.extend(
                    CompletionType.BASIC,
                    PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(SqlppFile.class))
                            .with(new PatternCondition<PsiElement>("excludes keywords") {
                                @Override
                                public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                    // after a dot
                                    if (element.getPrevSibling() != null && element.getPrevSibling().getNode().getElementType() == GeneratedTypes.DOT) {
                                        return false;
                                    }
                                    return true;
                                }
                            }),
                this
        );
    }


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(KWD);
    }
}
