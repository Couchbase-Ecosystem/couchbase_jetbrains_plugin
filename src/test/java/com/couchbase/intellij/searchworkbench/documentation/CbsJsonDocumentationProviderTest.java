package com.couchbase.intellij.searchworkbench.documentation;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests: PSI nodes are Mockito mocks wired into the parent chain the provider walks.
 */
public class CbsJsonDocumentationProviderTest {

    private final CbsJsonDocumentationProvider provider = new CbsJsonDocumentationProvider();

    // ---- getUrlFor -------------------------------------------------------------------------------

    @Test
    public void topLevelQueryUrlPointsToQueryObject() {
        JsonProperty property = property("query", topLevelParent());
        assertEquals(List.of("https://docs.couchbase.com/server/current/search/search-request-params.html#query-object"),
                provider.getUrlFor(property, null));
    }

    @Test
    public void nestedQueryUrlPointsToQueryStringSyntax() {
        JsonProperty outer = property("query", topLevelParent());
        JsonProperty inner = property("query", propertyParent(outer));
        assertEquals(List.of("https://docs.couchbase.com/server/current/search/search-request-params.html#query-string-query-syntax"),
                provider.getUrlFor(inner, null));
    }

    @Test
    public void knnAliasesShareOneUrl() {
        String expected = "https://docs.couchbase.com/server/current/search/search-request-params.html#knn-object";
        for (String key : List.of("knn", "k", "vector")) {
            assertEquals(key, List.of(expected), provider.getUrlFor(property(key, topLevelParent()), null));
        }
    }

    @Test
    public void fieldsUrlDependsOnParentType() {
        JsonProperty highlightFields = property("fields", propertyParent(property("highlight", topLevelParent())));
        JsonProperty plainFields = property("fields", propertyParent(property("query", topLevelParent())));

        assertEquals(List.of("https://docs.couchbase.com/server/current/search/search-request-params.html#highlight"),
                provider.getUrlFor(highlightFields, null));
        assertEquals(List.of("https://docs.couchbase.com/server/current/search/search-request-params.html"),
                provider.getUrlFor(plainFields, null));
    }

    @Test
    public void arrayElementPropertyResolvesTypeThroughArray() {
        // property -> JsonObject -> JsonArray -> JsonProperty("geometries")
        JsonProperty arrayOwner = property("geometries", topLevelParent());
        JsonArray array = mock(JsonArray.class);
        when(array.getParent()).thenReturn(arrayOwner);
        JsonObject element = mock(JsonObject.class);
        when(element.getParent()).thenReturn(array);
        JsonProperty property = property("relation", element);

        assertEquals(List.of("https://docs.couchbase.com/server/current/search/search-request-params.html#geojson-queries-point"),
                provider.getUrlFor(property, null));
    }

    @Test
    public void unknownKeyReturnsNull() {
        assertNull(provider.getUrlFor(property("not_a_real_key", topLevelParent()), null));
    }

    @Test
    public void nonPropertyElementReturnsNull() {
        assertNull(provider.getUrlFor(mock(PsiElement.class), null));
    }

    @Test(expected = org.apache.commons.lang3.NotImplementedException.class)
    public void unsupportedParentShapeThrows() {
        // NOTE: possible bug: a property whose grandparent is neither file, property nor array throws
        // instead of returning null, which surfaces as an error in the documentation popup.
        JsonObject parent = mock(JsonObject.class);
        when(parent.getParent()).thenReturn(mock(PsiElement.class));
        provider.getUrlFor(property("query", parent), null);
    }

    // ---- generateDoc -----------------------------------------------------------------------------

    @Test
    public void generateDocReturnsHtmlForKnownKeyInCbsJsonFile() {
        JsonProperty property = property("must", topLevelParent());
        doReturn(file("index.cbs.json")).when(property).getContainingFile();

        String doc = provider.generateDoc(property, null);

        assertNotNull(doc);
        assertFalse(doc.isEmpty());
    }

    @Test
    public void generateDocIgnoresFilesWithOtherExtensions() {
        JsonProperty property = property("must", topLevelParent());
        doReturn(file("index.json")).when(property).getContainingFile();

        assertNull(provider.generateDoc(property, null));
    }

    @Test
    public void generateDocIgnoresNonPropertyElements() {
        PsiElement element = mock(PsiElement.class);
        doReturn(file("index.cbs.json")).when(element).getContainingFile();

        assertNull(provider.generateDoc(element, null));
    }

    @Test
    public void generateDocFallsBackToPlaceholderForUnknownKey() {
        JsonProperty property = property("unknown_key", topLevelParent());
        doReturn(file("index.cbs.json")).when(property).getContainingFile();

        assertEquals("No documentation available for this key.", provider.generateDoc(property, null));
    }

    // ---- helpers ---------------------------------------------------------------------------------

    private static JsonProperty property(String name, PsiElement parent) {
        JsonProperty property = mock(JsonProperty.class);
        when(property.getName()).thenReturn(name);
        when(property.getParent()).thenReturn(parent);
        return property;
    }

    /** A JsonObject sitting directly under the file root. */
    private static PsiElement topLevelParent() {
        JsonObject object = mock(JsonObject.class);
        when(object.getParent()).thenReturn(mock(JsonFile.class));
        return object;
    }

    /** A JsonObject nested under the given property. */
    private static PsiElement propertyParent(JsonProperty owner) {
        JsonObject object = mock(JsonObject.class);
        when(object.getParent()).thenReturn(owner);
        return object;
    }

    private static PsiFile file(String name) {
        PsiFile file = mock(PsiFile.class);
        when(file.getName()).thenReturn(name);
        return file;
    }
}
