package org.intellij.sdk.language.completion;

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
import generated.psi.Alias;
import generated.psi.KeyspaceRef;
import generated.psi.Statement;
import org.intellij.sdk.language.psi.SqlppTokenSets;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
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
        return getErroredStatement(element, false)
                .map(el -> getRightmostElement(el, c -> c.getNode().getElementType() == GeneratedTypes.EXPR))
                .filter(el -> el.getNode().getElementType() == GeneratedTypes.EXPR)
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

    public static Collection<Alias> getAliases(PsiElement s) {
        return PsiTreeUtil.collectElementsOfType(s, Alias.class);
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

    public static Stream<Alias> findAlias(PsiElement element, String name) {
        return getStatement(element)
                .stream()
                .flatMap(s -> getAliases(s).stream())
                .filter(a -> getAliasName(a).filter(n -> name == null || n.startsWith(name)).isPresent());
    }

    public static Optional<String> getAliasName(Alias a) {
        return getPreviousSibling(a, s -> s.getNode().getElementType() == GeneratedTypes.AS)
                .map(s -> getIdentifier(a));
    }

    public static List<String> getAliasPath(Alias alias) {
        return getPreviousSibling(alias, e -> e.getNode().getElementType() == GeneratedTypes.AS)
                .flatMap(as -> getPreviousSibling(as, Utils::isIdentifierOrRef)
                        .map(Optional::of)
                        .orElseGet(() -> getPreviousSibling(as, e -> e instanceof KeyspaceRef)
                                .map(Utils::getRightmostElement)
                                .map(Utils::walkUpDotIfPresent)
                        )
                )
                .map(e -> getPath(e))
                .orElse((List<String>) Collections.EMPTY_LIST);
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
}
