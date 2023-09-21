package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import generated.GeneratedTypes;
import generated.psi.impl.ExprImpl;
import org.jetbrains.annotations.NotNull;

public class SQLPPTemplates extends CompletionProvider<CompletionParameters> {
    public SQLPPTemplates(CompletionContributor with) {
        with.extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(GeneratedTypes.IDENTIFIER).inside(ExprImpl.class),
                this
        );
    }


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext ctx, @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("slt")
                    .withInsertHandler((context, item) -> {
                        Editor editor = context.getEditor();
                        Project project = context.getProject();

                        // Create and insert the template
                        Template template = createSelectTemplate(project);
                        TemplateManager.getInstance(project).startTemplate(editor, template);
                    }));
    }


    private Template createSelectTemplate(Project project) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = templateManager.createTemplate("Select", "SQLPP");
        template.setToReformat(true);
        template.addTextSegment("SELECT * FROM ");
        template.addVariable("collection", new TextExpression(""), true);
        template.addTextSegment(";");
        return template;
    }
}
