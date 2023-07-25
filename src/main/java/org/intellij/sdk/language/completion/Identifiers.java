package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.database.entity.CouchbaseField;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.IdentifierRef;
import generated.psi.Str;
import generated.psi.impl.ExprImpl;
import org.jetbrains.annotations.NotNull;

public class Identifiers extends CompletionProvider<CompletionParameters> {
    public Identifiers(CompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).inside(ExprImpl.class),
                this
        );
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.ESCAPED_IDENTIFIER).inside(ExprImpl.class),
                this
        );
    }
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        if (element.getPrevSibling() != null && element.getPrevSibling().getNode().getElementType() == GeneratedTypes.DOT) {
            // todo: implement sub-fields
        } else {
            ActiveCluster cluster = ActiveCluster.getInstance();
            if (cluster != null && cluster.getCluster() != null) {
                appendRecursively(cluster, result);
            }
        }
    }

    private static void appendRecursively(CouchbaseClusterEntity entity, CompletionResultSet result) {
        String name = entity.getName();
        if (name != null) {
            result.addElement(LookupElementBuilder.create(name));
        }

        if (!(entity instanceof CouchbaseField)) {
            entity.getChildren().stream().forEach(c -> appendRecursively(c, result));
        }
    }
}
