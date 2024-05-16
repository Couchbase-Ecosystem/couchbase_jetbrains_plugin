package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.List;

public class CBSTemplates {

    public static final String RADIUS_QUERY = "radius_query";

    public static final String RECTANGLE_QUERY = "rectangle_query";
    public static final String POINT_GEO_QUERY = "point_geo_query";
    public static final String LINESTRING_GEO_QUERY = "linestring_geo_query";
    public static final String POLYGON_GEO_QUERY = "polygon_geo_query";

    public static LookupElement getPointGeoJsonTemplate(List<String> existingKeys) {
        return getGeoTemplate(POINT_GEO_QUERY, "Point GeoJSON Query", "Point", existingKeys);
    }

    public static LookupElement getLineStringTemplate(List<String> existingKeys) {
        return getGeoTemplate(LINESTRING_GEO_QUERY, "LineString GeoJSON Query", "LineString", existingKeys);
    }

    public static LookupElement getPolygonTemplate(List<String> existingKeys) {
        return getGeoTemplate(POLYGON_GEO_QUERY, "Polygon GeoJSON Query", "Polygon", existingKeys);
    }

    public static LookupElement getMultiPointTemplate(List<String> existingKeys) {
        return getGeoTemplate("multi_point_geo_query", "MultiPoint GeoJSON Query", "MultiPoint", existingKeys);
    }

    public static LookupElement getMultiLineStringTemplate(List<String> existingKeys) {
        return getGeoTemplate("multi_linestring_geo_query", "MultiLineString GeoJSON Query", "MultiLineString", existingKeys);
    }

    public static LookupElement getMultiPolygonTemplate(List<String> existingKeys) {
        return getGeoTemplate("multi_polygon_geo_query", "MultiPolygon GeoJSON Query", "MultiPolygon", existingKeys);
    }

    public static LookupElement getEnvelopeTemplate(List<String> existingKeys) {
        return getGeoTemplate("envelope_geo_query", "Envelope GeoJSON Query", "Envelope", existingKeys);
    }

    public static LookupElement getCircleTemplate(List<String> existingKeys) {
        return getGeoTemplate("circle_geo_query", "Circle GeoJSON Query", "Circle", existingKeys);
    }

    public static LookupElement getGeometryCollectionTemplate(List<String> existingKeys) {
        return getGeoTemplate("geometry_col_geo_query", "Geometry Collection GeoJSON Query", "GeometryCollection", existingKeys);
    }


