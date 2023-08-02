package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.IdentifierRef;
import generated.psi.impl.ExprImpl;
import org.intellij.sdk.language.psi.SqlppFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Identifiers extends CompletionProvider<CompletionParameters> {
    private static final Logger log = Logger.getInstance(Identifiers.class);

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

        // after failed expression
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile(SqlppFile.class))
                        .with(Utils.AFTER_FAILED_EXPR),
                this
        );
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        ActiveCluster cluster = ActiveCluster.getInstance();
        if (cluster == null || cluster.getCluster() == null) {
            return;
        }

        PsiElement element = Utils.cleanErrorIfPresent(parameters.getPosition());

        PsiFile psiFile = element.getContainingFile();
        List<String> path = psiFile instanceof SqlppFile ? ((SqlppFile) psiFile).getClusterContext() : Collections.EMPTY_LIST;

        path.addAll(Utils.getPath(element));

        if (path.isEmpty()) {
            appendRecursively(0, cluster, result, (depth, entity) -> depth <= 5);
        } else {
            completeForPath(cluster, path, result, 0, (depth, entity) -> depth <= 5);
        }

        appendAliases(element, cluster, path, result);
    }

    private void appendAliases(PsiElement element, ActiveCluster cluster, List<String> path, CompletionResultSet result) {
        String name = path.size() > 0 ? path.get(0) : null;
        Utils.findAlias(element, name).forEach(alias -> {
            String aliasName = Utils.getAliasName(alias).get();
            if (aliasName.equals(name)) {
                List<String> aliasPath = Utils.getAliasPath(alias);
                if (aliasPath.size() == 1) {
                    aliasPath = Utils.expandCollectionPath(aliasPath.get(0));
                }
                int rm = aliasPath.size();
                aliasPath.addAll(path);
                aliasPath.remove(rm);
                completeForPath(cluster, aliasPath, result, 0, (d, e) -> d <= 5);
            } else {
                result.addElement(LookupElementBuilder.create(aliasName));
            }
        });
    }

    private void completeForPath(CouchbaseClusterEntity from, List<String> to, CompletionResultSet result, int depth, BiPredicate<Integer, CouchbaseClusterEntity> failureRecoverGate) {
        if (to == null || to.isEmpty()) {
            if (from.getChildren() != null) {
                from.getChildren().stream()
                        .flatMap(e -> {
                            if (e.getName() == null) {
                                return e.getChildren().stream();
                            } else {
                                return Stream.of(e);
                            }
                        })
                        .map(CouchbaseClusterEntity::getName)
                        .filter(Objects::nonNull)
                        .map(LookupElementBuilder::create)
                        .peek(e -> log.debug(String.format("Complete option: %s", e.getLookupString())))
                        .forEach(result::addElement);
            }
        } else {
            String name = to.get(0);
            if (name != null) {
                Set<? extends CouchbaseClusterEntity> children = from.getChildren() == null ? Collections.EMPTY_SET :
                        from.getChildren().stream()
                                // ignore elements with no name
                                .flatMap(e -> e.getName() == null ? e.getChildren() == null ? Stream.empty() : e.getChildren().stream() : Stream.of(e))
                                .filter(Objects::nonNull)
                                .filter(e -> e.getName().startsWith(name))
                                .collect(Collectors.toSet());
                for (CouchbaseClusterEntity child : children) {
                    if (name.equals(child.getName())) {
                        List<String> subList = new ArrayList<>(to);
                        subList.remove(0);
                        completeForPath(child, subList, result, depth + 1, failureRecoverGate);
                    } else {
                        result.addElement(LookupElementBuilder.create(child.getName()));
                    }
                }
                if (children.isEmpty()) {
                    if (failureRecoverGate.test(depth, from) && from.getChildren() != null) {
                        from.getChildren().forEach(c -> completeForPath(c, to, result, depth + 1, failureRecoverGate));
                    }
                }
            }
        }
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
