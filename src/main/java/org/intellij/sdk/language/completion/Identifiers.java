package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.workbench.Log;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
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

        // match identifiers after specific keywords
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .with(new PatternCondition<PsiElement>("after keywords") {
                            @Override
                            public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                while (element.getPrevSibling() instanceof PsiWhiteSpace) {
                                    element = element.getPrevSibling();
                                }
                                return PlatformPatterns.psiElement(GeneratedTypes.SET).accepts(element.getPrevSibling())
                                        || PlatformPatterns.psiElement(GeneratedTypes.INTO).accepts(element.getPrevSibling());
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

    private static final BiPredicate<Integer, CouchbaseClusterEntity> passer = (depth, entity) -> depth <= 3;
    private static final BiPredicate<Integer, CouchbaseClusterEntity> emptyPathPasser = (depth, entity) -> depth <= 5;

    private static boolean appendRecursively(int depth, CouchbaseClusterEntity entity, CompletionResultSet result, BiPredicate<Integer, CouchbaseClusterEntity> filter) {
        boolean found = false;
        String name = entity.getName();
        if (name != null && (filter == null || filter.test(depth, entity))) {
//            log.debug("Completion appended recursively: " + name + "; depth: " + depth);
            result.addElement(LookupElementBuilder.create(name));
            found = true;
        }

        if (entity.getChildren() != null) {
            found |= 0 > entity.getChildren().stream().filter(c -> appendRecursively(depth + 1, c, result, filter)).count();
        }
        return found;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        ActiveCluster cluster = ActiveCluster.getInstance();
        if (cluster == null || cluster.getCluster() == null) {
            return;
        }

        PsiElement element = Utils.cleanErrorIfPresent(parameters.getPosition());

        PsiFile psiFile = element.getContainingFile();
        List<String> editorContext = psiFile instanceof SqlppFile ? ((SqlppFile) psiFile).getClusterContext() : Collections.EMPTY_LIST;
        List<List<String>> statementContexts = Utils.getStatementContexts(element);
        List<String> completePath = Utils.getPath(element);
        boolean openContext = Utils.isOpenContext(element);
        Log.debug("Open context: " + openContext);

        if (!completePath.isEmpty()) {
            if (!appendAliases(element, cluster, completePath, result) &&
                    0 == Utils.findEntities(cluster, openContext, editorContext, statementContexts, completePath)
                            .peek(e -> Log.debug("potential path target: " + e.path()))
                            .filter(entity -> completeForPath(entity, Collections.EMPTY_LIST, result))
                            .count()) {
                Log.debug("failed path completion");
                completePath = Collections.EMPTY_LIST;
            }
        }

        if (completePath.isEmpty()) {
            Log.debug("Empty path completion");
            appendAliases(element, cluster, Collections.EMPTY_LIST, result);
            if (editorContext.isEmpty() && openContext) {
                Log.debug("Empty statement context completion");
                appendRecursively(0, cluster, result, emptyPathPasser);
            }

            if (!statementContexts.isEmpty()) {
                Log.debug("STATEMENT context completion");
                Utils.findEntities(cluster, openContext, editorContext, statementContexts, Collections.EMPTY_LIST)
                        .forEach(entity -> completeForPath((CouchbaseClusterEntity) entity, Collections.EMPTY_LIST, result));
            }

            if (!editorContext.isEmpty() && Utils.isOpenContext(element)) {
                Log.debug("EDITOR context completion");
                completeForPath(cluster, editorContext, result);
            }
        }
    }

    private boolean appendAliases(PsiElement element, ActiveCluster cluster, List<String> path, CompletionResultSet result) {
        String name = path.size() > 0 ? path.get(0) : null;
        return 0 < Utils.findAlias(element, name).filter(alias -> {
            String aliasName = Utils.getAliasName(alias).get();
            if (aliasName.equals(name)) {
                List<String> aliasPath = Utils.getAliasPath(alias);
                if (aliasPath.size() == 1) {
                    List<String> context = Collections.EMPTY_LIST;
                    if (element.getContainingFile() instanceof SqlppFile) {
                        context = ((SqlppFile) element.getContainingFile()).getClusterContext();
                    }
                    aliasPath = Utils.expandCollectionPath(context, aliasPath.get(0));
                }
                int rm = aliasPath.size();
                aliasPath.addAll(path);
                aliasPath.remove(rm);
                boolean rt = completeForPath(cluster, aliasPath, result);
                return rt;
            } else {
                result.addElement(LookupElementBuilder.create(aliasName));
                return false;
            }
        }).count();
    }

    private boolean completeForPath(CouchbaseClusterEntity from, List<String> to, CompletionResultSet result) {
        boolean found = false;
        if (to == null || to.isEmpty()) {
            if (from.getChildren() != null) {
                long suggested = from.getChildren().stream()
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
                        .peek(e -> Log.debug(String.format("Complete option: %s", e.getLookupString())))
                        .peek(result::addElement)
                        .count();
                return true;
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
                        found |= completeForPath(child, subList, result);
                    } else if (to.size() == 1) {
                        result.addElement(LookupElementBuilder.create(child.getName()));
                        found = true;
                    }
                }
            }
        }
        return found;
    }

}