    public static LookupElement getGeoTemplate(String key, String desc, String type, List<String> existingKeys) {
        return LookupElementBuilder.create(key)
                .withTypeText(desc)
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - key.length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + key.length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate(key, "JSON");
                    template.setToReformat(true);

                    boolean needsComma = false;

                    if (!existingKeys.contains("field")) {
                        template.addTextSegment("\"field\" : ");
                        template.addVariable("field", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("geometry")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"geometry\": {\n");
                        template.addTextSegment("\"shape\" : { \n");
                        template.addTextSegment("\"type\" : \"" + type + "\", \n");

                        if ("GeometryCollection".equals(type)) {
                            template.addTextSegment("\"geometries\" : [ \n");
                            template.addVariable("geometries", new TextExpression(""), true);
                            template.addTextSegment("]\n");
                        } else {
                            template.addTextSegment("\"coordinates\" :");
                            template.addVariable("coordinates", new TextExpression(""), true);
                        }

                        if ("Circle".equals(type)) {
                            template.addTextSegment(",\n \"radius\":");
                            template.addVariable("radius", new TextExpression(""), true);
                        }
                        template.addTextSegment("},\n");
                        template.addTextSegment("\"relation\" : ");
                        template.addVariable("relation", new TextExpression(""), true);
                        template.addTextSegment("\n}");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }


    public static LookupElement getMustTemplate() {
        return LookupElementBuilder.create("must")
                .withTypeText("Must boolean query template")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "must".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "must".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("must", "JSON");
                    template.setToReformat(true);


                    template.addTextSegment("\"must\" : {\n \"conjuncts\":[ \n{");
                    template.addVariable("filter", new TextExpression(""), true);
                    template.addTextSegment("}\n]\n}");
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getShouldTemplate() {
        return LookupElementBuilder.create("should")
                .withTypeText("Should boolean query template")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "should".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "should".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("should", "JSON");
                    template.setToReformat(true);


                    template.addTextSegment("\"should\" : {\n \"disjuncts\":[ \n{");
                    template.addVariable("filter", new TextExpression(""), true);
                    template.addTextSegment("}\n]\n}");
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getMustNotTemplate() {
        return LookupElementBuilder.create("must_not")
                .withTypeText("MustNot boolean query template")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "must_not".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "must_not".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("must_not", "JSON");
                    template.setToReformat(true);


                    template.addTextSegment("\"must_not\" : {\n \"disjuncts\":[ \n{");
                    template.addVariable("filter", new TextExpression(""), true);
                    template.addTextSegment("}\n]\n}");
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getRectangleTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create(RECTANGLE_QUERY)
                .withTypeText("Rectangle-Based Geopoint Query")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - RECTANGLE_QUERY.length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + RECTANGLE_QUERY.length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate(RECTANGLE_QUERY, "JSON");
                    template.setToReformat(true);

                    boolean needsComma = false;

                    if (!existingKeys.contains("field")) {
                        template.addTextSegment("\"field\" : ");
                        template.addVariable("field", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("top_left")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"top_left\": {");
                        template.addTextSegment("\"lon\" : ");
                        template.addVariable("lon", new TextExpression(""), true);
                        template.addTextSegment(",\n");
                        template.addTextSegment("\"lat\" : ");
                        template.addVariable("lat", new TextExpression(""), true);
                        template.addTextSegment("\n}");
                    }

                    if (!existingKeys.contains("bottom_right")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"bottom_right\": {");
                        template.addTextSegment("\"lon\" : ");
                        template.addVariable("lon", new TextExpression(""), true);
                        template.addTextSegment(",\n");
                        template.addTextSegment("\"lat\" : ");
                        template.addVariable("lat", new TextExpression(""), true);
                        template.addTextSegment("\n}");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getRadiusTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create(RADIUS_QUERY)
                .withTypeText("Distance/Radius-Based Geopoint Query")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - RADIUS_QUERY.length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + RADIUS_QUERY.length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate(RADIUS_QUERY, "JSON");
                    template.setToReformat(true);

                    boolean needsComma = false;

                    if (!existingKeys.contains("field")) {
                        template.addTextSegment("\"field\" : ");
                        template.addVariable("field", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("distance")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"distance\" : ");
                        template.addVariable("distance", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("location")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"location\": {");
                        template.addTextSegment("\"lon\" : ");
                        template.addVariable("lon", new TextExpression(""), true);
                        template.addTextSegment(",\n");
                        template.addTextSegment("\"lat\" : ");
                        template.addVariable("lat", new TextExpression(""), true);
                        template.addTextSegment("\n}");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }


    public static LookupElement getGenericTemplate(String key, String desc, List<String> attrs) {
        return LookupElementBuilder.create(key)
                .withTypeText(desc)
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - key.length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + key.length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate(key, "JSON");
                    template.setToReformat(true);

                    for (int i = 0; i < attrs.size(); i++) {

                        template.addTextSegment("\"" + attrs.get(i) + "\" : ");
                        template.addVariable(attrs.get(i), new TextExpression(""), true);
                        //isn't the last in the value
                        if (attrs.size() > 1 && i < attrs.size() - 1) {
                            template.addTextSegment(",\n");
                        }
                    }
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }


    public static LookupElement getQueryTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create("query")
                .withTypeText("Query string query syntax")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "query".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "query".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("query", "JSON");
                    template.setToReformat(true);

                    if (!existingKeys.contains("query")) {
                        template.addTextSegment("\"query\" : \"");
                        template.addVariable("query", new TextExpression(""), true);
                        template.addTextSegment("\"");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }


    public static LookupElement getConjunctsTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create("conjuncts")
                .withTypeText("Query conjunction")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "conjuncts".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "conjuncts".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("conjuncts", "JSON");
                    template.setToReformat(true);

                    if (!existingKeys.contains("conjuncts")) {
                        template.addTextSegment("\"conjuncts\" : [ \n {\n");
                        template.addVariable("conjuncts", new TextExpression(""), true);
                        template.addTextSegment("\n}\n]");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getDisjunctsTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create("disjuncts")
                .withTypeText("Query disjunction")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "disjuncts".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "disjuncts".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("disjuncts", "JSON");
                    template.setToReformat(true);

                    if (!existingKeys.contains("disjuncts")) {
                        template.addTextSegment("\"disjuncts\" : [ \n {\n");
                        template.addVariable("disjuncts", new TextExpression(""), true);
                        template.addTextSegment("\n}\n]");
                    }

                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }


    public static LookupElement getHighlightTemplate() {
        return LookupElementBuilder.create("highlight")
                .withTypeText("Highlight Options")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "highlight".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "highlight".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("highlight", "JSON");
                    template.setToReformat(true);

                    template.addTextSegment("\"highlight\" : {\n");
                    template.addTextSegment("\"style\" : \n");
                    template.addVariable("style", new TextExpression(""), true);
                    template.addTextSegment(",\n");
                    template.addTextSegment("\"fields\" : [\n");
                    template.addVariable("fields", new TextExpression(""), true);
                    template.addTextSegment("]\n}");
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getKNNTemplate() {
        return LookupElementBuilder.create("knn")
                .withTypeText("Knn filter")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "knn".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "knn".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("knn", "JSON");
                    template.setToReformat(true);

                    template.addTextSegment("\"knn\" : [\n");
                    template.addTextSegment("{\n");
                    template.addTextSegment("\"k\" : ");
                    template.addVariable("k", new TextExpression(""), true);
                    template.addTextSegment(",\n");
                    template.addTextSegment("\"field\" :");
                    template.addVariable("field", new TextExpression(""), true);
                    template.addTextSegment(",\n");
                    template.addTextSegment("\"vector\" : ");
                    template.addVariable("vector", new TextExpression(""), true);
                    template.addTextSegment("\n}");
                    template.addTextSegment("\n]");
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }

    public static LookupElement getVectorTemplate(List<String> existingKeys) {
        return LookupElementBuilder.create("vector_query")
                .withTypeText("Vector search filter")
                .withBoldness(true)
                .withIcon(AllIcons.Json.Object)
                .withInsertHandler((context, item) -> {
                    Editor editor = context.getEditor();
                    Project project = context.getProject();

                    int startOffset = editor.getCaretModel().getOffset() - "vector_query".length();

                    Document document = editor.getDocument();
                    document.deleteString(startOffset, startOffset + "vector_query".length());

                    TemplateManager templateManager = TemplateManager.getInstance(project);
                    Template template = templateManager.createTemplate("vector_query", "JSON");
                    template.setToReformat(true);

                    boolean needsComma = false;

                    if (!existingKeys.contains("field")) {
                        template.addTextSegment("\"field\" : ");
                        template.addVariable("field", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("k")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"k\" : ");
                        template.addVariable("k", new TextExpression(""), true);
                        needsComma = true;
                    }

                    if (!existingKeys.contains("vector")) {
                        if (needsComma) {
                            template.addTextSegment(",\n");
                        }
                        template.addTextSegment("\"vector\" : ");
                        template.addVariable("vector", new TextExpression(""), true);
                    }
                    TemplateManager.getInstance(project).startTemplate(editor, template);
                });
    }
}
