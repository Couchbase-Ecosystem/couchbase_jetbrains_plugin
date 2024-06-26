package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.KeyspaceRef;
import generated.psi.Statement;
import generated.psi.impl.AliasImpl;
import org.intellij.sdk.language.psi.SqlppTokenSets;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static final PatternCondition<PsiElement> AFTER_FAILED_EXPR = new PatternCondition<>("after failed expression") {

        @Override
        public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
            return isAfterFailedExpr(element);
        }
    };
    public static final PatternCondition<PsiElement> EXCLUDE_KEYWORDS = new PatternCondition<PsiElement>("excludes keywords") {
        @Override
        public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
            if (Utils.isAfterDot(element)) {
                return false;
            } else if (PlatformPatterns.psiElement().afterSiblingSkipping(PlatformPatterns.psiElement(PsiWhiteSpace.class), PlatformPatterns.psiElement(GeneratedTypes.EQUAL)).accepts(element)) {
                return false;
            } else if (PlatformPatterns.psiElement().inside(PlatformPatterns.psiElement(GeneratedParserUtilBase.DummyBlock.class)).accepts(element)) {
                return false;
            }
            return !PlatformPatterns.psiElement().inside(PlatformPatterns.psiElement(GeneratedTypes.EXPR)).accepts(element);
        }
    };
    public static final PatternCondition<PsiElement> EXCLUDE_FUNCTIONS = new PatternCondition<PsiElement>("excludes functions") {
        @Override
        public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
            // after a dot
            return !isAfterDot(element);
        }
    };

    public static final PatternCondition<PsiElement> START_OF_STATEMENT = new PatternCondition<PsiElement>("At the start of the statement") {
        @Override
        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
            boolean fileDetected = false;
            PsiElement prevSibling = null;
            if ((psiElement.getParent() instanceof PsiErrorElement && psiElement.getParent().getParent() instanceof PsiFile)) {
                prevSibling = psiElement.getParent().getPrevSibling();
                fileDetected = true;
            } else if (psiElement.getParent() instanceof PsiFile) {
                prevSibling = psiElement.getPrevSibling();
                fileDetected = true;
            }

            if (prevSibling instanceof PsiWhiteSpace) {
                prevSibling = prevSibling.getPrevSibling();
            }

            return fileDetected && (prevSibling == null || prevSibling.getNode().getElementType() == GeneratedTypes.SEMICOLON);
        }
    };

    public static final PatternCondition<PsiElement> INCLUDE_FUNCTIONS = new PatternCondition<PsiElement>("include functions") {
        @Override
        public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
            return PlatformPatterns.psiElement().afterSibling(
                            PlatformPatterns.psiElement(GeneratedTypes.LPAREN)
                                    .afterSibling(PlatformPatterns.psiElement(GeneratedTypes.FUNCS)))
                    .accepts(element);
        }
    };

    public static boolean isAfterFailedExpr(PsiElement element) {
        return isInFailedSubQuery(element) || getErroredStatement(element, false)
                .isPresent();
    }

    public static Optional<PsiErrorElement> getImmediateError(PsiElement element) {
        return getError(element, true);
    }

    public static Optional<PsiErrorElement> getError(PsiElement element) {
        return getError(element, false);
    }

    public static Optional<PsiErrorElement> getError(PsiElement element, boolean immediate) {
        while (!(element.getParent() instanceof PsiFile)) {
            element = element.getParent();
        }
        while (element.getPrevSibling() != null) {
            if (element.getPrevSibling() instanceof PsiErrorElement) {
                return Optional.of((PsiErrorElement) element.getPrevSibling());
            } else if (!immediate || element.getPrevSibling() instanceof PsiWhiteSpace) {
                element = element.getPrevSibling();
            } else if (immediate) {
                break;
            }
        }
        return Optional.empty();
    }

    public static Optional<Statement> getErroredStatement(PsiElement element, boolean immediate) {
        if (isInFailedSubQuery(element)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getError(element, immediate).map(e -> (PsiElement) e).orElseGet(() -> element))
                .map(e -> {
                    PsiElement psi = e;
                    while (psi.getPrevSibling() != null) {
                        if (psi.getPrevSibling().getNode().getElementType() == GeneratedTypes.STATEMENT) {
                            break;
                        }
                        psi = psi.getPrevSibling();
                    }
                    return (Statement) psi.getPrevSibling();
                }).filter(Objects::nonNull);
    }

    public static PsiElement getRightmostElement(PsiElement element, Predicate<PsiElement> breaker) {
        while (element.getLastChild() != null && !breaker.test(element)) {
            element = element.getLastChild();
        }
        return element;
    }

    public static PsiElement getRightmostElement(PsiElement element) {
        while (element.getLastChild() != null) {
            element = element.getLastChild();
        }
        return element;
    }

    public static Optional<PsiElement> getPreviousSibling(PsiElement element, Predicate<PsiElement> test) {
        while (element.getPrevSibling() != null) {
            element = element.getPrevSibling();
            if (test.test(element)) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }

    public static boolean isIdentifierOrRef(PsiElement element) {
        return element != null && (
                SqlppTokenSets.REFS.contains(element.getNode().getElementType())
                        || element.getNode().getElementType() == GeneratedTypes.IDENTIFIER
        );
    }

    public static PsiElement cleanErrorIfPresent(PsiElement element) {
        if (Utils.isAfterFailedExpr(element)) {
            if (isAfterDot(element)) {
                if (element.getPrevSibling() instanceof GeneratedParserUtilBase.DummyBlock) {
                    return unwrapDummyBlock(element.getPrevSibling()).orElseGet(() -> element.getPrevSibling().getPrevSibling());
                }
                return element.getPrevSibling().getPrevSibling();
            }
            if (isInFailedSubQuery(element)) {
                return element;
            }
            return getPreviousSibling(element, Utils::isDummyBlock)
                    .flatMap(Utils::unwrapDummyBlock)
                    .orElseGet(() -> Utils.getErroredStatement(element, true)
                            .map(Utils::getRightmostElement)
                            .map(Utils::walkUpDotIfPresent)
                            .orElse(element)
                    );
        }
        return Utils.walkUpDotIfPresent(element);
    }

    public static boolean isDummyBlock(PsiElement element) {
        return element instanceof GeneratedParserUtilBase.DummyBlock;
    }

    public static Optional<PsiElement> unwrapDummyBlock(PsiElement element) {
        if (element.getLastChild() != null && element.getLastChild().getNode().getElementType() == GeneratedTypes.DOT) {
            return Optional.of(element.getLastChild().getPrevSibling());
        }
        return Optional.empty();
    }

    public static PsiElement walkUpDotIfPresent(PsiElement element) {
        PsiElement original = element;
        while (element.getParent() != null) {
            if (!isAfterDot(element.getParent())) {
                return element;
            }
            element = element.getParent();
        }
        return original;
    }

    public static boolean isAfterDot(PsiElement element) {
        while (element.getParent() != null && !(element.getParent() instanceof PsiFile) && SqlppTokenSets.REFS.contains(element.getParent().getNode().getElementType())) {
            element = element.getParent();
        }
        while (element.getPrevSibling() != null) {
            if (element.getPrevSibling() instanceof GeneratedParserUtilBase.DummyBlock) {
                if (element.getPrevSibling().getLastChild() != null && element.getPrevSibling().getLastChild().getNode().getElementType() == GeneratedTypes.DOT) {
                    return true;
                }
            }
            if (element.getPrevSibling().getNode().getElementType() == GeneratedTypes.DOT) {
                return true;
            }
            if (!(element.getPrevSibling() instanceof PsiWhiteSpace)) {
                return false;
            }
            element = element.getPrevSibling();
        }

        // special case -- function return traversal
        PsiElement parentPath = PsiTreeUtil.findFirstParent(element, psiElement -> psiElement.getNode().getElementType() == GeneratedTypes.PATH);
        if (parentPath != null) {
            PsiElement parentPathPrevSibling = parentPath.getPrevSibling();
            if (parentPathPrevSibling != null && parentPathPrevSibling.getNode().getElementType() == GeneratedTypes.DOT) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFakeInitializer(PsiElement element) {
        return element != null && "intellijidearulezzz".equalsIgnoreCase(getIdentifier(element));
    }

    public static String getIdentifier(PsiElement element) {
        while (SqlppTokenSets.REFS.contains(element.getNode().getElementType())) {
            element = element.getLastChild();
        }
        if (element.getNode().getElementType() == GeneratedTypes.ESCAPED_IDENTIFIER) {
            element = element.getFirstChild().getNextSibling();
        }
        return element.getText();
    }

    public static Collection<AliasImpl> getAliases(PsiElement s) {
        return PsiTreeUtil.collectElementsOfType(s, AliasImpl.class);
    }

    private static PsiElement ffToStatementEnd(PsiElement element) {
        while (element.getLastChild() != null && (
                element.getPrevSibling() == null ||
                        !PlatformPatterns.psiElement(GeneratedTypes.DOT).accepts(element.getPrevSibling())
        )
        ) {
            element = element.getLastChild();
        }
        return element;
    }

    static List<String> getPath(PsiElement element) {
        List<String> result = new ArrayList<>();

        while (element != null && SqlppTokenSets.REFS.contains(element.getNode().getElementType())
                || PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).accepts(element)
                || PlatformPatterns.psiElement(GeneratedTypes.STATEMENT).accepts(element)
                || PlatformPatterns.psiElement(GeneratedTypes.BACKTICK).accepts(element)
        ) {
            if (PlatformPatterns.psiElement(GeneratedTypes.STATEMENT).accepts(element)) {
                element = ffToStatementEnd(element);
            }
            if (element.getNode().getElementType() == GeneratedTypes.BACKTICK) {
                element = element.getPrevSibling();
            }

            if (!isFakeInitializer(element)) {
                result.add(0, getIdentifier(element).replaceAll("(?i)intellijidearulezzz", ""));
            }

            if (element.getPrevSibling() != null && element.getPrevSibling().getNode().getElementType() == GeneratedTypes.BACKTICK) {
                element = element.getPrevSibling();
            }

            if (element.getPrevSibling() instanceof PsiErrorElement) {
                element = ffToStatementEnd(element.getPrevSibling().getPrevSibling());
            } else if (PlatformPatterns.psiElement(GeneratedTypes.DOT).accepts(element.getPrevSibling())) {
                element = element.getPrevSibling().getPrevSibling();
            } else if (element.getPrevSibling() instanceof GeneratedParserUtilBase.DummyBlock) {
                element = unwrapDummyBlock(element.getPrevSibling()).orElse(null);
            } else {
                break;
            }
        }
        return result;
    }

    public static Optional<Statement> getStatement(PsiElement element) {
        return getErroredStatement(element, false)
                .map(Optional::of)
                .orElseGet(() -> Optional.ofNullable(PsiTreeUtil.getParentOfType(element, Statement.class)));
    }

    public static Stream<AliasImpl> findAlias(PsiElement element, String name) {
        return getStatement(element)
                .stream()
                .flatMap(s -> getAliases(s).stream())
                .filter(a -> getAliasName(a).filter(n -> name == null || n.startsWith(name)).isPresent());
    }

    public static Optional<String> getAliasName(AliasImpl a) {
        return getPreviousSibling(a, s -> s instanceof PsiWhiteSpace)
                .map(PsiElement::getNextSibling)
                .map(Utils::getIdentifier);
    }

    public static List<String> getAliasPath(AliasImpl alias) {
        return getPreviousSibling(alias, e -> e.getNode().getElementType() == GeneratedTypes.AS)
                .map(Optional::of)
                .orElseGet(() -> getPreviousSibling(alias, e -> e instanceof PsiWhiteSpace))
                .flatMap(as -> getPreviousSibling(as, Utils::isIdentifierOrRef)
                        .map(Optional::of)
                        .orElseGet(() -> getPreviousSibling(as, e -> e instanceof KeyspaceRef)
                                .map(Utils::getRightmostElement)
                        )
                        .map(Utils::walkUpDotIfPresent)
                )
                .map(e -> getPath(e))
                .orElse((List<String>) Collections.EMPTY_LIST);
    }

    public static List<String> expandCollectionPath(List<String> context, String s) {
        Stream<CouchbaseCollection> options;
        if (context.isEmpty()) {
            options = ActiveCluster.getInstance().getChildren().stream()
                    .flatMap(b -> b.getChildren().stream())
                    .flatMap(scope -> scope.getChildren().stream())
                    .filter(c -> c.getName().equalsIgnoreCase(s));
        } else {
            options = ActiveCluster.getInstance().navigate(context)
                    .flatMap(scope -> scope.getChildren().stream())
                    .map(c -> (CouchbaseCollection) c);
        }

        Set<List<String>> matches = options
                .map(CouchbaseCollection::pathElements)
                .collect(Collectors.toSet());


        if (matches.size() == 1) {
            return matches.iterator().next();
        }

        ArrayList<String> stub = new ArrayList<>(context);
        stub.add(s);
        return stub;
    }

    public static boolean isInFailedSubQuery(PsiElement element) {
        while (element.getPrevSibling() != null) {
            if (element.getNode().getElementType() == GeneratedTypes.SELECT) {
                break;
            }
            element = element.getPrevSibling();
        }

        while (element.getPrevSibling() != null) {
            if (element.getNode().getElementType() == GeneratedTypes.LPAREN) {
                return true;
            }
            element = element.getPrevSibling();
        }
        return false;
    }

    public static List<List<String>> getStatementContexts(PsiElement element) {
        return getStatement(element)
                .stream()
                .flatMap(statement -> PsiTreeUtil.findChildrenOfType(statement, KeyspaceRef.class).stream())
                .map(Utils::getRightmostElement)
                .map(Utils::getPath)
                .collect(Collectors.toList());
    }

    public static Stream<? extends CouchbaseClusterEntity> findEntities(CouchbaseClusterEntity from, boolean openContext, List<String> editorContext, List<List<String>> statementContexts, List<String> path) {
        List<List<String>> options = new ArrayList<>();
        if (!editorContext.isEmpty()) {
            if (openContext) {
                options.add(editorContext);
            }
            statementContexts.stream()
                    .map(ctx -> {
                        List<String> editorOption = new ArrayList(editorContext);
                        editorOption.addAll(ctx);
                        return editorOption;
                    }).forEach(options::add);
        }
        if (statementContexts.isEmpty() || openContext) {
            options.add(Collections.EMPTY_LIST);
        } else {
            statementContexts.stream()
                    .map(context -> context.equals(path) ? Collections.EMPTY_LIST : context)
                    .forEach(options::add);
        }

        return options.stream()
                .distinct()
                .flatMap(p -> from.navigate(p))
                .flatMap(option -> option.navigate(path));
    }

    public static boolean isOpenContext(PsiElement element) {
        return getStatement(element)
                .map(statement -> {
                    PsiElement[] projections = PsiTreeUtil.collectElements(statement, e -> e.getNode().getElementType() == GeneratedTypes.PROJECTION);
                    if (projections != null &&
                            Arrays.stream(projections).anyMatch(projection -> PsiTreeUtil.isAncestor(projection, element, true))) {
                        return true;
                    }

                    PsiElement[] fromClauses = PsiTreeUtil.collectElements(statement, e -> e.getNode().getElementType() == GeneratedTypes.FROM_CLAUSE);
                    if (fromClauses != null &&
                            Arrays.stream(fromClauses).anyMatch(fromClause -> PsiTreeUtil.isAncestor(fromClause, element, true))) {
                        return true;
                    }

                    PsiElement[] targetKeyspaces = PsiTreeUtil.collectElements(statement, e -> e.getNode().getElementType() == GeneratedTypes.TARGET_KEYSPACE);
                    if (targetKeyspaces != null &&
                            Arrays.stream(targetKeyspaces).anyMatch(targetKeyspace -> PsiTreeUtil.isAncestor(targetKeyspace, element, true))) {
                        return true;
                    }

                    return (targetKeyspaces == null || targetKeyspaces.length == 0) &&
                            (fromClauses == null || fromClauses.length == 0) &&
                            (projections == null || projections.length == 0);
                }).orElse(true);
    }
}
