package org.intellij.sdk.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keyword-based line breaking for SQL++. String literals, backticked identifiers and comments are copied through
 * untouched; everything else has its whitespace collapsed and gets a line break before the main clauses.
 */
public class SQLPPFormatter {

    public static String format(String query) {
        return processText(query, 4);
    }

    private static String[] splitN1ql(String str, String tab) {
        return Pattern.compile("\\s+")
                .matcher(str)
                .replaceAll(" ")

                .replaceAll("(?i) AND ", "~::~" + tab + tab + "AND ")
                .replaceAll("(?i) BETWEEN ", "~::~" + tab + "BETWEEN ")
                .replaceAll("(?i) CASE ", "~::~" + tab + "CASE ")
                .replaceAll("(?i) ELSE ", "~::~" + tab + "ELSE ")
                .replaceAll("(?i) END ", "~::~" + tab + "END ")
                .replaceAll("(?i) FROM ", "~::~FROM ")
                .replaceAll("(?i) GROUP\\s+BY", "~::~GROUP BY ")
                .replaceAll("(?i) HAVING ", "~::~HAVING ")
                .replaceAll("(?i) IN ", " IN ")

                .replaceAll("(?i) JOIN ", "~::~JOIN ")
                .replaceAll("(?i) CROSS~::~+JOIN ", "~::~CROSS JOIN ")
                .replaceAll("(?i) INNER~::~+JOIN ", "~::~INNER JOIN ")
                .replaceAll("(?i) LEFT~::~+JOIN ", "~::~LEFT JOIN ")
                .replaceAll("(?i) RIGHT~::~+JOIN ", "~::~RIGHT JOIN ")

                .replaceAll("(?i) ON ", "~::~" + tab + "ON ")
                .replaceAll("(?i) OR ", "~::~" + tab + tab + "OR ")
                .replaceAll("(?i) ORDER\\s+BY", "~::~ORDER BY ")
                .replaceAll("(?i) OVER ", "~::~" + tab + "OVER ")

                .replaceAll("(?i)\\(\\s*SELECT ", "~::~(SELECT ")
                .replaceAll("(?i)\\)\\s*SELECT ", ")~::~SELECT ")


                .replaceAll("(?i) THEN ", " THEN~::~" + tab)
                .replaceAll("(?i) UNION ", "~::~UNION~::~")
                .replaceAll("(?i) USING ", "~::~USING ")
                .replaceAll("(?i) WHEN ", "~::~" + tab + "WHEN ")
                .replaceAll("(?i) WHERE ", "~::~WHERE ")
                .replaceAll("(?i) WITH ", "~::~WITH ")

                .replaceAll("(?i) ALL ", " ALL ")
                .replaceAll("(?i) AS ", " AS ")
                .replaceAll("(?i) ASC ", " ASC ")
                .replaceAll("(?i) DESC ", " DESC ")
                .replaceAll("(?i) DISTINCT ", " DISTINCT ")
                .replaceAll("(?i) EXISTS ", " EXISTS ")
                .replaceAll("(?i) NOT ", " NOT ")
                .replaceAll("(?i) NULL ", " NULL ")
                .replaceAll("(?i) LIKE ", " LIKE ")
                .replaceAll("(?i)\\s*SELECT ", "SELECT ")
                .replaceAll("(?i)\\s*UPDATE ", "UPDATE ")
                .replaceAll("(?i) SET ", " SET ")

                .replaceAll("~::~+", "~::~")
                .split("~::~");
    }

    private static List<String> createShiftArr(Integer step) {
        String space = getSpaces(step);
        List<String> shift = new ArrayList<>();
        shift.add("\n");
        for (int ix = 0; ix < 100; ix++) {
            shift.add(shift.get(ix) + space);
        }

        return shift;
    }

    /** Text the formatter must not touch: string literals, backticked identifiers, and comments. */
    private static final Pattern PROTECTED = Pattern.compile(
            "'(?:[^'\\\\]|\\\\.)*'|\"(?:[^\"\\\\]|\\\\.)*\"|`[^`]*`|--[^\\n]*|//[^\\n]*|/\\*.*?\\*/", Pattern.DOTALL);

    private static String processText(String text, int step) {
        List<String> ar = new ArrayList<>();
        List<Boolean> literal = new ArrayList<>();
        Matcher protectedMatcher = PROTECTED.matcher(text);
        int consumed = 0;
        while (protectedMatcher.find()) {
            addCode(ar, literal, text.substring(consumed, protectedMatcher.start()), step);
            ar.add(protectedMatcher.group());
            literal.add(true);
            consumed = protectedMatcher.end();
        }
        addCode(ar, literal, text.substring(consumed), step);

        int deep = 0;
        int parenthesisLevel = 0;
        StringBuilder str = new StringBuilder();
        List<String> shift = createShiftArr(step);

        Pattern selectPattern = Pattern.compile("\\s*SELECT\\s*", Pattern.CASE_INSENSITIVE);
        Pattern setPattern = Pattern.compile("\\s*SET\\s*", Pattern.CASE_INSENSITIVE);
        Pattern selectParenthesisPattern = Pattern.compile("\\s*\\(\\s*SELECT\\s*", Pattern.CASE_INSENSITIVE);

        int len = ar.size();
        for (int ix = 0; ix < len; ix++) {
            if (literal.get(ix)) {
                if (parenthesisLevel < 1 && deep > 0) {
                    deep--;
                }
                str.append(ar.get(ix));
                continue;
            }
            parenthesisLevel = isSubquery(ar.get(ix), parenthesisLevel);

            Matcher selectMatcher = selectPattern.matcher(ar.get(ix));
            Matcher setMatcher = setPattern.matcher(ar.get(ix));
            Matcher selectParenthesisMatcher = selectParenthesisPattern.matcher(ar.get(ix));

            if (selectMatcher.find()) {
                ar.set(ix, ar.get(ix).replace(",", ",\n" + getSpaces(step) + getSpaces(step)));
            }

            if (setMatcher.find()) {
                ar.set(ix, ar.get(ix).replace(",", ",\n" + getSpaces(step) + getSpaces(step)));
            }

            if (selectParenthesisMatcher.find()) {
                deep++;
                str.append(shift.get(deep)).append(ar.get(ix));
            } else {
                str.append(shift.get(deep)).append(ar.get(ix));
                if (parenthesisLevel < 1 && deep > 0) {
                    deep--;
                }
            }
        }

        str = new StringBuilder(str.toString().replaceFirst("\\n+", "").replaceAll("\\n+", "\n"));
        return str.toString();
    }

    private static void addCode(List<String> ar, List<Boolean> literal, String code, int step) {
        if (code.isEmpty()) {
            return;
        }
        for (String part : splitN1ql(code, getSpaces(step))) {
            ar.add(part);
            literal.add(false);
        }
    }

    private static int isSubquery(String str, int parenthesisLevel) {
        return parenthesisLevel - (str.replace("(", "").length() - str.replace(")", "").length());
    }


    private static String getSpaces(int numSpaces) {
        return " ".repeat(Math.max(0, numSpaces));
    }

}
