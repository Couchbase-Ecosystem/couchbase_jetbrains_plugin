package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.IdentifierRef;
import generated.psi.impl.ExprImpl;
import generated.psi.impl.StatementImpl;
import org.intellij.sdk.language.psi.SqlppFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

public class Identifiers extends CompletionProvider<CompletionParameters> {
    public Identifiers(CompletionContributor with) {
        // match any identifier inside an expression
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).inside(ExprImpl.class),
                this
        );
        // match any identifier at error position inside function call argument list
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .inFile(
                                PlatformPatterns.psiFile(SqlppFile.class)
                        )
                        .afterSiblingSkipping(
                                PlatformPatterns.not(PlatformPatterns.psiElement(GeneratedTypes.LPAREN)),
                                PlatformPatterns.psiElement(GeneratedTypes.LPAREN)
                                        .afterSibling(PlatformPatterns.psiElement(GeneratedTypes.FUNCS))
                        ),
                this
        );
        // match any identifier at error position immediately after a backtick
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                       .with(new PatternCondition<PsiElement>("backtick") {

                           @Override
                           public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                               if (element.getPrevSibling() != null) {
                                   if ("`".equals(element.getPrevSibling().getText())) {
                                       return true;
                                   } else if (element.getPrevSibling() instanceof PsiErrorElement) {
                                       PsiElement tick = element.getPrevSibling().getPrevSibling();
                                       return tick != null && "`".equals(tick.getText());
                                   }
                               }
                               return false;
                           }
                       }),
                this
        );

        // match any identifier after a dot
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .with(new PatternCondition<PsiElement>("dot") {
                            @Override
                            public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                if (PlatformPatterns.psiElement(GeneratedTypes.DOT).accepts(element.getPrevSibling())
                                        || PlatformPatterns.psiElement(IdentifierRef.class).accepts(element.getParent())) {
                                    return true;
                                } else if (element.getPrevSibling() instanceof PsiErrorElement) {
                                    return ".".equals(element.getPrevSibling().getText());
                                }
                                return false;
                            }
                        }),
                this
        );
        // escaped dot
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .with(new PatternCondition<PsiElement>("escaped dot") {
                            @Override
                            public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                return element.getPrevSibling() instanceof PsiErrorElement && element.getPrevSibling().getLastChild().getNode().getElementType() == GeneratedTypes.DOT;
                            }
                        }),
                this
        );

        // match identifiers after SET keyword
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .with(new PatternCondition<PsiElement>("after SET") {
                            @Override
                            public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                while (element.getPrevSibling() instanceof PsiWhiteSpace) {
                                    element = element.getPrevSibling();
                                }
                                return PlatformPatterns.psiElement(GeneratedTypes.SET).accepts(element.getPrevSibling());
                            }
                        }),
                this
        );
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        ActiveCluster cluster = ActiveCluster.getInstance();
        if (cluster == null || cluster.getCluster() == null) {
            return;
        }

        PsiElement element = parameters.getPosition();
        if (element.getPrevSibling() != null && element.getPrevSibling().getNode().getElementType() == GeneratedTypes.DOT) {
            PsiElement dot = element.getPrevSibling();
            if (PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).accepts(dot.getPrevSibling())
                || dot.getPrevSibling() instanceof IdentifierRef) {
                List<String> path = getPath(element.getPrevSibling());
                completeForPath(cluster, path, result);
            } else if (dot.getPrevSibling() instanceof StatementImpl) {
                PsiElement statement = dot.getPrevSibling();
                while (statement.getLastChild() != null) {
                    statement = statement.getLastChild();
                }
                if (statement.getNode().getElementType() == GeneratedTypes.IDENTIFIER) {
                    completeForPath(cluster, getPath(statement), result);
                }
            }
        } else {
            appendRecursively(0, cluster, result, (depth, entity) -> depth <= 5);
        }
    }

    private void completeForPath(CouchbaseClusterEntity from, List<String> to, CompletionResultSet result) {
        if (to == null || to.isEmpty()) {
            from.getChildren().stream()
                    .map(CouchbaseClusterEntity::getName)
                    .filter(Objects::nonNull)
                    .map(LookupElementBuilder::create)
                    .forEach(result::addElement);
        } else {
            String name = to.get(0);
            if (name != null) {
                Optional<? extends CouchbaseClusterEntity> child = from.getChildren().stream()
                        .filter(Objects::nonNull)
                        .filter(e -> name.equals(e.getName()))
                        .findFirst();
                if (child.isPresent()) {
                    List<String> subList = new ArrayList<>(to);
                    subList.remove(0);
                    completeForPath(child.get(), subList, result);
                }
            }
        }
    }

    private List<String> getPath(PsiElement element) {
        List<String> result = new ArrayList<>();

        result.add(element.getText());
        while (element.getPrevSibling() != null &&
                element.getPrevSibling().getNode().getElementType() == GeneratedTypes.DOT &&
                element.getPrevSibling().getPrevSibling() instanceof IdentifierRef) {
            element = element.getPrevSibling().getPrevSibling();
            result.add(0, element.getText());
        }
        return result;
    }

    private static void appendRecursively(int depth, CouchbaseClusterEntity entity, CompletionResultSet result, BiPredicate<Integer, CouchbaseClusterEntity> filter) {
        String name = entity.getName();
        if (name != null && (filter == null || filter.test(depth, entity))) {
            result.addElement(LookupElementBuilder.create(name));
        }

        if (entity.getChildren() != null) {
            entity.getChildren().stream().forEach(c -> appendRecursively(depth + 1, c, result, filter));
        }
    }
}
