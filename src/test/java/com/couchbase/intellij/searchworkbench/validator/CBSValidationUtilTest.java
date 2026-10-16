package com.couchbase.intellij.searchworkbench.validator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.json.psi.JsonObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CBSValidationUtilTest {

    @Test
    public void messageTemplatesAreStable() {
        assertEquals("Unexpected attribute 'foo' under 'geometry' object",
                CBSValidationUtil.getUnexpectedAttUnder("foo", "geometry"));
        assertEquals("The attribute 'a' must not appear more than once under 'query'",
                CBSValidationUtil.singleOptionalKeyOccurrenceMessage("a", "query"));
        assertEquals("The attribute 'a' must appear once under 'query'",
                CBSValidationUtil.singleRequiredKeyOccurrenceMessage("a", "query"));
        assertEquals("The attribute 'a' must not appear more than once for this query type",
                CBSValidationUtil.singleOptionalKeyOccurrenceMessage("a"));
        assertEquals("Unexpected attribute 'x' for a 'geometry' query",
                CBSValidationUtil.getUnexpectedAttForQuery("x", "geometry"));
    }

    @Test
    public void singleMissingFieldUsesSingularMessage() {
        assertEquals("The attribute 'shape' is expected for a 'geometry' query",
                CBSValidationUtil.getMissingAttForQueryMessage("geometry", List.of("shape")));
    }

    @Test
    public void multipleMissingFieldsAreJoinedWithCommas() {
        assertEquals("The attributes shape, relation are expected for a 'geometry' query",
                CBSValidationUtil.getMissingAttForQueryMessage("geometry", Arrays.asList("shape", "relation")));
    }

    @Test
    public void emptyMissingListProducesPluralWordingWithNothingListed() {
        // Callers never pass an empty list, but pin the current behavior.
        assertEquals("The attributes  are expected for a 'geometry' query",
                CBSValidationUtil.getMissingAttForQueryMessage("geometry", Collections.emptyList()));
    }

    @Test
    public void validateMultipleOccurrencesReportsOnlyKeysSeenMoreThanOnce() {
        Map<String, Integer> counter = new LinkedHashMap<>();
        counter.put("once", 1);
        counter.put("twice", 2);
        counter.put("zero", 0);

        JsonObject object = mock(JsonObject.class);
        ProblemsHolder holder = mock(ProblemsHolder.class);

        CBSValidationUtil.validateMultipleOccurrences(counter, object, holder);

        verify(holder, times(1)).registerProblem(eq(object),
                eq("The attribute 'twice' must not appear more than once for this query type"),
                eq(ProblemHighlightType.GENERIC_ERROR));
        verify(holder, times(1)).registerProblem(any(JsonObject.class), anyString(), any(ProblemHighlightType.class));
    }

    @Test
    public void validateMultipleOccurrencesWithEmptyCounterReportsNothing() {
        JsonObject object = mock(JsonObject.class);
        ProblemsHolder holder = mock(ProblemsHolder.class);

        CBSValidationUtil.validateMultipleOccurrences(Collections.emptyMap(), object, holder);

        verifyNoInteractions(holder);
    }

    @Test
    public void geometryRelationErrorMessageListsAllowedValues() {
        assertEquals("'relation' can only be 'intersects', 'contains' or 'within'",
                GeometryObjectValidator.getInvalidRelationValue());
    }
}
