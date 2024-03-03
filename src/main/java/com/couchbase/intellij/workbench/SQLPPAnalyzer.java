package com.couchbase.intellij.workbench;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import generated.GeneratedTypes;
import generated.psi.LimitClause;
import generated.psi.SelectStatement;
import generated.psi.Statement;
import generated.psi.impl.StatementImpl;
import org.intellij.sdk.language.SQLPPFileType;
import org.intellij.sdk.language.SqlppParserDefinition;
import org.intellij.sdk.language.psi.SqlppFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLPPAnalyzer {

    public static boolean isMutation(String query) {

        List<String> items = createQueryTokens(query);
        return items.contains("insert")
                || items.contains("delete")
                || items.contains("upsert")
                || items.contains("replace")
                || items.contains("update")
                || items.contains("create")
                || items.contains("alter")
                || items.contains("drop")
                || items.contains("grant");
    }

    private static List<String> createQueryTokens(String query) {
        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'|`([^`]*)`");
        Matcher matcher = pattern.matcher(query);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add("\"" + matcher.group(1).toLowerCase() + "\"");
            } else if (matcher.group(2) != null) {
                tokens.add("'" + matcher.group(2).toLowerCase() + "'");
            } else if (matcher.group(3) != null) {
                tokens.add("`" + matcher.group(3).toLowerCase() + "`");
            } else {
                tokens.add(matcher.group().toLowerCase());
            }
        }
        return tokens;
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
