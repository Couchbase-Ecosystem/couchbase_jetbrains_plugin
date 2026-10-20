package com.couchbase.intellij.embeddings;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers the offline model-list parsing of GoogleGeminiEmbeddingProvider. The HTTP base URL is hard-coded,
 * so network paths are not exercised; parseModelsFromResponse is private and reached via reflection.
 */
public class GoogleGeminiEmbeddingProviderParseTest {

    private final GoogleGeminiEmbeddingProvider provider = new GoogleGeminiEmbeddingProvider();

    @SuppressWarnings("unchecked")
    private List<String> parse(String body) throws Throwable {
        Method m = GoogleGeminiEmbeddingProvider.class.getDeclaredMethod("parseModelsFromResponse", String.class);
        m.setAccessible(true);
        try {
            return (List<String>) m.invoke(provider, body);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void keepsOnlyModelsSupportingEmbedContent() throws Throwable {
        String body = "{\"models\":["
                + "{\"name\":\"models/text-embedding-004\",\"supportedGenerationMethods\":[\"embedContent\"]},"
                + "{\"name\":\"models/gemini-pro\",\"supportedGenerationMethods\":[\"generateContent\",\"countTokens\"]},"
                + "{\"name\":\"models/embedding-001\",\"supportedGenerationMethods\":[\"embedContent\",\"batchEmbedContents\"]}"
                + "]}";

        List<String> models = parse(body);

        assertEquals(2, models.size());
        assertEquals("models/text-embedding-004", models.get(0));
        assertEquals("models/embedding-001", models.get(1));
    }

    @Test
    public void noEmbeddingModelsYieldsEmptyList() throws Throwable {
        String body = "{\"models\":[{\"name\":\"models/gemini-pro\",\"supportedGenerationMethods\":[\"generateContent\"]}]}";

        assertTrue(parse(body).isEmpty());
    }

    @Test
    public void emptyModelArrayYieldsEmptyList() throws Throwable {
        assertEquals(Collections.emptyList(), parse("{\"models\":[]}"));
    }

    @Test
    public void modelWithoutSupportedMethodsFails() {
        // NOTE: possible bug: a model entry lacking supportedGenerationMethods causes a NullPointerException
        // instead of being skipped.
        try {
            parse("{\"models\":[{\"name\":\"models/x\"}]}");
            fail("expected NullPointerException");
        } catch (Throwable expected) {
            assertTrue(expected instanceof NullPointerException);
        }
    }

    @Test
    public void malformedJsonFailsWithParseException() {
        try {
            parse("not json");
            fail("expected a JSON parse exception");
        } catch (Throwable expected) {
            assertTrue(expected instanceof com.fasterxml.jackson.core.JsonProcessingException);
        }
    }
}
