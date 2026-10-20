package com.couchbase.intellij.workbench;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import generated.GeneratedTypes;
import generated.psi.LimitClause;
import generated.psi.SelectStatement;
import generated.psi.Statement;
import org.intellij.sdk.language.SQLPPFileType;
import org.intellij.sdk.language.psi.SqlppFile;


public class SQLPPAnalyzer {

    private static final java.util.Set<String> MUTATIONS = java.util.Set.of(
            "insert", "delete", "upsert", "replace", "update", "create", "alter", "drop", "grant", "revoke", "build");

    public static boolean isMutation(String query) {
        for (int i = 0; i < query.length();) {
            i = skipTrivia(query, i);
            if (i >= query.length()) break;
            char current = query.charAt(i);
            if (current == '\'' || current == '"' || current == '`') {
                char quote = current;
                for (i++; i < query.length(); i++) {
                    if (query.charAt(i) == '\\') { i++; continue; }
                    if (query.charAt(i) == quote) {
                        if (i + 1 < query.length() && query.charAt(i + 1) == quote) { i++; continue; }
                        i++; break;
                    }
                }
            } else if (Character.isLetter(current) || current == '_') {
                int start = i++;
                while (i < query.length() && (Character.isLetterOrDigit(query.charAt(i)) || query.charAt(i) == '_')) i++;
                String word = query.substring(start, i).toLowerCase(java.util.Locale.ROOT);
                if (MUTATIONS.contains(word)) {
                    int next = skipTrivia(query, i);
                    if (word.equals("replace") && next < query.length() && query.charAt(next) == '(') continue;
                    return true;
                }
            } else i++;
        }
        return false;
    }

    private static int skipTrivia(String query, int offset) {
        int i = offset;
        while (i < query.length()) {
            if (Character.isWhitespace(query.charAt(i))) i++;
            else if (query.startsWith("--", i) || query.startsWith("//", i)) {
                while (i < query.length() && query.charAt(i) != '\n' && query.charAt(i) != '\r') i++;
            } else if (query.startsWith("/*", i)) {
                int end = query.indexOf("*/", i + 2);
                i = end < 0 ? query.length() : end + 2;
            } else break;
        }
        return i;
    }

    public static boolean isLimited(Project project, String query) {
        SqlppFile vFile = (SqlppFile) PsiFileFactory.getInstance(project).createFileFromText("__query__.sqlpp", SQLPPFileType.INSTANCE, query);
        Statement statement = PsiTreeUtil.getParentOfType(vFile.findElementAt(0), Statement.class);
        PsiElement firstDeepest = PsiTreeUtil.getDeepestFirst(statement);

        if (firstDeepest.getNode().getElementType() == GeneratedTypes.SELECT){
            PsiElement selStatement = PsiTreeUtil.getParentOfType(firstDeepest, SelectStatement.class);
            return PsiTreeUtil.getChildrenOfType(selStatement, LimitClause.class) != null;
        }
        return true;
    }
}
