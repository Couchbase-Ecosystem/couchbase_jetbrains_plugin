package com.couchbase.intellij.searchworkbench.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CBSTemplatesTest {

    @Test
    public void geoTemplatesUseExpectedLookupKeysAndDescriptions() {
        List<String> none = List.of();

        assertPresentation(CBSTemplates.getPointGeoJsonTemplate(none), "point_geo_query", "Point GeoJSON Query");
        assertPresentation(CBSTemplates.getLineStringTemplate(none), "linestring_geo_query", "LineString GeoJSON Query");
        assertPresentation(CBSTemplates.getPolygonTemplate(none), "polygon_geo_query", "Polygon GeoJSON Query");
        assertPresentation(CBSTemplates.getMultiPointTemplate(none), "multi_point_geo_query", "MultiPoint GeoJSON Query");
        assertPresentation(CBSTemplates.getMultiLineStringTemplate(none), "multi_linestring_geo_query", "MultiLineString GeoJSON Query");
        assertPresentation(CBSTemplates.getMultiPolygonTemplate(none), "multi_polygon_geo_query", "MultiPolygon GeoJSON Query");
        assertPresentation(CBSTemplates.getEnvelopeTemplate(none), "envelope_geo_query", "Envelope GeoJSON Query");
        assertPresentation(CBSTemplates.getCircleTemplate(none), "circle_geo_query", "Circle GeoJSON Query");
        assertPresentation(CBSTemplates.getGeometryCollectionTemplate(none), "geometry_col_geo_query", "Geometry Collection GeoJSON Query");
    }

    @Test
    public void constantKeysMatchTheirTemplates() {
        assertEquals("point_geo_query", CBSTemplates.POINT_GEO_QUERY);
        assertEquals("linestring_geo_query", CBSTemplates.LINESTRING_GEO_QUERY);
        assertEquals("polygon_geo_query", CBSTemplates.POLYGON_GEO_QUERY);
        assertEquals("rectangle_query", CBSTemplates.RECTANGLE_QUERY);
        assertEquals("radius_query", CBSTemplates.RADIUS_QUERY);
        assertEquals(CBSTemplates.POINT_GEO_QUERY,
                CBSTemplates.getPointGeoJsonTemplate(List.of()).getLookupString());
    }

    @Test
    public void booleanAndStructuralTemplatesHaveFixedKeys() {
        assertEquals("must", CBSTemplates.getMustTemplate().getLookupString());
        assertEquals("should", CBSTemplates.getShouldTemplate().getLookupString());
        assertEquals("must_not", CBSTemplates.getMustNotTemplate().getLookupString());
        assertEquals("conjuncts", CBSTemplates.getConjunctsTemplate(List.of()).getLookupString());
        assertEquals("disjuncts", CBSTemplates.getDisjunctsTemplate(List.of()).getLookupString());
        assertEquals("highlight", CBSTemplates.getHighlightTemplate().getLookupString());
        assertEquals("knn", CBSTemplates.getKNNTemplate().getLookupString());
        assertEquals("vector_query", CBSTemplates.getVectorTemplate(List.of()).getLookupString());
        assertEquals("query", CBSTemplates.getQueryTemplate(List.of()).getLookupString());
        assertEquals(CBSTemplates.RECTANGLE_QUERY, CBSTemplates.getRectangleTemplate(List.of()).getLookupString());
        assertEquals(CBSTemplates.RADIUS_QUERY, CBSTemplates.getRadiusTemplate(List.of()).getLookupString());
    }

    @Test
    public void genericAndEmptyTemplatesUseGivenKeyAndDescription() {
        assertPresentation(CBSTemplates.getGenericTemplate("my_key", "My Description", List.of("a", "b")),
                "my_key", "My Description");
        assertPresentation(CBSTemplates.getEmptyTemplate("empty_one", "Empty Description"),
                "empty_one", "Empty Description");
    }

    @Test
    public void geoTemplateKeepsExistingKeysOutOfInsertHandlerPlan() {
        // Lookup identity does not depend on existing keys; the insert handler (not invoked here)
        // is what skips "field"/"geometry". Pin that the key list does not change the lookup string.
        LookupElement withKeys = CBSTemplates.getPointGeoJsonTemplate(List.of("field", "geometry"));
        LookupElement withoutKeys = CBSTemplates.getPointGeoJsonTemplate(List.of());
        assertEquals(withoutKeys.getLookupString(), withKeys.getLookupString());
    }

    private static void assertPresentation(LookupElement element, String key, String typeText) {
        assertEquals(key, element.getLookupString());
        LookupElementPresentation presentation = new LookupElementPresentation();
        element.renderElement(presentation);
        assertEquals(typeText, presentation.getTypeText());
    }
}
