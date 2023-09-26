package org.intellij.sdk.language.completion;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.database.entity.CouchbaseField;
import com.couchbase.intellij.database.entity.CouchbaseScope;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import org.intellij.sdk.language.psi.SqlppFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SQLPPTemplates extends CompletionProvider<CompletionParameters> {

    private static List<LookupElement> recommendations;

    public SQLPPTemplates(CompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER),
                this
        );
    }


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext ctx, @NotNull CompletionResultSet result) {
        result.addAllElements(getStaticRecommendations());
        List<String> context = getQueryContext(parameters);
        if (!context.isEmpty()) {
            result.addAllElements(getDynamicRecommendations(context));
        }
    }


    private List<LookupElement> getDynamicRecommendations(List<String> path) {

        List<LookupElement> recommendations = new ArrayList<>();
        Optional<CouchbaseScope> scope = ((Stream<CouchbaseScope>) ActiveCluster.getInstance().navigate(path)).findFirst();
        if (scope.isPresent()) {
            Set<CouchbaseCollection> collections = scope.get().getChildren();
            Map<String, Set<String>> colAttributes = collections
                    .stream().collect(Collectors.toMap(CouchbaseCollection::getName, this::getCollectionAttributes));

            for (Map.Entry<String, Set<String>> entry : colAttributes.entrySet()) {
                recommendations.addAll(getCollectionRecommendations(entry.getKey(), entry.getValue()));
            }
        }
        return recommendations;
    }

    private List<LookupElement> getCollectionRecommendations(String collection, Set<String> attributes) {
        List<LookupElement> result = new ArrayList<>();
        if (!attributes.isEmpty()) {

            result.add(LookupElementBuilder.create("sel" + collection)
                    .withTypeText(getPreviewSelect(collection, attributes))
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();

                        int startOffset = editor.getCaretModel().getOffset() - (3 + collection.length());

                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + (3 + collection.length()));

                        Template template = createSelectWithAttrsTemplate(project, collection, attributes);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

        }
        return result;
    }

    private String getPreviewSelect(String collection, Set<String> attributes) {
        return "SELECT "
                + attributes.stream().limit(2).collect(Collectors.joining(", "))
                + ", ..."
                + " FROM " + collection + " as " + collection;
    }

    private Set<String> getCollectionAttributes(CouchbaseCollection col) {
        return col.getChildren().stream()
                .flatMap(e -> e.getChildren().stream())
                .map(CouchbaseField::getName).collect(Collectors.toCollection(TreeSet::new));
    }

    private List<LookupElement> getStaticRecommendations() {

        if (recommendations == null) {
            recommendations = new ArrayList<>();
            recommendations.add(LookupElementBuilder.create("sel")
                    .withTypeText("SELECT * FROM ...")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();

                        int startOffset = editor.getCaretModel().getOffset() - 3;

                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 3);

                        Template template = createSelectTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("selw")
                    .withTypeText("SELECT * FROM ... WHERE")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 4;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 4);
                        Template template = createSelectWhereTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("ins")
                    .withTypeText("INSERT INTO ... (KEY, VALUE) VALUES ( ..., ...);")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 3;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 3);
                        Template template = createInsertTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("upd")
                    .withTypeText("UPDATE ... SET ... = ...;")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 3;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 3);
                        Template template = createUpdateTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("selc")
                    .withTypeText("SELECT COUNT(*) FROM ...;")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 4;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 4);
                        Template template = createSelectCountTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("cidx")
                    .withTypeText("CREATE INDEX ... ON ... (..., ...);")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 4;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 4);
                        Template template = createIndexTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));

            recommendations.add(LookupElementBuilder.create("del")
                    .withTypeText("DELETE FROM ... WHERE ...;")
                    .withBoldness(true)
                    .withIcon(AllIcons.Json.Object)
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();
                        int startOffset = editor.getCaretModel().getOffset() - 3;
                        Document document = editor.getDocument();
                        document.deleteString(startOffset, startOffset + 3);
                        Template template = createDeleteTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));
        }
        return recommendations;
    }

    private List<String> getQueryContext(CompletionParameters parameters) {
        PsiElement element = Utils.cleanErrorIfPresent(parameters.getPosition());
        PsiFile psiFile = element.getContainingFile();
        return psiFile instanceof SqlppFile ? ((SqlppFile) psiFile).getClusterContext() : Collections.EMPTY_LIST;
    }


    private Template createInsertTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("ins", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("INSERT INTO ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(" (KEY, VALUE) VALUES ( ");
        template.addVariable("key", new TextExpression(""), true);
        template.addTextSegment(" , ");
        template.addVariable("value", new TextExpression(""), true);
        template.addTextSegment(" )");
        template.addTextSegment(";");
        return template;
    }

    private Template createUpdateTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("upd", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("UPDATE ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(" SET ");
        template.addVariable("attribute", new TextExpression(""), true);
        template.addTextSegment(" = ");
        template.addVariable("value", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }

    private Template createSelectTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("sel", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("SELECT * FROM ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }

    private Template createIndexTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("cidx", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("CREATE INDEX ");
        template.addVariable("indexName", new TextExpression(""), true);
        template.addTextSegment(" ON ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(" ( ");
        template.addVariable("storedAttributes", new TextExpression(""), true);
        template.addTextSegment(" ) ");
        template.addTextSegment(";");
        return template;
    }


    private Template createSelectWithAttrsTemplate(Project project, String collection, Set<String> attributes) {

        List<String> attributesList = new ArrayList<>(attributes);
        String attrStream = IntStream.range(0, attributesList.size())
                .mapToObj(i -> (i == 0 ? "" : "\t\t") + collection + "." + attributesList.get(i))
                .collect(Collectors.joining(", \n"));

        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("sel" + collection, "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("SELECT " + attrStream + "\n FROM " + collection + " as " + collection + " ");
        template.addVariable("filters", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }

    private Template createSelectWhereTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("selw", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("SELECT * FROM ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(" WHERE ");
        template.addVariable("attribute", new TextExpression(""), true);
        template.addTextSegment(" = ");
        template.addVariable("value", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }

    private Template createSelectCountTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("selc", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("SELECT COUNT(*) FROM ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }

    private Template createDeleteTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("del", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("DELETE FROM ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(" WHERE ");
        template.addVariable("filter", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }
}
