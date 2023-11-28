package com.couchbase.intellij.tree.iq.core;

@FunctionalInterface
public interface TextSubstitutor {
    TextSubstitutor NONE = (x -> x);

    String resolvePlaceholders(String text);

}
