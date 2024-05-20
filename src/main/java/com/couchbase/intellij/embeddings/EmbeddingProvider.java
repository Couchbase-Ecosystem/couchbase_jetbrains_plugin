package com.couchbase.intellij.embeddings;

import java.io.IOException;
import java.util.List;

public interface EmbeddingProvider {

    String getName();

    boolean isAvailable();

    List<String> listModels() throws IOException;

    String generateEmbedding(String model, String text) throws IOException;
}
