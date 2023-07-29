package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.impl.ExprImpl;
import org.intellij.sdk.language.psi.SqlppFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Functions extends CompletionProvider<CompletionParameters> {

    private static final String[] SQLPP_FUNC = {
            "ABS", "ACOS", "ARRAY_APPEND", "ARRAY_AVG", "ARRAY_CONCAT", "ARRAY_CONTAINS", "ARRAY_COUNT", "ARRAY_DISTINCT",
            "ARRAY_FLATTEN", "ARRAY_IFNULL", "ARRAY_INSERT", "ARRAY_INTERSECT", "ARRAY_LENGTH", "ARRAY_MAX",
            "ARRAY_MIN", "ARRAY_POSITION", "ARRAY_PREPEND", "ARRAY_PUT", "ARRAY_RANGE", "ARRAY_REMOVE", "ARRAY_REPEAT",
            "ARRAY_REPLACE", "ARRAY_REVERSE", "ARRAY_SORT", "ARRAY_STAR", "ARRAY_SUM", "ARRAY_SYMDIFF", "ARRAY_SYMDIFF1",
            "ARRAY_SYMDIFFN", "ARRAY_UNION", "ASIN", "ATAN", "ATAN2", "AVG", "CEIL", "CLOCK_LOCAL", "CLOCK_MILLIS",
            "CLOCK_STR", "CLOCK_TZ", "CLOCK_UTC", "CONCAT", "CONTAINS", "COS", "COUNT", "DATE_ADD_MILLIS", "DATE_ADD_STR",
            "DATE_DIFF_MILLIS", "DATE_DIFF_STR", "DATE_FORMAT_STR", "DATE_PART_MILLIS", "DATE_PART_STR",
            "DATE_RANGE_MILLIS", "DATE_RANGE_STR", "DATE_TRUNC_MILLIS", "DATE_TRUNC_STR", "DECODE_JSON", "DEGREES",
            "DURATION_TO_STR", "E", "ENCODE_JSON", "ENCODED_SIZE", "EXP", "FLOOR", "GREATEST", "IF_INF", "IF_MISSING",
            "IF_MISSING_OR_NULL", "IF_NAN", "IF_NAN_OR_INF", "IF_NULL", "IFINF", "IFMISSING", "IFMISSINGORNULL",
            "IFNAN", "IFNANORINF", "IFNULL", "INITCAP", "IS_ARRAY", "IS_ATOM", "IS_BOOL", "IS_BOOLEAN", "IS_NUM",
            "IS_NUMBER", "IS_OBJ", "IS_OBJECT", "IS_STR", "IS_STRING", "ISARRAY", "ISATOM", "ISBOOL", "ISBOOLEAN",
            "ISNUM", "ISNUMBER", "ISOBJ", "ISOBJECT", "ISSTR", "ISSTRING", "LEAST", "LENGTH", "LN", "LOG", "LOWER", "LTRIM",
            "MAX", "META", "MILLIS", "MILLIS_TO_LOCAL", "MILLIS_TO_STR", "MILLIS_TO_TZ", "MILLIS_TO_UTC",
            "MILLIS_TO_ZONE_NAME", "MIN", "MISSING_IF", "MISSINGIF", "NAN_IF", "NANIF", "NEGINF_IF", "NEGINFIF",
            "NOW_LOCAL", "NOW_MILLIS", "NOW_STR", "NOW_TZ", "NOW_UTC", "NULL_IF", "NULLIF", "OBJECT_ADD", "OBJECT_CONCAT",
            "OBJECT_INNER_VALUES", "OBJECT_LENGTH", "OBJECT_NAMES", "OBJECT_PAIRS", "OBJECT_PUT", "OBJECT_REMOVE",
            "OBJECT_RENAME", "OBJECT_REPLACE", "OBJECT_UNWRAP", "OBJECT_VALUES", "PAIRS", "PI", "POSINF_IF", "POSINFIF",
            "POSITION", "POWER", "RADIANS", "RANDOM", "REGEXP_CONTAINS", "REGEXP_LIKE", "REGEXP_POSITION", "REGEXP_REPLACE",
            "REPEAT", "REPLACE", "REVERSE", "ROUND", "RTRIM", "SIGN", "SIN", "SPLIT", "SQRT", "STR_TO_DURATION",
            "STR_TO_MILLIS", "STR_TO_TZ", "STR_TO_UTC", "STR_TO_ZONE_NAME", "SUBSTR", "SUM", "TAN", "TITLE", "TO_ARRAY",
            "TO_ATOM", "TO_BOOL", "TO_BOOLEAN", "TO_NUM", "TO_NUMBER", "TO_OBJ", "TO_OBJECT", "TO_STR", "TO_STRING",
            "TOARRAY", "TOATOM", "TOBOOL", "TOBOOLEAN", "TONUM", "TONUMBER", "TOOBJ", "TOOBJECT", "TOSTR",
            "TOSTRING", "TRIM", "TRUNC", "TYPE", "TYPENAME", "UPPER", "UUID", "WEEKDAY_MILLIS", "WEEKDAY_STR"
    };

    private static final Collection<LookupElement> LOOKUPS = new ArrayList<>();

    static {
        Arrays.stream(SQLPP_FUNC)
                .map(s -> new CouchbaseLookupItem(s, false))
                .forEach(LOOKUPS::add);
    }

    public Functions(CompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).inside(ExprImpl.class),
                this
        );
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER)
                        .with(Utils.EXCLUDE_FUNCTIONS)
                        .inFile(
                                PlatformPatterns.psiFile(SqlppFile.class)
                        )
                        .with(Utils.INCLUDE_FUNCTIONS),
                this
        );
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile(SqlppFile.class))
                        .with(Utils.EXCLUDE_FUNCTIONS)
                        .with(new PatternCondition<PsiElement>("custom places") {
                            @Override
                            public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                                return PlatformPatterns.psiElement()
                                        .afterSiblingSkipping(
                                                PlatformPatterns.psiElement(PsiWhiteSpace.class),
                                                PlatformPatterns.psiElement(GeneratedTypes.EQUAL)
                                        ).accepts(element);
                            }
                        }),
                this
        );
        // after failed expression
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile(SqlppFile.class))
                        .with(Utils.EXCLUDE_FUNCTIONS)
                        .with(Utils.AFTER_FAILED_EXPR),
                this
        );

        // inside DummyBlock
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile(SqlppFile.class))
                        .inside(PlatformPatterns.psiElement(GeneratedParserUtilBase.DummyBlock.class))
                        .with(Utils.EXCLUDE_FUNCTIONS),
                this
        );
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(LOOKUPS);
    }
}
